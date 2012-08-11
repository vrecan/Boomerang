package com.vreco.boomerang;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.datastore.RedisStore;
import com.vreco.boomerang.message.Message;
import com.vreco.util.mq.Producer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.jms.JMSException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public class ResendExpired implements Runnable {

  final SimpleShutdown shutdown = SimpleShutdown.getInstance();
  RedisStore store;
  final ObjectMapper mapper = new ObjectMapper();
  final Conf conf;
  Producer producer;

  public ResendExpired(Conf conf) {
    this.conf = conf;
  }

  @Override
  public void run() {
    producer = new Producer(conf.getValue("mq.connection.url"));
    store = new RedisStore(conf);
    while (!shutdown.isShutdown()) {
      try {
        Collection<Message> oldMessages = getOldMessages();
        if (!oldMessages.isEmpty()) {
          resend(oldMessages);
        } else {
          try {
            System.out.println("No messages to resend...");
            Thread.sleep(1000);
          } catch (Exception e) {
            //ignore
          }
        }
      } catch (JMSException | IOException | ParseException e) {
        try {
          System.out.println("Unable to resend message");
          System.out.print(e.getCause());
          Thread.sleep(100);
        } catch (InterruptedException ex) {
          //ignore
        }
      }
    }
  }

  protected Collection<Message> getOldMessages() throws IOException, ParseException {
    Collection<Message> msgs = store.getLastNMessages(10);
    ArrayList<Message> remove = new ArrayList(10);
    Date now = new Date();
    Date dateThreshold = new Date(now.getTime() - 60 * 1000);
    
    for(Message msg: msgs) {
      Date msgDate = msg.getDate();
      boolean youngerThenThreshold = dateThreshold.before(msgDate);
      if(youngerThenThreshold) {
        remove.add(msg); 
      }
    }
    msgs.removeAll(remove);
    return msgs;
  }

  protected void resend(Collection<Message> msgs) throws JMSException, IOException, ParseException {
    for (Message msg : msgs) {
      msg.getMsg();
      msg.getQueues();
      producer.connect("queue", msg.getDestination());
      producer.sendMessage(mapper.writeValueAsString(msg.getMsg()));
      store.delete(msg);
      msg.setDate(new Date());
      msg.setRetryCount(msg.getRetryCount() + 1);
      store.set(msg);
    }
  }
}
