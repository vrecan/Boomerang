package com.vreco.boomerang.response;

import com.vreco.boomerang.message.ResponseMessage;
import com.vreco.boomerang.DataStore;
import com.vreco.boomerang.RedisStore;
import com.vreco.boomerang.conf.Conf;
import com.vreco.util.mq.Consumer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public class ResponseConsumer implements Runnable {

  SimpleShutdown shutdown = SimpleShutdown.getInstance();
  ObjectMapper mapper = new ObjectMapper();
  DataStore store = new RedisStore("localhost", "superslack");
  final protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
  final Conf conf;

  public ResponseConsumer(Conf conf) {
    this.conf = conf;
  }

  @Override
  public void run() {
    try (Consumer consumer = new Consumer(conf.getValue("mq.connection.url"))) {
      consumer.setTimeout(conf.getLongValue("mq.connection.timeout", Long.parseLong("2000")));
      consumer.connect("queue", conf.getValue("mq.response.queue"));
      while (!shutdown.isShutdown()) {
        TextMessage mqMsg = consumer.getTextMessage();
        if (mqMsg != null) {
          try {
            processResponse(mapper.readValue(mqMsg.getText(), ResponseMessage.class));
            mqMsg.acknowledge();
            System.out.println("finished processing response.");
          } catch (JMSException | IOException e) {
            System.out.print(e.getCause());
          }
        } else {
          System.out.println("No messages on response queue...");
        }
      }
    } catch (JMSException e) {
      System.out.print(e.getCause());
    }

  }

  protected void processResponse(ResponseMessage msg) {
    try {
      System.out.println("processing response...");
      if (msg.isSuccess() && !msg.getBoomDate().isEmpty()) {
        store.delete(msg.getUuid(), sdf.parse(msg.getBoomDate()));
      } else if (msg.isResetTimer()) {
        //delete and re add with new date
      }
    } catch (Exception e) {
      System.out.println(e.getCause());
    }
  }
}
