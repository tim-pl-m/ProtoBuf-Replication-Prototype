package replication.prototype.server.core;

import replication.prototype.server.messages.M.Command;

/**
 * 
 * A command coordinator is responsible for coordinating the replication of (update-)commands to the dedicated command dispatchers.
 * The coordination is not only dependent on the the command but also on the server's identity.
 * @see {@link replication.prototype.server.core.ICommandDispatcher ICommandDispatcher}. 
 */
public interface ICommandCoordinator {
	
	/**
	 * Coordinates how incoming commands are handled. The method should determine, which CommandDispatcher @see {@link replication.prototype.server.core.ICommandDispatcher ICommandDispatcher}
	 * is capable of dispatching this command to the other instances using the required replication path strategy.
	 * @param command the command to  be coordinated
	 */
	public void coordinateCommand(Command command);

}
