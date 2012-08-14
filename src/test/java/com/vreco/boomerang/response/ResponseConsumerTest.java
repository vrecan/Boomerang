package com.vreco.boomerang.response;

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
    ((RedisStore) store).deleteAll();
  }

  @After
  public void tearDown() {
    store.close();
  }

  @Test
  public void TestMultipleQueueResponse() throws Exception {
    String queues = "testMultipleResponse1Q, testMultipleResponse2Q";
    BoomerangMessage storeMsg = MockMessage.getBasicMessage(conf, queues);
    store.set(storeMsg);
    ResponseMessage r = new ResponseMessage(storeMsg.getJsonStringMessage(), conf);
    r.setResponseQueue("testMultipleResponse1Q");
    r.setSuccess(true);
    String get = store.get(r);
    store.updateOrDelete(r);
    //Message should still exist because there are multiple queues.
    Assert.assertTrue(store.exists(r));
    r.setResponseQueue("testMultipleResponse2Q");
    store.updateOrDelete(r);
    Assert.assertFalse(store.exists(r));
  }
}