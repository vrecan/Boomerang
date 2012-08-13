package com.vreco.boomerang.message;

import com.vreco.boomerang.conf.Conf;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public abstract class Message {

  protected final HashMap<String, Object> msg;
  private String uuid;
  private Date date;
  protected final ObjectMapper mapper = new ObjectMapper();
  protected final SimpleDateFormat msgDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  protected final Conf conf;

  public Message(String hMsg, final Conf conf) throws IOException {
    this.msg = mapper.readValue(hMsg, HashMap.class);
    this.conf = conf;
    
    setInternalUuid();
    setInternalDate();
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
   * Get the internal message as a json String.
   *
   * @return
   * @throws IOException
   */
  public String getJsonStringMessage() throws IOException {
    return mapper.writeValueAsString(msg);
  }  
}
