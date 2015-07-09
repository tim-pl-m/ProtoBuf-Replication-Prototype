package replication.prototype.server.core;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import replication.prototype.server.environment.CommunicationEnumType;
import replication.prototype.server.environment.NodeType;
import replication.prototype.server.environment.QuorumReplicationLinkType;
import replication.prototype.server.environment.ReplicationLinkType;
import replication.prototype.server.messages.M.Command;

/**
 * The reference implementation of {@link replication.prototype.server.core.ICommandCoordinator
 * ReplicationConfigurationType} coordinates the updates using
 *
 */
public class CommandCoordinator implements ICommandCoordinator {

  static final Logger logger = LogManager.getLogger(CommandCoordinator.class.getName());

  private List<ReplicationLinkType> replLinks;
  private NodeType thisNode;
  private ICommandDispatcherFactory dispatcherFactory;

  public CommandCoordinator(List<ReplicationLinkType> replLinks, NodeType thisNode,
      ICommandDispatcherFactory dispatcherFactory) {
    this.replLinks = replLinks;
    this.dispatcherFactory = dispatcherFactory;
    this.thisNode = thisNode;
  }

  public void coordinateCommand(Command command) {

    // Every entry of the replication path is (eventually) processed in parallel
    // the thread usage of parallelStream depends on the system environment and it's available
    // resources
    this.replLinks.parallelStream().forEach(new Consumer<ReplicationLinkType>() {

      @Override
      public void accept(ReplicationLinkType t) {

        // for quorums there is a dedicated type 'ReplicationLinkType' - logging behavior differs
        // Begin logging
        if (!(t instanceof QuorumReplicationLinkType)) {
          CommandCoordinator.logger.debug(
              "Task: The command is coordinated from Node='{}' to Node='{}' with mode '{}'.",
              CommandCoordinator.this.thisNode.getLabel(), t.getTarget(), t.getType().toString());
        } else {
          CommandCoordinator.logger.debug(
              "Task: The command is coordinated from Node='{}' to multiple nodes with mode '{}'.",
              CommandCoordinator.this.thisNode.getLabel(), t.getType().toString());
        }
        // End logging
        ICommandDispatcher dispatcher = null;
        try {

          // creation of the required command dispatchers...
          // for the synchronous strategy do...
          if (t.getType().compareTo(CommunicationEnumType.SYNC) == 0) {

            dispatcher = dispatcherFactory.createSyncDispatcher(t);
            logger.debug("Trying: Synchronously dispatching the following command: {}",
                command.toString());

            // for the asynchronous strategy do...
          } else if (t.getType().compareTo(CommunicationEnumType.ASYNC) == 0) {

            dispatcher = dispatcherFactory.createAsyncDispatcher(t);
            logger.debug("Trying: Asynchronously dispatching the following command: {}",
                command.toString());


          } else if (t.getType().compareTo(CommunicationEnumType.QUORUM) == 0) {

            dispatcher = dispatcherFactory.createQuorumDispatcher((QuorumReplicationLinkType) t);
            logger.debug(
                "Trying: Synchronously dispatching the following command: {} in quorum mode.",
                command.toString());

          }

          // the dispatchCommand method is provided by the general interface ICommandDispatcher and
          // can therefore be used independent from a specific implementation/strategy
          dispatcher.dispatchCommand(CommandCoordinator.this.rebuildCommand(command));


        } catch (IOException e) {

        }

      }

    });

  }

  /**
   * This method is a helper that simply creates a copy of the received command. If the command has
   * 
   * @param command the received command that has to be copied
   * @return a copy of the received command.
   */
  private Command rebuildCommand(Command command) {
    Command.Builder builder = Command.newBuilder();
    builder.setOperation(command.getOperation());
    builder.setKey(command.getKey());
    if (command.hasId()) {
      builder.setId(command.getId());

    }
    if (command.hasValue())
      builder.setValue(command.getValue());

    // extend the given command by the initiator id
    // if no id is given
    if (!command.hasInitiator()) {
      builder.setInitiator(this.thisNode.getLabel());
    } else {
      builder.setInitiator(command.getInitiator());
    }

    logger.debug("The received command will now be copied and modified. Is builder initiialized? "
        + builder.isInitialized());
    return builder.build();
  }

}
