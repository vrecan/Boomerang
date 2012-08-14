package com.vreco.boomerang.datastore;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import com.vreco.boomerang.message.BoomerangMessage;
import com.vreco.boomerang.message.ResponseMessage;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.*;

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
    store = new RedisStore(conf);
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
    store.deleteAll();
  }

  @After
  public void tearDown() {
    store.close();
  }

  /**
   * Test of set method, of class RedisStore.
   */
  @Test
  public void testSetExistDeleteMessage() throws Exception {

    String json = "{}";
    BoomerangMessage msg = new BoomerangMessage(json, conf);
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

    String json = "{}";
    BoomerangMessage msg = new BoomerangMessage(json, conf);
    store.set(msg);
    ResponseMessage rm = new ResponseMessage(msg.getJsonStringMessage(), conf);
    rm.setSuccess(true);
    Assert.assertNotNull(store.get(rm));
    Assert.assertTrue(store.exists(rm));
    store.delete(rm);
    Assert.assertFalse(store.exists(rm));
    Assert.assertEquals(0, store.zSize());
  }

  /**
   * Test of getZValue method, of class RedisStore.
   */
  @Test
  public void testGetZValue() {
    Date date = new Date(Long.parseLong("1344486400340"));
    String zValue = store.getZValue(date, "test");
    System.out.println(zValue);
    Assert.assertEquals("1344486400340:test", zValue);
  }

}