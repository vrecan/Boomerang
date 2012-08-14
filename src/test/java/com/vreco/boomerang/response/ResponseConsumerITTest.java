package com.vreco.boomerang.response;

import com.vreco.boomerang.Main;
import com.vreco.boomerang.TestUtil;
import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import com.vreco.boomerang.datastore.DataStore;
import com.vreco.boomerang.datastore.RedisStore;
import com.vreco.boomerang.message.BoomerangMessage;
import com.vreco.boomerang.message.MockMessage;
import com.vreco.boomerang.message.ResponseMessage;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import org.junit.*;

/**
 *
 * @author Ben Aldrich
 */
public class ResponseConsumerITTest {

  final protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  final SimpleShutdown shutdown = SimpleShutdown.getInstance();
  DataStore store;
  Conf conf;

  public ResponseConsumerITTest() throws IOException {
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
    ((RedisStore) store).deleteAll();
  }

  @After
  public void tearDown() {
    store.close();
  }

  /**
   * Test message acknowledgment.
   *
   * @throws Exception
   */
  @Test
  public void ITTestAcknowledgeMessage() throws Exception {
    String forwardQueue = "TestAckMessage";
    HashMap<Thread, Long> threads = Main.getThreads(conf);
    Main.startThreads(threads);
    BoomerangMessage msg = MockMessage.getBasicMessage(conf, forwardQueue);
    TestUtil.sendBoomerangMessage(msg, conf);

    BoomerangMessage bMsg = TestUtil.ConsumeMessage(forwardQueue, conf);
    ResponseMessage rMsg = new ResponseMessage(bMsg.getJsonStringMessage(), conf);
    rMsg.setSuccess(true);
    rMsg.setResponseQueue(forwardQueue);
    //Did the message make it in the db store?
    Assert.assertTrue(store.exists(rMsg));
    TestUtil.sendResponseMessage(rMsg, conf);
    if (!TestUtil.waitForMessageDeleteInStore(rMsg, store)) {
      Assert.fail("Message not removed from data store");
    }
    this.shutdown.setShutdown(true);
    Main.joinThreads(threads);
    this.shutdown.setFinished(true);
  }
}
