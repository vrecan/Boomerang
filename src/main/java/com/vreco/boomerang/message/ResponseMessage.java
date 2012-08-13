package com.vreco.boomerang.message;

import com.vreco.boomerang.conf.Conf;
import java.io.IOException;

/**
 *
 * @author Ben Aldrich
 */
public class ResponseMessage extends Message {
  private boolean success = false;
  private String responseQueue;

  public ResponseMessage(String hMsg, final Conf conf) throws IOException {
    super(hMsg, conf);
    setInternalSuccess();
    setInternalResponseQueue();
    
  }

  /**
   * Set internal success field.
   */
  private void setInternalSuccess() {
    try {
      this.success = (boolean) msg.get(conf.getValue("boomerang.response.success.label"));
    } catch (Exception e) {
      this.success = false;
    }
  }
  
  /**
   * Set internal response queues.
   */
  private void setInternalResponseQueue() {
    try{
      this.responseQueue = (String) msg.get(conf.getValue("boomerang.response.queue.label"));
    } catch (Exception e) {
      this.responseQueue = null;
    } 
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
    msg.put(conf.getValue("boomerang.response.success.label"), success);
  }

  /**
   * Set the queue that is responding.
   * @param queue 
   */
  public void setResponseQueue(String queue) {
    this.responseQueue = queue;
    msg.put(conf.getValue("boomerang.response.queue.label"), this.responseQueue);
  }

}
