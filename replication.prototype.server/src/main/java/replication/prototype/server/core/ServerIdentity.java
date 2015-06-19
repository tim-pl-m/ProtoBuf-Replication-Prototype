package replication.prototype.server.core;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import replication.prototype.server.environment.NodeType;
import replication.prototype.server.environment.ReplicationConfigurationType;
import replication.prototype.server.environment.UnknownIdentityException;

/**
 * Objects of this class represent a server's identity, providing access to the underlying node definition
 */
public class ServerIdentity {

  static final Logger logger = LogManager.getLogger(ServerIdentity.class.getName());

  private ReplicationConfigurationType config;
  private String label;
  private Optional<NodeType> nodeType;

  public ServerIdentity(ReplicationConfigurationType config, String label)
      throws UnknownIdentityException {
    this.config = config;
    this.label = label;
    this.determineServerIdentity();
  }

  /**
   * This method will determine the server's identity by traversing through the provided configuration object.
   * If a node definition is found for the given label, the method terminates.
   * If there is no node definition matching the given criteria, an exception will be thrown
   * @throws UnknownIdentityException if there is no node definition for the given label
   */
  private void determineServerIdentity() throws UnknownIdentityException {
    this.nodeType =
        this.config.getReplicationnodes().getNode().stream()
            .filter(n -> n.getLabel().equals(this.label)).findFirst();
    if (this.nodeType == null) {
      logger.debug("The server's identity could be determined: {}", this.getServerIdentity()
          .getLabel());

      throw new UnknownIdentityException();
    }
  }

  /**
   * This method returns the node definition object of this server instance.
   * @return node definition for this server object
   */
  public NodeType getServerIdentity() {
    return this.nodeType.get();
  }

}
