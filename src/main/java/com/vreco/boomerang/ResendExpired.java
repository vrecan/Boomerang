package com.vreco.boomerang;

import com.vreco.boomerang.conf.Conf;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public class ResendExpired implements Runnable {

  final SimpleShutdown shutdown = SimpleShutdown.getInstance();
  RedisStore store = new RedisStore("localhost", "superslack");
  final ObjectMapper mapper = new ObjectMapper();
  final Conf conf;

  public ResendExpired(Conf conf) {
    this.conf = conf;
  }

  @Override
  public void run() {
    while (!shutdown.isShutdown()) {
//      Set<String> keys = store.getKeys();
//      for (String k : keys) {
//        String rawMsg = store.get(k);
//        if (rawMsg != null) {
//          try {
//            Message msg = mapper.readValue(rawMsg, Message.class);
//            System.out.println(mapper.writeValueAsString(msg));
//            //check date in msg here and resend if it's expired.
//          } catch (Exception ex) {
//            System.out.print(ex.getStackTrace().toString());
//          }
//        }
      //   }
      try {
        Thread.sleep(1000);
      } catch (Exception e) {
        //ignore
      }
    }
  }
}
