package com.vreco.boomerang.message;

import java.util.*;

/**
 * This is the POJO mapping for a message object.
 *
 * @author Ben Aldrich
 */
public class Message {

  private HashMap<String, Object> msg;
  private String uuid;
  private Date date;
  private ArrayList<String> queues = new ArrayList();

  public Message(String sQueues) {
    this.uuid = UUID.randomUUID().toString();
    this.date = new Date();
    String[] split = sQueues.split(",");
    queues.addAll(Arrays.asList(split));
  }

  /**
   * @return the msg
   */
  public HashMap<String, Object> getMsg() {
    return msg;
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
   * @return the queues
   */
  public ArrayList<String> getQueues() {
    return queues;
  }

  /**
   * @param queues the queues to set
   */
  public void setQueues(ArrayList<String> queues) {
    this.queues = queues;
  }
}
