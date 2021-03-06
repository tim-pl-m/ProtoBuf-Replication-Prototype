package replication.prototype.server.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import replication.prototype.server.environment.NodeType;
import replication.prototype.server.environment.ReplicationLinkType;
import replication.prototype.server.messages.M.Command;
import replication.prototype.server.messages.M.Response;
import replication.prototype.server.util.ReplicationConfigAccessor;

/**
 * Synchronous implementation of the ICommandDispatcher interface that streams the given command to a TCP output stream.
 * When a response is received, the used socket will be closed and the process of dispatching terminates.
 *
 */
public class SyncCommandDispatcher implements ICommandDispatcher {

	static final Logger logger = LogManager
			.getLogger(SyncCommandDispatcher.class.getName());

	/**
	 * Outputstream to replicating node
	 */
	private OutputStream oStream;

	/**
	 * InputStream from replicating node
	 */
	private InputStream iStream;

	private Response response = null;
	private Socket socket;
	private NodeType targetNode;

	private ReplicationLinkType repLink;

	public SyncCommandDispatcher(IConnectionManager connectionManager, ReplicationLinkType repLink,
			ReplicationConfigAccessor configAccessor) throws IOException {

		try {
			this.repLink = repLink;
			targetNode = configAccessor.getNodeByLabel(repLink.getTarget()
					.toString());
			this.socket = connectionManager.getSocketFor(targetNode.getIpadress(), targetNode.getPort());
			this.oStream = socket.getOutputStream();
			this.iStream = socket.getInputStream();

		} catch (IOException e) {
			logger.error("Error communicating with server with IP-Address "
					+ targetNode.getIpadress() + ".");
			throw e;
		}

	}

	@Override
	public void dispatchCommand(Command command) throws IOException {

		command.writeDelimitedTo(this.oStream);
		this.response = Response.parseDelimitedFrom(iStream);
		logger.debug("Received a response from {}", this.targetNode.getLabel());

	}

	@Override
	public Response getResponse() {
		return this.response;
	}

  @Override
  public Response[] getResponses() {
    // TODO Auto-generated method stub
    return null;
  }

}
