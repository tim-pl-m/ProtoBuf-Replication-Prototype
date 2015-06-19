package replication.prototype.server.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import replication.prototype.server.Server;
import replication.prototype.server.environment.NodeType;
import replication.prototype.server.environment.ReplicationLinkType;
import replication.prototype.server.messages.M;
import replication.prototype.server.messages.M.Command;
import replication.prototype.server.messages.M.OperationType;
import replication.prototype.server.messages.M.Response;

/**
 * The reference implementation of
 * {@link replication.prototype.server.core.ICommandReceiver ICommandReceiver}
 * that receives the protobuff messages using TCP socket streams.
 *
 */
public class CommandReceiver implements ICommandReceiver {

	private Server currentServer;
	private ServerSocket socket;
	private Map<String, String> map;
	private NodeType thisNode;
	private List<ReplicationLinkType> thisReplicationLinks;
	static final Logger logger = LogManager.getLogger(CommandReceiver.class
			.getName());
	private boolean shutdownHook = false;

	public CommandReceiver(NodeType thisNode, Map<String, String> map,
			Server currentServer) throws IOException {
		this.thisNode = thisNode;
		this.thisReplicationLinks = currentServer.getReplicationPath()
				.getLink();
		this.socket = new ServerSocket(thisNode.getPort());
		this.map = map;
		this.currentServer = currentServer;
	}

	/**
	 * The read command is handled locally. The method accesses our
	 * key-value-store (hashmap) and checks if there is a value for the given
	 * key.
	 * 
	 * @param command
	 *            the read command
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
			this.map.put(command.getKey(),
					(command.hasValue()) ? command.getValue() : null);
			logger.debug("Command is sent to the coordinator");

			CommandCoordinator coordinator = new CommandCoordinator(
					this.getRelevantReplicationLinks(command), thisNode,
					new CommandDispatcherFactory(
							this.currentServer.getConfigAccessor()));
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
	 * Not every replication link is has to be processed in this step. The
	 * method determines which replication path has to be used first. If the
	 * command contains a reference to an initiator (true if another node has
	 * propagated the command), the replication path of that node will be used.
	 * Otherwise the replication path of this node will be used.
	 * 
	 * The second step is to filter the containing replication links for those
	 * that start with his node.
	 * 
	 * @param command
	 *            the relevant replication links for that command
	 * @return
	 */
	private List<ReplicationLinkType> getRelevantReplicationLinks(
			Command command) {

		if (command.hasInitiator()) {
			logger.debug(
					"As this command has the initiator {}. Will wil use it's replication path.",
					command.getInitiator());
			// 1. find the replication path 2. filter the containing replication
			// links
			return this.currentServer.getConfig().getReplicationpaths()
					.getPath().stream()
					.filter(r -> r.getStart().equals(command.getInitiator()))
					.findAny().get().getLink().stream()
					.filter(t -> t.getSrc().equals(this.thisNode.getLabel()))
					.collect(Collectors.toList());
		} else {
			logger.debug("Our own replication path will be used.");
			return this.thisReplicationLinks.stream()
					.filter(t -> t.getSrc().equals(this.thisNode.getLabel()))
					.collect(Collectors.toList());
		}
	}

	@Override
	public void startListening() {
		Command command = null;
		InputStream iStream = null;
		boolean canShutdown = false;
		try (Socket connectionSocket = this.socket.accept()) {

			logger.debug("Socket accepted");

			// read commmands until shutdown command from springBoot-Controller
			while (true && !canShutdown) {

				try {

					iStream = connectionSocket.getInputStream();
					command = Command.parseDelimitedFrom(iStream);

				} catch (IOException e) {
					logger.error("Exception appeared while reading request command. Exception message is: "
							+ e.getMessage());
				}
				if (command != null) {
					try {

						logger.debug("New command found: " + command.toString());

						Response resp = this.handleCommand(command);
						// the same command is just written back to the output,
						// this will be improved by adding
						// an acknowledgment
						logger.debug("Will now respond to requester: "
								+ resp.toString());

						resp.writeDelimitedTo(connectionSocket
								.getOutputStream());
						logger.debug("Response successfully sent to the requester.");

					} catch (IOException e) {
						logger.error("Exception appeared while responding: "
								+ e.getMessage());
					}
				}
				if (this.shutdownHook) {
					connectionSocket.close();
					this.socket.close();
					canShutdown = true;
				}

			}
		} catch (IOException e) {
			logger.error("Exception appeared while accepting socket. Message is: "
					+ e.getMessage());
		}

	}

	@Override
	public void setShutdownHook() {
		this.shutdownHook = true;
	}

}
