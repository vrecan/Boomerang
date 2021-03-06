package com.vreco.boomerang.response;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.datastore.DataStore;
import com.vreco.boomerang.datastore.RedisStore;
import com.vreco.boomerang.message.ResponseMessage;
import com.vreco.util.mq.Consumer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ben Aldrich
 */
public class ResponseConsumer implements Runnable {

  final SimpleShutdown shutdown = SimpleShutdown.getInstance();
  final Logger logger = LoggerFactory.getLogger(ResponseConsumer.class);
  DataStore store;
  final Conf conf;

  public ResponseConsumer(Conf conf) {
    this.conf = conf;
  }

  @Override
  public void run() {
    try (Consumer consumer = new Consumer(conf.getValue("mq.connection.url"))) {
      store = new RedisStore(conf);
      consumer.setTimeout(conf.getLongValue("mq.connection.timeout", Long.parseLong("2000")));
      consumer.connect("queue", conf.getValue("mq.response.queue"));
      while (!shutdown.isShutdown()) {
        TextMessage mqMsg = consumer.getTextMessage();
        if (mqMsg != null) {
          try {
            processResponse(new ResponseMessage(mqMsg.getText(), conf), store);
            mqMsg.acknowledge();
            logger.debug("finished processing response.");
          } catch (JMSException | IOException e) {
            logger.error("Caught exceptiong while processing response: ", e);
          }
        } else {
          logger.debug("No messages on response queue...");
        }
      }
    } catch (JMSException e) {
      logger.error("Caught MQ exception", e);
    }

  }

  /**
   * Process a response message.
   * @param msg 
   */
  protected void processResponse(ResponseMessage msg, DataStore store) {
    try {
      logger.debug("processing response...");
      logger.debug("ResponseMsg: {}", msg.getJsonStringMessage());
      logger.debug("Response Date: {}",msg.getDate());
      logger.debug("Response Success: {}",msg.isSuccess());
      if (msg.isSuccess() && msg.getDate() != null) {
        store.updateOrDelete(msg);
      }
      //TODO: Deal with other cases other then succes msg.
    } catch (Exception e) {
      logger.error("Error processing response: ", e);
    }
  }
}
