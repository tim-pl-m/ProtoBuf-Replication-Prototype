package replication.prototype.client.util;

import java.util.Date;


public class LatencyMeasurementLogEvent {

  private String commandId;
  private Date timestampBegin;
  private Date timestampEnd;
  
  public String getCommandId() {
    return commandId;
  }
  public void setCommandId(String commandId) {
    this.commandId = commandId;
  }
  public Date getTimestampBegin() {
    return timestampBegin;
  }
  public void setTimestampBegin(Date timestampBegin) {
    this.timestampBegin = timestampBegin;
  }
  public Date getTimestampEnd() {
    return timestampEnd;
  }
  public void setTimestampEnd(Date timestampEnd) {
    this.timestampEnd = timestampEnd;
  }

  public long getDuration()
  {
    return this.timestampBegin.getTime() - this.timestampEnd.getTime(); 
  }
  
  public String toCsv()
  {
    return this.commandId+", "+this.timestampBegin+", "+this.timestampEnd+", "+this.getDuration();
  }
  
}
