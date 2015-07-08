package replication.prototype.server.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import replication.prototype.server.Server;
import replication.prototype.server.environment.NodeType;
import replication.prototype.server.environment.ReplicationLinkType;
import replication.prototype.server.messages.M;
import replication.prototype.server.messages.M.Command;
import replication.prototype.server.messages.M.OperationType;
import replication.prototype.server.messages.M.Response;
import replication.prototype.server.util.CommitLogEventCreator;

/**
 * The reference implementation of {@link replication.prototype.server.core.ICommandReceiver
 * ICommandReceiver} that receives the protobuff messages using TCP socket streams.
 *
 */
public class CommandReceiver implements ICommandReceiver {

  private Server currentServer;
  private ServerSocket socket;
  private Map<String, String> map;
  private NodeType thisNode;

  static final Marker COMMIT_MARKER = MarkerManager.getMarker("COMMIT");
  static final Logger logger = LogManager.getLogger(CommandReceiver.class.getName());
  private CommitLogEventCreator commitLogEventCreator;

  public CommandReceiver(NodeType thisNode, Map<String, String> map, Server currentServer)
      throws IOException, JAXBException {
    this.thisNode = thisNode;
    this.socket = new ServerSocket(thisNode.getPort());
    this.map = map;
    this.currentServer = currentServer;
    this.commitLogEventCreator = new CommitLogEventCreator();
  }

  /**
   * The read command is handled locally. The method accesses our key-value-store (hashmap) and
   * checks if there is a value for the given key.
   * 
   * @param command the read command
   * @return a response containing the value found or null
   */
  private M.Response handleReadCommand(Command command) {
    String value = this.map.get(command.getKey());
    M.Response.Builder builder = M.Response.newBuilder();
    M.Ack.Builder ackBuilder = M.Ack.newBuilder();
    builder.setOperation(OperationType.READ);

    if (value != null) {
      ackBuilder.setResult(true);
      builder.setValue(value);
    } else {
      ackBuilder.setResult(false);
      ackBuilder.setMessage("There is no value for that key");
      builder.setValue("null");
    }

    builder.setA(ackBuilder);
    M.Response response = builder.build();
    return response;
  }

  @Override
  public M.Response handleCommand(Command command) {

    if (!command.getOperation().equals(OperationType.READ)) {
      this.map.put(command.getKey(), (command.hasValue()) ? command.getValue() : null);

      logger.info(
          COMMIT_MARKER,
          this.commitLogEventCreator.createCommitLogEvent(command, this.currentServer,
              this.currentServer.getTimeOffset()).toCsv());

      logger.debug("Command is sent to the coordinator");

      CommandCoordinator coordinator =
          new CommandCoordinator(this.getRelevantReplicationLinks(command), thisNode,
              new CommandDispatcherFactory(this.currentServer.getConfigAccessor()));
      coordinator.coordinateCommand(command);

      // create a simple response, will be changed later...
      M.Response.Builder builder = M.Response.newBuilder();
      M.Ack.Builder ackBuilder = M.Ack.newBuilder();
      builder.setOperation(command.getOperation());
      ackBuilder.setResult(true);
      builder.setA(ackBuilder);
      return builder.build();
    } else {
      return this.handleReadCommand(command);
    }

  }

  /**
   * Not every replication link is has to be processed in this step. The method determines which
   * replication path has to be used first. If the command contains a reference to an initiator
   * (true if another node has propagated the command), the replication path of that node will be
   * used. Otherwise the replication path of this node will be used.
   * 
   * The second step is to filter the containing replication links for those that start with his
   * node.
   * 
   * @param command the relevant replication links for that command
   * @return
   */
  private List<ReplicationLinkType> getRelevantReplicationLinks(Command command) {

    if (command.hasInitiator()) {
      logger.debug("As this command has the initiator {}. Will wil use it's replication path.",
          command.getInitiator());
      // 1. find the replication path 2. filter the containing replication links
      return this.currentServer.getConfig().getReplicationpaths().getPath().stream()
          .filter(r -> r.getStart().equals(command.getInitiator())).findAny().get().getLink()
          .stream().filter(t -> t.getSrc().equals(this.thisNode.getLabel()))
          .collect(Collectors.toList());
    } else {
      logger.debug("Our own replication path will be used.");
      return this.currentServer.getReplicationPath().getLink().stream()
          .filter(t -> t.getSrc().equals(this.thisNode.getLabel())).collect(Collectors.toList());
    }
  }

  @Override
  public void startListening() {
    boolean canShutdown = false;

    // outer loop for creating socket connections to several clients
    while (true && !canShutdown) {

      try (Socket connectionSocket = this.socket.accept()) {

        logger.debug("Socket accepted - new connection");

        Runnable threadForSocketConnection = new Runnable() {

          Socket connectionSocketInner = null;
          InputStream iStream = null;
          OutputStream oStream = null;
          Command command = null;

          // instance initializer
          // is important, because the connectionSocket variable must not be overriden meanwhile
          {
            connectionSocketInner = connectionSocket;
            iStream = connectionSocket.getInputStream();
            oStream = connectionSocket.getOutputStream();
          }

          @Override
          public void run() {
            boolean canShutdownIn = false;

            // loop for reading from proto buffer
            while (true && !canShutdownIn) {
              try {

                command = Command.parseDelimitedFrom(iStream);
                if (command != null) {

                  logger.debug("New command found: {}", command.toString());
                  Response resp = CommandReceiver.this.handleCommand(command);
                  logger.debug("Will now respond to requester: {} ", resp.toString());

                  resp.writeDelimitedTo(oStream);
                  logger.debug("Response successfully sent to the requester.");
                }

                if (command == null) {
                  logger.debug("Closing Socket because connection has died.");
                  canShutdownIn = true;
                  // connection has dies
                  this.connectionSocketInner.close();
                  // if has received shutdown hook, the specific connection via sockets will be
                  // closed
                }

              } catch (IOException e) {
                canShutdownIn = true;

                logger
                    .error("Exception appeared while reading/writing command. Exception message is: "
                        + e.getMessage());
                try {
                  if (connectionSocketInner != null && !connectionSocketInner.isClosed()) {
                    logger.debug("Closing Socket because of Exception");
                    connectionSocketInner.close();

                  }
                } catch (IOException e1) {
                  logger.error("Unable to close socket.");
                }

              }

            }
          }
        };

        Thread tfsc = new Thread(threadForSocketConnection);
        tfsc.run();


      } catch (IOException e1) {
        logger.error("Error accepting socket.");
        canShutdown = true;

      }
    }

  }


}
