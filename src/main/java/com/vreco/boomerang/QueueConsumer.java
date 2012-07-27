package com.vreco.boomerang;

import com.vreco.boomerang.message.Message;
import com.vreco.util.mq.Consumer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
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

  public QueueConsumer() {
  }

  @Override
  public void run() {
    try (Consumer consumer = new Consumer()) {
      consumer.setTimeout(1000);
      consumer.connect("vm://localhost", "tempqueue");      
      while (!shutdown.isShutdown()) {
        TextMessage mqMsg = consumer.getTextMessage();
        if(mqMsg != null) {
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
      Message msg = mapper.readValue(mqMsg.getText(), Message.class);
      System.out.println(mapper.writeValueAsString(msg));
      mqMsg.acknowledge();
    } catch (JMSException | IOException e) {
      System.out.print(e.getStackTrace().toString());
    }

  }
}
