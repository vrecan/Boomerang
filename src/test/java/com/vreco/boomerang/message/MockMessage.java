package com.vreco.boomerang.message;

import com.vreco.boomerang.conf.Conf;
import java.io.IOException;

/**
 *
 * @author Ben Aldrich
 */
public class MockMessage {


  public static String getBasicMessageString(Conf conf, String forwardQueue) {
    return "{\"" + conf.getValue("boomerang.producer.label") + "\":\"" + forwardQueue + "\", \"private\":{\"woo\":\"woo\"}, \"something\":\"something\"}";
  }

  public static BoomerangMessage getBasicMessage(Conf conf, String forwardQueue) throws IOException {
    String json = "{\"" + conf.getValue("boomerang.producer.label") + "\":\"" + forwardQueue + "\", \"private\":{\"woo\":\"woo\"}, \"something\":\"something\"}";
    return new BoomerangMessage(json, conf);
  }
  
  public static ResponseMessage getBasicResponseSuccess(Conf conf, String date) throws IOException {
    String json = "{\"" + conf.getValue("boomerang.date.label") + "\":\"" + date + "\",\""+ conf.getValue("boomerang.success.label") +"\":true}";
    return new ResponseMessage(json, conf);
  }
}
