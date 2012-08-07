package com.vreco.boomerang.message;

import com.vreco.boomerang.conf.Conf;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is the POJO mapping for a message object.
 *
 * @author Ben Aldrich
 */
public class Message {

  private final HashMap<String, Object> msg;
  private final String uuid;
  private final Date date;
  private ArrayList<String> queues = new ArrayList();
  final protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

  public Message(HashMap<String, Object> msg, final Conf conf) {
    this.msg = msg;
    this.uuid = UUID.randomUUID().toString();
    this.date = new Date();

    String sQueues = (String) msg.get(conf.getValue("boomerang.producer.label"));
    if(sQueues != null) {
      String[] split = sQueues.split(",");
      queues.addAll(Arrays.asList(split));
    }

    msg.put(conf.getValue("boomerang.date.label"), dateFormat.format(date));
    msg.put(conf.getValue("boomerang.uuid.label"), uuid);
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
}
