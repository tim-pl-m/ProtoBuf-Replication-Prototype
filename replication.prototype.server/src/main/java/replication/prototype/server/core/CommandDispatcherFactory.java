package replication.prototype.server.core;

import java.io.IOException;

import replication.prototype.server.environment.QuorumReplicationLinkType;
import replication.prototype.server.environment.ReplicationLinkType;
import replication.prototype.server.util.ReplicationConfigAccessor;

/**
 * Reference implementation that is able to create command dispatchers communicating via TCP sockets.
 *
 */
public class CommandDispatcherFactory implements ICommandDispatcherFactory {

	private ReplicationConfigAccessor configAccessor;
	private IConnectionManager connectionManager;
	
	public CommandDispatcherFactory(IConnectionManager connectionManager, ReplicationConfigAccessor configAccessor)
	{
		this.configAccessor = configAccessor;
		this.connectionManager = connectionManager;
	}
	
	@Override
	public ICommandDispatcher createSyncDispatcher(ReplicationLinkType repLink) throws IOException {
		return new SyncCommandDispatcher(this.connectionManager, repLink, this.configAccessor);
	}

	@Override
	public ICommandDispatcher createAsyncDispatcher(ReplicationLinkType repLink) throws IOException {
		return new AsyncCommandDispatcher(this.connectionManager, repLink, this.configAccessor);
	}

	@Override
	public ICommandDispatcher createQuorumDispatcher(QuorumReplicationLinkType repLink) {
		return new QuorumCommandDispatcher(this.connectionManager, repLink, this.configAccessor);
	}


}
