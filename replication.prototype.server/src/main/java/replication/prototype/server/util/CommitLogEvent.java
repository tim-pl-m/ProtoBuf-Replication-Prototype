package replication.prototype.server.util;

import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import replication.prototype.server.Server;
import replication.prototype.server.messages.M.Command;


public class CommitLogEvent {


  private String node;
  private String commandId;
  private Date timestamp;
  private String operation;
  private Marshaller ma;

  public CommitLogEvent(Command cm, Server sv, Marshaller ma) {
    this.node = sv.getIdentity().getServerIdentity().getLabel();
    this.commandId = cm.getId();
    this.timestamp = new java.util.Date();
    this.operation = cm.getOperation().name();
    this.ma = ma;
  }

  public String toCsv() {
    return this.node + ", " + this.commandId + ", " + this.timestamp + ", " + this.operation;
  }

  public String toXml() {
    StringWriter sw = new StringWriter();
    JAXBElement<CommitLogEvent> je2 =
        new JAXBElement<CommitLogEvent>(new QName("commitlog"), CommitLogEvent.class, this);

    try {
      this.ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
      this.ma.marshal(je2, sw);

    } catch (JAXBException e) {
      return "<error></error>";
    }

    return sw.toString();

  }
  
  public String getNode() {
    return node;
  }

  public void setNode(String node) {
    this.node = node;
  }

  public String getCommandId() {
    return commandId;
  }

  public void setCommandId(String commandId) {
    this.commandId = commandId;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public String getOperation() {
    return operation;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

}
