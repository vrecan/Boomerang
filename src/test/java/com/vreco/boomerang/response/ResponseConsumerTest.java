package com.vreco.boomerang.response;

import com.vreco.boomerang.Main;
import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import com.vreco.boomerang.datastore.DataStore;
import com.vreco.boomerang.datastore.RedisStore;
import com.vreco.boomerang.message.Message;
import com.vreco.boomerang.message.MockMessage;
import com.vreco.util.mq.Consumer;
import com.vreco.util.mq.Producer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import javax.jms.TextMessage;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.*;

/**
 *
 * @author Ben Aldrich
 */
public class ResponseConsumerTest {

  final protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  final SimpleShutdown shutdown = SimpleShutdown.getInstance();
  DataStore store;
  Conf conf;

  public ResponseConsumerTest() throws IOException {
    conf = new MockConf().conf;
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
    store = new RedisStore(conf);
  }

  @After
  public void tearDown() {
    store.close();
  }

  @Test
  public void ITTestAcknowledgeMessage() throws Exception {
    String forwardQueue = "fullcycleQ";
    HashMap<Thread, Long> threads = Main.getThreads(conf);
    Main.startThreads(threads);
    try (Producer producer = new Producer(conf.getValue("mq.connection.url"));
            Consumer consumer = new Consumer(conf.getValue("mq.connection.url"))) {
      Message msg = MockMessage.getBasicMessage(conf, forwardQueue);

      producer.connect("queue", conf.getValue("mq.processing.queue"));
      producer.setPersistence(false);
      producer.sendMessage(msg.getJsonStringMessage());

      consumer.setTimeout(2000);
      consumer.connect("queue", forwardQueue);
      TextMessage mqMsg = consumer.getTextMessage();
      if (mqMsg == null) {
        throw new IOException("Failed to get a message from " + forwardQueue + " queue");
      }
      System.out.println(mqMsg.getText());
      Message rMsg = new Message(mqMsg.getText(), conf);
      rMsg.setSuccess(true);
      //Did the message make it in the db store?
      Assert.assertTrue(store.exists(rMsg));
      producer.connect("queue", conf.getValue("mq.response.queue"));
      producer.sendMessage(rMsg.getJsonStringMessage());
      mqMsg.acknowledge();
      if (!waitForMessageDeleteInStore(rMsg)) {
        Assert.fail("Message not removed from data store");
      }
      this.shutdown.setShutdown(true);
    }
  }

  /**
   * Wait for message to be deleted in the data store.
   *
   * @param msg
   * @return
   * @throws InterruptedException
   */
  public boolean waitForMessageDeleteInStore(Message msg) throws InterruptedException, ParseException {
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
}