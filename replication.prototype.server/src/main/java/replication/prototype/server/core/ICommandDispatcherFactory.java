package replication.prototype.server.core;

import replication.prototype.server.environment.QuorumReplicationLinkType;
import replication.prototype.server.environment.ReplicationLinkType;

/**
 * Implementations deal with the internals of creating synchronous, asynchronous and quorum-mode
 * command dispatchers @see {@link replication.prototype.server.core.ICommandDispatcher
 * ICommandDispatcher}.
 *
 */
public interface ICommandDispatcherFactory {

  /**
   * Will create a synchronous command dispatcher
   * 
   * @param the replication link used to dispatch the command
   * @return a synchronous command dispatcher
   */
  public ICommandDispatcher createSyncDispatcher(ReplicationLinkType repLink);

  /**
   * Will create an asynchronous command dispatcher
   * 
   * @param the replication link used to dispatch the command
   * @return an asynchronous command dispatcher
   */
  public ICommandDispatcher createAsyncDispatcher(ReplicationLinkType repLink);

  /**
   * Will create a command dispatcher for the quorum mode. @see QuorumCommandDispatcher {@link replication.prototype.server.core.QuorumCommandDispatcher QuorumCommandDispatcher} for the reference implementation.
   * 
   * @param the replication link used to dispatch the command
   * @return a synchronous command dispatcher
   */
  public ICommandDispatcher createQuorumDispatcher(QuorumReplicationLinkType repLink);


}
