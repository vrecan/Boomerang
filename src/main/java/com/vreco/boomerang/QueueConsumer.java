package com.vreco.boomerang;

import com.vreco.boomerang.message.Message;
import com.vreco.util.mq.Consumer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public class QueueConsumer implements Runnable {

  Consumer consumer = new Consumer();
  SimpleShutdown shutdown = SimpleShutdown.getInstance();
  ObjectMapper mapper = new ObjectMapper();

  public QueueConsumer() {
  }

  @Override
  public void run() {
    try {
      connect();
      while (!shutdown.isShutdown()) {
        TextMessage mqMsg = consumer.getTextMessage();        

      }
    } catch (JMSException ex) {
      Logger.getLogger(QueueConsumer.class.getName()).log(Level.SEVERE, null, ex);
    }

  }

  protected void connect() throws JMSException {
    consumer.connect("vm://localhost", "testQueue");
    consumer.setTimeout(1000);
  }
  
  protected void consume(TextMessage mqMsg) {
        try {
          Message msg = mapper.readValue(mqMsg.getText(), Message.class);
          System.out.println(msg.toString());
        } catch (JMSException | IOException e) {
          System.out.print(e.getStackTrace().toString());
        }
    
  }
}
