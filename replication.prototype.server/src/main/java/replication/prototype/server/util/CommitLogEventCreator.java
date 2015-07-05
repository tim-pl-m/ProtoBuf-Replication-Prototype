package replication.prototype.server.util;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import replication.prototype.server.Server;
import replication.prototype.server.messages.M.Command;

public class CommitLogEventCreator {

  private Marshaller marshaller;

  public CommitLogEventCreator() throws JAXBException {
    JAXBContext jc = JAXBContext.newInstance(CommitLogEvent.class);
    this.marshaller = jc.createMarshaller();
    this.marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
  }

  public CommitLogEvent createCommitLogEvent(Command cmd, Server serv) {
    return new CommitLogEvent(cmd, serv, this.marshaller);
  }

}


