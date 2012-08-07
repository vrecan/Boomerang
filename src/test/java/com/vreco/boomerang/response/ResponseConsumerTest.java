package com.vreco.boomerang.response;

import com.vreco.boomerang.datastore.DataStore;
import com.vreco.boomerang.Main;
import com.vreco.boomerang.MessageConsumer;
import com.vreco.boomerang.datastore.RedisStore;
import com.vreco.boomerang.message.ResponseMessage;
import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import com.vreco.util.mq.Consumer;
import com.vreco.util.mq.Producer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.jms.TextMessage;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.*;

/**
 *
 * @author Ben Aldrich
 */
public class ResponseConsumerTest {

  ObjectMapper mapper = new ObjectMapper();
  final protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
  final SimpleShutdown shutdown = SimpleShutdown.getInstance();
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
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testFullCycle() throws Exception {
    DataStore store;
    String forwardQueue = "fullcycleQ";
    HashMap<Thread, Long> threads = Main.getThreads(conf);
    Main.startThreads(threads);
    try (Producer producer = new Producer(conf.getValue("mq.connection.url"));
            Consumer consumer = new Consumer(conf.getValue("mq.connection.url"))) {
      store = new RedisStore(conf.getValue("data.redis.url"), conf.getValue("app.name"));
      String json = "{\"boomQueues\":\"" + forwardQueue + "\", \"private\":{\"woo\":\"woo\"}, \"something\":\"something\"}";
      producer.connect("queue", conf.getValue("mq.processing.queue"));
      producer.setPersistence(false);
      producer.sendMessage(json);
      consumer.connect("queue", forwardQueue);
      TextMessage mqMsg = consumer.getTextMessage();
      if(mqMsg == null) {
        throw new IOException("Failed to get a message from " + forwardQueue + " queue");
      }
      System.out.println(mqMsg.getText());
      HashMap<String,Object> forwardedMessage = mapper.readValue(mqMsg.getText(), HashMap.class);;
      producer.connect("queue", conf.getValue("mq.response.queue"));
      ResponseMessage rMsg = new ResponseMessage();
      rMsg.setDate((String) forwardedMessage.get(conf.getValue("boomerang.date.label")));
      rMsg.setUuid((String) forwardedMessage.get(conf.getValue("boomerang.uuid.label")));
      rMsg.setQueue((String) forwardedMessage.get(conf.getValue("boomerang.producer.label")));
      rMsg.setSuccess(true);
      //Did the message make it in the db store?
      Assert.assertTrue(store.exists(rMsg));
      producer.sendMessage(mapper.writeValueAsString(rMsg));
      mqMsg.acknowledge();
      int count = 0;
      while(store.exists(rMsg)) {
        if(count >= 50) {
          throw new IOException("Message never deleted from store");
        }
        count++;
        Thread.sleep(100);
      }
    }
   this.shutdown.setShutdown(true);      
  }

  /**
   * Test of processResponse method, of class ResponseConsumer.
   */
  @Test
  public void testProcessResponseSuccess() throws JsonParseException, JsonMappingException, IOException {

    ResponseConsumer rc = new ResponseConsumer(conf);
    Date date = new Date();
    String sDate = sdf.format(date);
    String json = "{\"uuid\":\"83a25bcc-adb9-4fc8-99b0-530ab70188ff\", \"date\": \"20120805220840\", \"success\" : \"true\", \"resetTimer\": \"false\", \"queue\":\"test2\"}";
    rc.processResponse(mapper.readValue(json, ResponseMessage.class));
  }
}