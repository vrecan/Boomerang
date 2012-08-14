package com.vreco.boomerang;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.datastore.DataStore;
import com.vreco.boomerang.datastore.RedisStore;
import com.vreco.boomerang.message.BoomerangMessage;
import com.vreco.util.mq.Producer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.script.Update;

/**
 * Loop through oldest messages and resend them back on the queues that have not acknowledged being processed.
 *
 * @author Ben Aldrich
 */
public class ResendExpired implements Runnable {

  final SimpleShutdown shutdown = SimpleShutdown.getInstance();
  final Logger logger = LoggerFactory.getLogger(ResendExpired.class);
  final Conf conf;
  final long defaultResend;
  final long defaultSendTTL;

  public ResendExpired(Conf conf) {
    this.conf = conf;
    this.defaultResend = conf.getLongValue("boomerang.resend.default", Long.parseLong("60000"));
    this.defaultSendTTL = conf.getLongValue("boomerang.producer.ttl.default", Long.parseLong("60000"));
  }

  /**
   * Main loop.
   */
  @Override
  public void run() {
    Producer producer = new Producer(conf.getValue("mq.connection.url"));
    DataStore store = new RedisStore(conf);
    while (!shutdown.isShutdown()) {
      try {
        Collection<BoomerangMessage> oldMessages = getOldMessages(store);
        if (!oldMessages.isEmpty()) {
          failMessages(oldMessages, store);
          resend(producer, store, oldMessages);
        } else {
          try {
            logger.debug("No messages to resend...");
            Thread.sleep(1000);
          } catch (Exception e) {
            logger.error("Caught exception while sleeping:", e);
          }
        }
      } catch (JMSException | IOException | ParseException e) {
        logger.error("Failed to resend message", e);
      }
    }
  }

  /**
   * Fail messages that have been tried more then the configured amount of times.
   *
   * @param msgs
   * @param store
   * @throws ParseException
   * @throws IOException
   */
  protected void failMessages(Collection<BoomerangMessage> msgs, DataStore store) throws ParseException, IOException {
    ArrayList<BoomerangMessage> remove = new ArrayList(10);
    for (BoomerangMessage msg : msgs) {
      if (msg.getRetryCount() >= conf.getIntValue("boomerang.resend.retry", 2)) {
        //TODO: make this an atomic opperation
        store.delete(msg);
        store.setFailed(msg);
        remove.add(msg);
      }
    }
    msgs.removeAll(remove);

  }

  /**
   * Get the oldest messages out of the data store and compare them against our time resend timer.
   *
   * @return
   * @throws IOException
   * @throws ParseException
   */
  protected Collection<BoomerangMessage> getOldMessages(DataStore store) throws IOException, ParseException {
    Collection<BoomerangMessage> msgs = store.getLastNMessages(10);
    ArrayList<BoomerangMessage> remove = new ArrayList(10);
    Date now = new Date();
    Date dateThreshold = new Date(now.getTime() - defaultResend);

    for (BoomerangMessage msg : msgs) {
      Date msgDate = msg.getDate();
      boolean youngerThenThreshold = dateThreshold.before(msgDate);
      if (youngerThenThreshold) {
        remove.add(msg);
      }
    }
    msgs.removeAll(remove);
    return msgs;
  }

  /**
   * Resend the message.
   *
   * @param msgs
   * @throws JMSException
   * @throws IOException
   * @throws ParseException
   */
  protected void resend(Producer producer, DataStore store, Collection<BoomerangMessage> msgs) throws JMSException, IOException, ParseException {
    for (BoomerangMessage msg : msgs) {
      producer.connect("queue", msg.getDestination());
      producer.setUseAsyncSend(true);
      producer.setTTL(defaultSendTTL);
      producer.setPersistence(false);
      BoomerangMessage newMsg = new BoomerangMessage(msg.getJsonStringMessage(), conf);
      
      //reset the date and inrement our retry.
      newMsg.setDate(new Date());
      newMsg.incrementRetry();
      
      producer.sendMessage(newMsg.getJsonStringMessage());
      store.update(msg, newMsg);
    }
  }
}
