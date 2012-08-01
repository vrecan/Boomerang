package com.vreco.boomerang;

import com.vreco.util.mq.Consumer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public class MessageConsumer implements Runnable {

  SimpleShutdown shutdown = SimpleShutdown.getInstance();
  ObjectMapper mapper = new ObjectMapper();
  DataStore store = new RedisStore("localhost", "superslack");
  final protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

  public MessageConsumer() {
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
      Logger.getLogger(MessageConsumer.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  protected void consume(TextMessage mqMsg) {
    try {
      String uuid = UUID.randomUUID().toString();
      Date date = new Date();
      HashMap<String, Object> msg = mapper.readValue(mqMsg.getText(), HashMap.class);
      msg.put("boomDate", sdf.format(date));
      msg.put("boomUid", uuid);
      System.out.println(mapper.writeValueAsString(msg));
      store.set(uuid, mapper.writeValueAsString(msg), date);
      mqMsg.acknowledge();
    } catch (Exception e) {
      System.out.print(e.getStackTrace().toString());
    }
    
  }
}
