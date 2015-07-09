package replication.prototype.server.core;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager implements IConnectionManager {

  private Map<String, Socket> sockets = new HashMap<String, Socket>();
  @Override
  public Socket getSocketFor(String address, int port) throws UnknownHostException, IOException {
    if(!this.sockets.containsKey(address+port))
      this.sockets.put(address+port,   new Socket(address, port)) ;
    
  
    return this.sockets.get(address+port);
  }

}
