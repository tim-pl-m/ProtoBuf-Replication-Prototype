package replication.prototype.server;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import replication.prototype.server.util.ReplicationConfigAccessor;
import replication.prototype.server.core.ConnectionManager;
import replication.prototype.server.core.ICommandReceiver;
import replication.prototype.server.core.IConnectionManager;
import replication.prototype.server.core.ServerIdentity;
import replication.prototype.server.core.CommandReceiver;
import replication.prototype.server.environment.ReplicationConfigurationType;
import replication.prototype.server.environment.ReplicationPathType;
import replication.prototype.server.environment.UnknownIdentityException;

/**
 * Objects of this class represent a single node in the replication cluster. To create an instance,
 * it's required to pass a configuration object of type
 * {@link replication.prototype.server.environment.ReplicationConfigurationType
 * ReplicationConfigurationType} and the server's identifying label. The configuration object holds
 * information about all nodes in in the cluster (IP addresses, TCP ports to listen to etc.). After
 * instantiation the server must be booted by calling
 * {@link replication.prototype.server.Server#boot() serverObj.boot();} to allow it to listen to the
 * specified port for incomming commands.
 *
 * Additionally the server creates a synchronized hashmap that will store the key-value-pais.
 */
public class Server {

  static final Logger logger = LogManager.getLogger(Server.class.getName());

  private IConnectionManager connectionManager = new ConnectionManager();
  private ReplicationConfigurationType config;
  private ServerIdentity identity;
  private ReplicationConfigAccessor configAccessor;
  private boolean hasBooted = false;
  private Map<String, String> synchrnoizedMap;
  private ReplicationPathType thisReplicationPath;
  private String nodeLabel;
  private ICommandReceiver receiver;
  private Thread receiverThread;
  private long timeOffset = 0;
  
  public Server(ReplicationConfigurationType config, String nodeLabel, long timeOffset) {
    this.config = config;
    this.nodeLabel = nodeLabel;
    this.timeOffset = timeOffset;
  }

  /**
   * Coordinates the creation of required resources
   * 
   * @throws UnknownIdentityException will be thrown if there is no replication path found for the
   *         specified node
   * @throws IOException will be thrown, if socket communication fails
   * @throws JAXBException 
   */
  public void boot() throws UnknownIdentityException, IOException, JAXBException {
    this.determineServersIdentity();
    // depends on server's identity and availability of config objects --> don't change order
    this.configAccessor =
        new ReplicationConfigAccessor(this.config, this.identity.getServerIdentity());
    this.buildSynchrnoizedMap();
    this.determineServersReplicationPath();
    this.buildAndRunCommandReceiver();
    this.hasBooted = true;
  }
  
  public void rebootWithNewConfig()
  {
    this.configAccessor =
        new ReplicationConfigAccessor(this.config, this.identity.getServerIdentity());
    this.determineServersReplicationPath();
  }

  /**
   * The method is part of the booting process and creates a synchronized map hat will be used to
   * store the key-value-pairs.
   */
  private void buildSynchrnoizedMap() {
    this.synchrnoizedMap = Collections.synchronizedMap(new HashMap<String, String>());
  }

  /**
   * The server's identity will be determined using the node label, specified as constructor
   * parameter
   * 
   * @throws UnknownIdentityException if there is no configuration for the specified node
   */
  private void determineServersIdentity() throws UnknownIdentityException {
    this.identity = new ServerIdentity(this.config, this.nodeLabel);
  }

  /**
   * The configuration contains a replication path definition for every node. This method finds and
   * returns the replication path object responsible for this node.
   * 
   * This method must not be called before this server's identity has been analyzed via
   * {@link replication.prototype.server.Server#determineServersReplicationPath()
   * determineServersReplicationPath()}
   */
  private void determineServersReplicationPath() {
    this.thisReplicationPath =
        this.getConfigAccessor().getPathByNode(this.identity.getServerIdentity());
  }


  /**
   * This method is responsible for creating the command receiver for this server @see
   * {@link replication.prototype.server.core.ICommandReceiver ICommandReceiver}. The command
   * receiver processes incoming commands according to the respective replication path.
   * @throws JAXBException 
   * @throws IOException 
   * @throws Exception 
   */
  private void buildAndRunCommandReceiver() throws IOException, JAXBException {
    this.receiver =
        new CommandReceiver(connectionManager, this.identity.getServerIdentity(), this.synchrnoizedMap, this);

    Runnable task = () -> {
      try {
        receiver.startListening();
      } catch (Exception e) {
        logger.fatal(e);
      }
    };

    this.receiverThread = new Thread(task);
    this.receiverThread.start();
  }

  /**
   * 
   * @return the configuration passed as constructor parameter
   */
  public ReplicationConfigurationType getConfig() {
    return config;
  }
  
  /**
   * 
   * @return the configuration passed as constructor parameter
   */
  public void setConfig(ReplicationConfigurationType config) {
     this.config = config;
  }

  /**
   * 
   * @return the determined server identity
   */
  public ServerIdentity getIdentity() {
    return identity;
  }

  /**
   * 
   * @return an instance of a helper class that allows
   */
  public ReplicationConfigAccessor getConfigAccessor() {
    return configAccessor;
  }

  public ReplicationPathType getReplicationPath() {
    return this.thisReplicationPath;
  }

  /**
   * 
   * @return if the system has booted/finisged booting yet
   */
  public boolean hasBooted() {
    return this.hasBooted;
  }


  
  public long getTimeOffset()
  {
    return this.timeOffset;
  }


}
