package com.vreco.boomerang.message;

/**
 * This is a POJO to convert our json response message.
 *
 * @author Ben Aldrich
 */
public class ResponseMessage {

  private String uuid;
  private String boomDate;
  private String queue;
  private boolean success = false;
  private boolean resetTimer = false;

  /**
   * @return the uuid
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * @param uuid the uuid to set
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  /**
   * @return the success
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * @param success the success to set
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * @return the resetTimer
   */
  public boolean isResetTimer() {
    return resetTimer;
  }

  /**
   * @param resetTimer the resetTimer to set
   */
  public void setResetTimer(boolean resetTimer) {
    this.resetTimer = resetTimer;
  }

  /**
   * @return the boomDate
   */
  public String getBoomDate() {
    return boomDate;
  }

  /**
   * @param boomDate the boomDate to set
   */
  public void setBoomDate(String boomDate) {
    this.boomDate = boomDate;
  }

  /**
   * @return the queue
   */
  public String getQueue() {
    return queue;
  }

  /**
   * @param queue the queue to set
   */
  public void setQueue(String queue) {
    this.queue = queue;
  }
}
