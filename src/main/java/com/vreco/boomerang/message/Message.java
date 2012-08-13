package com.vreco.boomerang.message;

import com.vreco.boomerang.conf.Conf;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public class Message {

  private final HashMap<String, Object> msg;
  private String uuid;
  private Date date;
  private ArrayList<String> queues = new ArrayList();
  private final ObjectMapper mapper = new ObjectMapper();
  protected final SimpleDateFormat msgDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  private final Conf conf;
  private Integer RetryCount = 0;
  private boolean success = false;

  public Message(String hMsg, final Conf conf) throws IOException {
    this.msg = mapper.readValue(hMsg, HashMap.class);
    this.conf = conf;

    setInternalQueues();
    setInternalDate();
    setInternalUuid();
    setInternalRetryCount();
    setInternalSuccess();
  }

  /**
   * Set queues in internal message.
   */
  private void setInternalQueues() {
    setQueues((String) msg.get(conf.getValue("boomerang.producer.label")));
  }

  /**
   * Get the date from the internal message.
   *
   * @return
   */
  private Date getInternalDate() {
    try {
      String msgDate = (String) msg.get(conf.getValue("boomerang.date.label"));
      return msgDateFormat.parse(msgDate);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Set the date in both objects.
   */
  private void setInternalDate() {
    this.date = getInternalDate();
    if (this.date == null) {
      setDate(new Date());
    }
  }

  /**
   * Get the uuid from the internal message.
   *
   * @return
   */
  private String getInternalUuid() {
    try {
      return (String) msg.get(conf.getValue("boomerang.uuid.label"));
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Set the uuid in both objects.
   */
  private void setInternalUuid() {
    this.uuid = getInternalUuid();
    if (this.uuid == null) {
      setUuid(UUID.randomUUID().toString());
    }
  }

  /**
   * get internal retry counter.
   *
   * @return
   */
  private Integer getInternalRetryCount() {
    try {
      String retry = (String) msg.get(conf.getValue("boomerang.retry.label"));
      return Integer.parseInt(retry);
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

  private void setInternalSuccess() {
    try {
      this.success = (boolean) msg.get(conf.getValue("boomerang.success.label"));
    } catch (Exception e) {
      this.success = false;
    }
  }

  /**
   * Get the internal message as a json String.
   *
   * @return
   * @throws IOException
   */
  public String getJsonStringMessage() throws IOException {
    return mapper.writeValueAsString(msg);
  }

  /**
   * @return the uuid
   */
  public String getUuid() {
    return uuid;
  }

  /**
   * @return the date
   */
  public Date getDate() {
    return date;
  }

  /**
   * Set the date.
   *
   * @param Date
   */
  public void setDate(Date date) {
    this.date = date;
    msg.put(conf.getValue("boomerang.date.label"), msgDateFormat.format(date));
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
    msg.put(conf.getValue("boomerang.uuid.label"), uuid);
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
      this.queues.addAll(Arrays.asList(split));
      msg.put(conf.getValue("boomerang.producer.label"), queue);
    }
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
    this.RetryCount++;
    msg.put(conf.getValue("boomerang.retry.label"), RetryCount);
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
    msg.put(conf.getValue("boomerang.success.label"), success);
  }
}
