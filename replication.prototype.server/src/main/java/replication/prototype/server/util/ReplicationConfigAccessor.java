package replication.prototype.server.util;


import java.util.HashMap;
import java.util.Map;

import replication.prototype.server.environment.NodeType;
import replication.prototype.server.environment.ReplicationConfigurationType;
import replication.prototype.server.environment.ReplicationPathType;

/**
 * This class is helper class for accessing and analyzing the structure ofReplicationPathType
 *
 */
public class ReplicationConfigAccessor {
	
	private ReplicationConfigurationType config;
	private NodeType thisNode;
	private Map<String, NodeType> nodes;

	
	public ReplicationConfigAccessor(ReplicationConfigurationType config, NodeType thisNode)
	{
		this.config = config;
		this.thisNode = thisNode;
		this.nodes = new HashMap<String, NodeType>();
	}
	

	/**
	 * Will analyze the object configuration structure looks for a node labeled as specified. 
	 * @param label the label of the node
	 * @return the node's object representation
	 */
	public NodeType getNodeByLabel(String label)
	{
		if(this.nodes.containsKey(label)) {
		  return this.nodes.get(label);
		} else {
		  
		  NodeType node = this.config.getReplicationnodes().getNode().stream().filter(r -> r.getLabel().equals(label)).findFirst().get();
		  this.nodes.put(label, node);
		  return node;
		}
	   
	}
	
	/**
	 * Will return the replication path for the node set as constructor parameter.
	 * @return the corresponding replication path
	 */
	public ReplicationPathType getReplicationPath()
	{
		return this.config.getReplicationpaths().getPath().stream().filter(p -> p.getStart().equals(this.thisNode.getLabel())).findFirst().get();

	}
	
	/**
     * Will return the replication path for any node provided as parameter.
	 * @param node
	 * @return the corresponding replication path
	 */
	public  ReplicationPathType getPathByNode(NodeType node)
	{
		return this.config.getReplicationpaths().getPath().stream().filter(p -> p.getStart().equals(node.getLabel())).findFirst().get();

	}
	
	
	

}
