package replication.prototype.server.core;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import replication.prototype.server.environment.CommunicationEnumType;
import replication.prototype.server.environment.QuorumReplicationLinkType;
import replication.prototype.server.environment.ReplicationLinkType;
import replication.prototype.server.messages.M.Command;
import replication.prototype.server.messages.M.Response;
import replication.prototype.server.util.ReplicationConfigAccessor;

/**
 * This class implements the quroum replication strategy. It dispatches received commands in a
 * quorum by using <i>n</i> synchronous dispatchers in <i>n</i> threads, where <i>n</i> is the
 * number of participants within the quorum. Every 50 milliseconds the implementation checks if at
 * least <i>k</i> of <i>n</i> have already responded, where <i>k</i> is the quorum size. In that
 * case the caller is able to continue as the quorum condition is satisfied.
 * 
 * Consider that the dispatcher can run into a configurable (before building) timeout @see {@link replication.prototype.server.core.QuorumCommandDispatcher#TIMEOUTMILLIS
 * TIMEOUTMILLIS}. If at least <i>k</i> of <i>n</i> nodes have not responded after 60000 milliseconds the method {@link replication.prototype.server.core.QuorumCommandDispatcher#dispatchCommand(Command)
 * dispatchCommand(Command)} will throw an IOException.
 */
public class QuorumCommandDispatcher implements ICommandDispatcher {

  static final Logger logger = LogManager.getLogger(QuorumCommandDispatcher.class.getName());

  static final long TIMEOUTMILLIS = 60000;

  private QuorumReplicationLinkType repLink;
  private ReplicationConfigAccessor configAccessor;
  private SyncCommandDispatcher[] syncDispatchers;
  private int syncDispatchersCounter = 0;

  public QuorumCommandDispatcher(QuorumReplicationLinkType repLink,
      ReplicationConfigAccessor configAccessor) {
    this.repLink = repLink;
    this.configAccessor = configAccessor;
    this.syncDispatchers = new SyncCommandDispatcher[repLink.getQparticipant().size()];

  }


  /**
   * 
   */
  @Override
  public void dispatchCommand(Command command) throws IOException {

    ArrayList<Thread> tasks = new ArrayList<Thread>();

    // create a sync dispatcher for every participant in the quorum
    // pass an adopted replication link to that dispatcher and invoke dispatching
    this.repLink
        .getQparticipant()
        .forEach(
            t -> tasks
                .add(new Thread(
                    () -> {
                      try {
                        // create dispatcher
                        QuorumCommandDispatcher.this.syncDispatchers[QuorumCommandDispatcher.this.syncDispatchersCounter] =
                            QuorumCommandDispatcher.this
                                .createSyncCommandDispatcherForQuorumParticipant(t, repLink);
                        // invoke dispatching
                        QuorumCommandDispatcher.this.syncDispatchers[QuorumCommandDispatcher.this.syncDispatchersCounter]
                            .dispatchCommand(command);
                        
                        QuorumCommandDispatcher.this.syncDispatchersCounter++;
                      } catch (Exception e) {
                      }

                    })));

    // start every thread
    tasks.forEach(t -> t.start());

    boolean quorumSatisfied = false;
    long time = System.currentTimeMillis();
    while (quorumSatisfied) {
      // check ifquorum is fullfilied

      if (tasks.stream().filter(t -> t.isAlive()).count() > this.repLink.getQsize().longValue()) {
        quorumSatisfied = true;
      } else if (System.currentTimeMillis() - time > TIMEOUTMILLIS) {
        logger.error("Quorum failed because of timeout.");
        throw new IOException("Timeout");
      }


      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        logger.error("Unexpected error while activating thread sleep.)");
      }
    }

    // new Thread(task).start();

  }

  /**
   * The QuorumReplicationLinkType doesn't contain simple replication links but it uses a dedicated type
   * to describe the quorum participants. As the synchronous command dispatcher expects a replication link, the method created
   * a synchrnous command dispatcher an passes it a new adopted replication link.
   * @param qp the participants to create a command dispatcher for, the replication link for the whole quorum
   * @param repLink
   * @return
   * @throws IOException 
   */
  private SyncCommandDispatcher createSyncCommandDispatcherForQuorumParticipant(
      QuorumReplicationLinkType.Qparticipant qp, QuorumReplicationLinkType repLink) throws IOException {

    ReplicationLinkType adoptedRepLink = new ReplicationLinkType();
    adoptedRepLink.setSrc(repLink.getSrc());
    adoptedRepLink.setTarget(qp.getName());
    adoptedRepLink.setType(CommunicationEnumType.SYNC);
    return new SyncCommandDispatcher(adoptedRepLink, this.configAccessor);
  }


  @Override
  public Response getResponse() {

    return null;
  }

  @Override
  public Response[] getResponses() {
    // not implemented yet
    return null;
  }

}
