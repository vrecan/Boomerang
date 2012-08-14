package com.vreco.boomerang.message;

import com.vreco.boomerang.conf.Conf;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Ben Aldrich
 */
public class BoomerangMessage extends Message {

  private Integer RetryCount = 0;
  private ArrayList<String> queues = new ArrayList();

  public BoomerangMessage(String hMsg, final Conf conf) throws IOException {
    super(hMsg, conf);

    setInternalQueues();
    setInternalRetryCount();
  }

  /**
   * Set queues in internal message.
   */
  private void setInternalQueues() {
    setQueues((String) msg.get(conf.getValue("boomerang.producer.label")));
  }

  /**
   * get internal retry counter.
   *
   * @return
   */
  private Integer getInternalRetryCount() {
    try {
      return (Integer) msg.get(conf.getValue("boomerang.retry.label"));
    } catch (Exception e) {
      return null;
    }

  }

  /**
   * Set the internal retry counter.
   */
  private void setInternalRetryCount() {
    this.RetryCount = getInternalRetryCount();
    if (this.RetryCount == null) {
      this.RetryCount = 0;
      msg.put(conf.getValue("boomerang.retry.label"), RetryCount);
    }
  }

  /**
   * @return the queues
   */
  public ArrayList<String> getQueues() {
    return queues;
  }

  /**
   *
   * @return the queues as a string for mq destination
   */
  public String getDestination() {
    StringBuilder sb = new StringBuilder();
    int length = queues.size();
    int count = 0;
    for (String queue : queues) {
      count++;
      sb.append(queue);
      if (count < length) {
        sb.append(",");
      }
    }
    return sb.toString();
  }

  /**
   * @param queues the queues to set
   */
  public void setQueues(final String queue) {
    if (queue != null) {
      String[] split = queue.split(",");
      for(String q : split) {
        this.queues.add(q.trim());
      }
      msg.put(conf.getValue("boomerang.producer.label"), queue);
    }
  }

  /**
   * @param queues the queues to set
   */
  public void setQueues(final ArrayList<String> queues) {
    this.queues = queues;
    StringBuilder sb = new StringBuilder();
    int count = 1;
    int size = queues.size();
    for (String q : queues) {
      sb.append(q);
      if (count < size) {
        sb.append(",");
      }
      count++;
    }
    msg.put(conf.getValue("boomerang.producer.label"), sb.toString());
  }

  /**
   * @return the RetryCount
   */
  public int getRetryCount() {
    return RetryCount;
  }

  /**
   * Set retry count
   *
   * @param retry
   */
  public void setRetryCount(int retry) {
    this.RetryCount = retry;
    msg.put(conf.getValue("boomerang.retry.label"), retry);
  }

  /**
   * Increment the retry counter.
   */
  public void incrementRetry() {
    this.RetryCount = +1;
    msg.put(conf.getValue("boomerang.retry.label"), RetryCount);
  }
}
