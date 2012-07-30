package com.vreco.boomerang;

import com.vreco.boomerang.message.Message;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.util.Set;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public class ResendExpired implements Runnable {

  SimpleShutdown shutdown = SimpleShutdown.getInstance();
  StoreMessage store = new StoreMessage("localhost", "superslack");
  ObjectMapper mapper = new ObjectMapper();

  public void ResendExpired() {
  }

  @Override
  public void run() {
    while (!shutdown.isShutdown()) {
      Set<String> keys = store.getKeys();
      for (String k : keys) {
        String rawMsg = store.get(k);
        if (rawMsg != null) {
          try {
            Message msg = mapper.readValue(rawMsg, Message.class);
            System.out.println(mapper.writeValueAsString(msg));
            //check date in msg here and resend if it's expired.
          } catch (Exception ex) {
            System.out.print(ex.getStackTrace().toString());
          }
        }
      }
      try {
        Thread.sleep(100);
      } catch (Exception e) {
        //ignore
      }
    }
  }
}
