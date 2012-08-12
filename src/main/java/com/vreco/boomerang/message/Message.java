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
  private static final ObjectMapper mapper = new ObjectMapper();  
  protected final SimpleDateFormat msgDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  private final Conf conf;
  private Integer RetryCount = 0;
  private boolean success = false;

  public Message(String hMsg, final Conf conf) throws IOException {
    this.msg = mapper.readValue(hMsg, HashMap.class);
    this.conf = conf;

    String sQueues = (String) msg.get(conf.getValue("boomerang.producer.label"));
    if (sQueues != null) {
      String[] split = sQueues.split(",");
      queues.addAll(Arrays.asList(split));
    }
    setInternalDate();
    setInternalUuid();
    setInternalRetryCount();
    setInternalSuccess();
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
    date = getInternalDate();
    if (date == null) {
      this.date = new Date();
    }
    msg.put(conf.getValue("boomerang.date.label"), msgDateFormat.format(date));
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
    uuid = getInternalUuid();
    if (uuid == null) {
      this.uuid = UUID.randomUUID().toString();
    }
    msg.put(conf.getValue("boomerang.uuid.label"), uuid);
  }

  private Integer getInternalRetryCount() {
    try {
      String retry = (String) msg.get(conf.getValue("boomerang.retry.label"));
      return Integer.parseInt(retry);
    } catch (Exception e) {
      return null;
    }

  }

  private void setInternalRetryCount() {
    RetryCount = getInternalRetryCount();
    if (RetryCount == null) {
      this.RetryCount = 0;
    }
    msg.put(conf.getValue("boomerang.retry.label"), RetryCount);
  }

  private void setInternalSuccess() {
    boolean internalSuccess = false;

    try {
      Object sObject = msg.get(conf.getValue("boomerang.success.label"));
      if(sObject instanceof String) {
        internalSuccess = Boolean.parseBoolean((String) sObject );
      } 
      else if(sObject instanceof Boolean) {
        internalSuccess = (boolean) sObject;
      } else {
        throw new IOException("Type not supported");
      }
    } catch (Exception e) {
      internalSuccess = false;
    }
    success = internalSuccess;
    
  }


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
  public void setQueues(final ArrayList<String> queues) {
    this.queues = queues;
  }

  /**
   * @return the RetryCount
   */
  public int getRetryCount() {
    return RetryCount;
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
