package com.vreco.boomerang.message;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * This is the POJO mapping for a message object.
 * @author Ben Aldrich
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
  
  private String processName;
  private String uuid;

  public void setProcessName(String processName) {
    this.processName = processName;
  }
  
  public String getProcessName(){
    return processName;
  }

  public void setUUID(String UUID) {
    this.uuid = UUID;
  }
  
  public String getUUID() {
    return uuid;
  }
}
