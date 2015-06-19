package replication.prototype.server.core;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import replication.prototype.server.environment.ReplicationLinkType;
import replication.prototype.server.messages.M.Command;
import replication.prototype.server.messages.M.Response;
import replication.prototype.server.util.ReplicationConfigAccessor;

/**
 * This class implements the asynchronous replication strategy. It dispatches
 * received commands asynchronously by using the synchronous variant in a
 * thread.
 */
public class AsyncCommandDispatcher implements ICommandDispatcher {

	private SyncCommandDispatcher syncCommandDispatcher;
	private ReplicationLinkType repLink;

	static final Logger logger = LogManager
			.getLogger(AsyncCommandDispatcher.class.getName());

	public AsyncCommandDispatcher(ReplicationLinkType repLink,
			ReplicationConfigAccessor configAccessor) {
		this.repLink = repLink;
		syncCommandDispatcher = new SyncCommandDispatcher(repLink,
				configAccessor);
	}

	@Override
	public void dispatchCommand(Command command) throws IOException {

		// the asynchronous implementation simply uses a synchronous command
		// dispatcher and
		// invokes it's dispatching functionality within a thread
		
		// alternative: create new class
		// Runnable task = new Thread() ... {
		
		// use thread in lambda-notifaction to allow to receive ack without
		// waiting for it
		Runnable task = () -> {
			try {
				AsyncCommandDispatcher.this.syncCommandDispatcher
						.dispatchCommand(command);
			} catch (Exception e) {
				AsyncCommandDispatcher.logger
						.info("Error while dispatching command asynchrnously from '"
								+ this.repLink.getSrc()
								+ "' to '"
								+ this.repLink.getTarget() + "' ");
			}

		};

		new Thread(task).start();

	}

	@Override
	public Response getResponse() {
		return syncCommandDispatcher.getResponse();
	}

	@Override
	public Response[] getResponses() {
		// Not implemented yet
		return null;
	}

}
