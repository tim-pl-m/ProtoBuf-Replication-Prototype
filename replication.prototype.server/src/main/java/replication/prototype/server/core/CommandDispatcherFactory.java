package replication.prototype.server.core;

import replication.prototype.server.environment.QuorumReplicationLinkType;
import replication.prototype.server.environment.ReplicationLinkType;
import replication.prototype.server.util.ReplicationConfigAccessor;

/**
 * Reference implementation that is able to create command dispatchers communicating via TCP sockets.
 *
 */
public class CommandDispatcherFactory implements ICommandDispatcherFactory {

	private ReplicationConfigAccessor configAccessor;
	
	public CommandDispatcherFactory(ReplicationConfigAccessor configAccessor)
	{
		this.configAccessor = configAccessor;
	}
	
	@Override
	public ICommandDispatcher createSyncDispatcher(ReplicationLinkType repLink) {
		return new SyncCommandDispatcher(repLink, this.configAccessor);
	}

	@Override
	public ICommandDispatcher createAsyncDispatcher(ReplicationLinkType repLink) {
		return new AsyncCommandDispatcher(repLink, this.configAccessor);
	}

	@Override
	public ICommandDispatcher createQuorumDispatcher(QuorumReplicationLinkType repLink) {
		return new QuorumCommandDispatcher(repLink, this.configAccessor);
	}


}
