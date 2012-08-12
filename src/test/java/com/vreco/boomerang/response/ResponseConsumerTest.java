package com.vreco.boomerang.response;

import com.vreco.boomerang.Main;
import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import com.vreco.boomerang.datastore.DataStore;
import com.vreco.boomerang.datastore.RedisStore;
import com.vreco.boomerang.message.ResponseMessage;
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

  ObjectMapper mapper = new ObjectMapper();
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
  public void testFullCycle() throws Exception {
    String forwardQueue = "fullcycleQ";
    HashMap<Thread, Long> threads = Main.getThreads(conf);
    Main.startThreads(threads);
    try (Producer producer = new Producer(conf.getValue("mq.connection.url"));
            Consumer consumer = new Consumer(conf.getValue("mq.connection.url"))) {
      String json = "{\"boomQueues\":\"" + forwardQueue + "\", \"private\":{\"woo\":\"woo\"}, \"something\":\"something\"}";
      producer.connect("queue", conf.getValue("mq.processing.queue"));
      producer.setPersistence(false);
      producer.sendMessage(json);
      consumer.connect("queue", forwardQueue);
      TextMessage mqMsg = consumer.getTextMessage();
      if (mqMsg == null) {
        throw new IOException("Failed to get a message from " + forwardQueue + " queue");
      }
      System.out.println(mqMsg.getText());
      HashMap<String, Object> forwardedMessage = mapper.readValue(mqMsg.getText(), HashMap.class);
      producer.connect("queue", conf.getValue("mq.response.queue"));
      ResponseMessage rMsg = new ResponseMessage();
      rMsg.setDate(dateFormat.parse((String) forwardedMessage.get(conf.getValue("boomerang.date.label"))));
      rMsg.setUuid((String) forwardedMessage.get(conf.getValue("boomerang.uuid.label")));
      rMsg.setQueue((String) forwardedMessage.get(conf.getValue("boomerang.producer.label")));
      rMsg.setSuccess(true);
      //Did the message make it in the db store?
      Assert.assertTrue(store.exists(rMsg));
      producer.sendMessage(mapper.writeValueAsString(rMsg));
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
  public boolean waitForMessageDeleteInStore(ResponseMessage msg) throws InterruptedException, ParseException {
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