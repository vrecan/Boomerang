package com.vreco.boomerang.message;

import com.vreco.boomerang.conf.Conf;
import java.io.IOException;

/**
 *
 * @author Ben Aldrich
 */
public class ResponseMessage extends Message {
  private boolean success = false;

  public ResponseMessage(String hMsg, final Conf conf) throws IOException {
    super(hMsg, conf);
    setInternalSuccess();
    
  }

  /**
   * Set internal success field.
   */
  private void setInternalSuccess() {
    try {
      this.success = (boolean) msg.get(conf.getValue("boomerang.success.label"));
    } catch (Exception e) {
      this.success = false;
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
    msg.put(conf.getValue("boomerang.success.label"), success);
  }
}
