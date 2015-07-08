package replication.prototype.server.core;

import replication.prototype.server.messages.M;
import replication.prototype.server.messages.M.Command;

/**
 * The command receiver creates the actual input stream (e.g. TCP socket stream) that eventually contain commands. Implementations have to
 * provide a handler method that is able to handle that command in the desired way.
 * 
 */
public interface ICommandReceiver {

  /**
   * Handles a received command in a desired manner (e.g. propagating it to the replicas)
   * @param command the command to be handled
   * @return
   */
  public M.Response handleCommand(Command command);

  /**
   * (Creates and) Listens on the input stream for incoming commands.
   * This method loops indefinitely
   */
  public void startListening() throws Exception;
  
  


}
