package com.vreco.boomerang.message;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * This is the POJO mapping for a message object.
 *
 * @author Ben Aldrich
 * @author Michael Golowka
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

  private String processName;
  private String uuid;
  private String message;
  private Date   timestamp;

  public void setProcessName(final String processName) {
    this.processName = processName;
  }

  public String getProcessName() {
    return processName;
  }

  public void setUUID(final String UUID) {
    this.uuid = UUID;
  }

  public String getUUID() {
    return uuid;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(final String message) {
    this.message = message;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(final Date timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result + ((processName == null) ? 0 : processName.hashCode());
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Message other = (Message) obj;
    if (message == null) {
      if (other.message != null) {
        return false;
      }
    } else if (!message.equals(other.message)) {
      return false;
    }
    if (processName == null) {
      if (other.processName != null) {
        return false;
      }
    } else if (!processName.equals(other.processName)) {
      return false;
    }
    if (timestamp == null) {
      if (other.timestamp != null) {
        return false;
      }
    } else if (!timestamp.equals(other.timestamp)) {
      return false;
    }
    if (uuid == null) {
      if (other.uuid != null) {
        return false;
      }
    } else if (!uuid.equals(other.uuid)) {
      return false;
    }
    return true;
  }
}
