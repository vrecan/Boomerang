package com.vreco.boomerang.datastore;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import com.vreco.boomerang.message.Message;
import com.vreco.boomerang.message.ResponseMessage;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author Ben Aldrich
 */
public class RedisStoreTest {

  final protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
  final SimpleShutdown shutdown = SimpleShutdown.getInstance();
  Conf conf;
  RedisStore store;

  public RedisStoreTest() throws IOException {
    conf = new MockConf().conf;
    store = new RedisStore("localhost", conf.getValue("app.name"));
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
    //store.deleteAll();
  }

  @After
  public void tearDown() {
  }

  /**
   * Test of set method, of class RedisStore.
   */
  @Test
  public void testSetExistDeleteMessage() throws Exception {

    HashMap<String, Object> map = new HashMap();
    Message msg = new Message(map, conf);
    store.set(msg);
    Assert.assertNotNull(store.get(msg));
    Assert.assertTrue(store.exists(msg));
    store.delete(msg);
    Assert.assertFalse(store.exists(msg));
    Assert.assertEquals(0, store.zSize());
  }

  /**
   * Test of set method, of class RedisStore.
   */
  @Test
  public void testSetExistDeleteResponseMessage() throws Exception {

    HashMap<String, Object> map = new HashMap();
    Message msg = new Message(map, conf);
    store.set(msg);
    ResponseMessage rMsg = new ResponseMessage();
    rMsg.setDate(msg.getDate());
    rMsg.setUuid(msg.getUuid());
    rMsg.setQueue((String) msg.getDestination());
    rMsg.setSuccess(true);
    Assert.assertNotNull(store.get(rMsg));
    Assert.assertTrue(store.exists(rMsg));
    store.delete(rMsg);
    Assert.assertFalse(store.exists(rMsg));
    Assert.assertEquals(0, store.zSize());
  }

  /**
   * Test of batchSet method, of class RedisStore.
   */
  @Test
  public void testBatchSet() {
    System.out.println("batchSet");
    Collection<Message> msgs = null;
    RedisStore instance = null;
    instance.batchSet(msgs);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of delete method, of class RedisStore.
   */
  @Test
  public void testDelete() throws Exception {
    System.out.println("delete");
    ResponseMessage msg = null;
    RedisStore instance = null;
    instance.delete(msg);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getZKey method, of class RedisStore.
   */
  @Test
  public void testGetZKey() {
    System.out.println("getZKey");
    RedisStore instance = null;
    String expResult = "";
    String result = instance.getZKey();
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getZValue method, of class RedisStore.
   */
  @Test
  public void testGetZValue() {
    System.out.println("getZValue");
    Date date = null;
    String uuid = "";
    RedisStore instance = null;
    String expResult = "";
    String result = instance.getZValue(date, uuid);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getHashKey method, of class RedisStore.
   */
  @Test
  public void testGetHashKey() {
    System.out.println("getHashKey");
    String prefix = "";
    String uuid = "";
    Date date = null;
    RedisStore instance = null;
    String expResult = "";
    String result = instance.getHashKey(prefix, uuid, date);
    assertEquals(expResult, result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of close method, of class RedisStore.
   */
  @Test
  public void testClose() {
    System.out.println("close");
    RedisStore instance = null;
    instance.close();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
}