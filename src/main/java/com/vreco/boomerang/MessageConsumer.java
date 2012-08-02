package com.vreco.boomerang;

import com.vreco.boomerang.conf.Conf;
import com.vreco.util.mq.Consumer;
import com.vreco.util.mq.Producer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
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
  final Conf conf;

  public MessageConsumer(Conf conf) {
    this.conf = conf;
  }

  @Override
  public void run() {
    try (Consumer consumer = new Consumer(conf.getValue("mq.connection.url"));
            Producer producer = new Producer(conf.getValue("mq.connection.url"))) {
      consumer.setTimeout(conf.getLongValue("mq.connection.timeout", Long.parseLong("2000")));
      consumer.connect("queue", conf.getValue("mq.processing.queue"));
      while (!shutdown.isShutdown()) {
        TextMessage mqMsg = consumer.getTextMessage();
        if (mqMsg != null) {
          try {
            HashMap<String, Object> msg = mapper.readValue(mqMsg.getText(), HashMap.class);
            msg = setAndStoreMsg(msg);
            String queue = (String) msg.get(conf.getValue("boomerang.producer.label"));
            producer.connect("queue", queue);
            producer.sendMessage(mapper.writeValueAsString(msg));
            mqMsg.acknowledge();
          } catch (JMSException | IOException e) {
            System.out.print(e.getCause().toString());
          }
        } else {
          System.out.println("No messages on queue...");
        }
      }
    } catch (JMSException e) {
      System.out.print(e.getCause().toString());
    }

  }

  protected HashMap<String, Object> setAndStoreMsg(HashMap<String, Object> msg) throws JsonMappingException, JsonGenerationException, IOException {
    String uuid = UUID.randomUUID().toString();
    Date date = new Date();
    msg.put(conf.getValue("boomerang.date.label"), sdf.format(date));
    msg.put("boomerang.uuid.label", uuid);
    store.set(uuid, mapper.writeValueAsString(msg), date);
    return msg;
  }
}
