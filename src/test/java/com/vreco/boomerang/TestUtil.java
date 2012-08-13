package com.vreco.boomerang;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.datastore.DataStore;
import com.vreco.boomerang.message.Message;
import com.vreco.util.mq.Consumer;
import com.vreco.util.mq.Producer;
import java.io.IOException;
import java.text.ParseException;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 *
 * @author Ben Aldrich
 */
public class TestUtil {

  /**
   * Wait for message to be deleted in the data store.
   *
   * @param msg
   * @return
   * @throws InterruptedException
   */
  public static boolean waitForMessageDeleteInStore(Message msg, DataStore store) throws InterruptedException, ParseException {
    int count = 0;
    while (store.exists(msg)) {
      if (count >= 50) {
        return false;
      }
      count++;
      Thread.sleep(100);
    }
    return true;
  }

  /**
   * Send a message to boomerang.
   * @param msg
   * @param conf
   * @throws JMSException
   * @throws IOException 
   */
  public static void sendBoomerangMessage(Message msg, Conf conf) throws JMSException, IOException {
    try (Producer producer = new Producer(conf.getValue("mq.connection.url"))) {
      producer.connect("queue", conf.getValue("mq.processing.queue"));
      producer.setPersistence(false);
      producer.sendMessage(msg.getJsonStringMessage());
    }
  }
  
  /**
   * Send a message to response queue.
   * @param msg
   * @param conf
   * @throws JMSException
   * @throws IOException 
   */
  public static void sendResponseMessage(Message msg, Conf conf) throws JMSException, IOException {
    try (Producer producer = new Producer(conf.getValue("mq.connection.url"))) {
      producer.connect("queue", conf.getValue("mq.response.queue"));
      producer.setPersistence(false);
      producer.sendMessage(msg.getJsonStringMessage());
    }
  }  
  
  /**
   * Consume a message from a queue
   * @param msg
   * @param conf
   * @throws JMSException
   * @throws IOException 
   */
  public static Message ConsumeMessage(String queue, Conf conf) throws JMSException, IOException {
    try (Consumer consumer = new Consumer(conf.getValue("mq.connection.url"))) {
     consumer.setTimeout(2000);
      consumer.connect("queue", queue);
      TextMessage mqMsg = consumer.getTextMessage();
      if (mqMsg == null) {
        throw new IOException("Failed to get a message from " + queue + " queue");
      }
      System.out.println(mqMsg.getText());
      mqMsg.acknowledge();
      return new Message(mqMsg.getText(), conf);
    }
  }  
}
