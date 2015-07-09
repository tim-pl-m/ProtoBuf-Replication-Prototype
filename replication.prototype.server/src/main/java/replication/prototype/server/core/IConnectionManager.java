package replication.prototype.server.core;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public interface IConnectionManager {
  
  public Socket getSocketFor(String address, int port)  throws UnknownHostException, IOException;

}
