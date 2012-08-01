package com.vreco.boomerang;

import com.vreco.util.mq.Consumer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public class QueueConsumer implements Runnable {

  SimpleShutdown shutdown = SimpleShutdown.getInstance();
  ObjectMapper mapper = new ObjectMapper();
  DataStore store = new RedisStore("localhost", "superslack");
  final protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

  public QueueConsumer() {
  }

  @Override
  public void run() {
    try (Consumer consumer = new Consumer()) {
      consumer.setTimeout(1000);
      consumer.connect("vm://localhost", "tempqueue");
      while (!shutdown.isShutdown()) {
        TextMessage mqMsg = consumer.getTextMessage();
        if (mqMsg != null) {
          consume(mqMsg);
        } else {
          System.out.println("No messages on queue...");
        }
      }
    } catch (JMSException ex) {
      Logger.getLogger(QueueConsumer.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  protected void consume(TextMessage mqMsg) {
    try {
      HashMap<String, Object> msg = mapper.readValue(mqMsg.getText(), HashMap.class);
      Date date = sdf.parse((String)msg.get("date"));
      System.out.println(mapper.writeValueAsString(msg));
      store.set((String)msg.get("uuid"), mapper.writeValueAsString(msg), date);
      mqMsg.acknowledge();
    } catch (Exception e) {
      System.out.print(e.getStackTrace().toString());
    }

  }
}
