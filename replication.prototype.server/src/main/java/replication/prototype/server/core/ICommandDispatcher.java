package replication.prototype.server.core;

import java.io.IOException;

import replication.prototype.server.messages.M.Command;
import replication.prototype.server.messages.M.Response;

public interface ICommandDispatcher {
	
	public void dispatchCommand(Command command) throws IOException;
	
	
	public Response getResponse();
	public Response[] getResponses();

	
	public default boolean hasResponse() {
		return this.getResponse() != null;
	}

}
