package com.vreco.boomerang;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import com.vreco.boomerang.datastore.RedisStore;
import com.vreco.boomerang.message.Message;
import com.vreco.boomerang.message.MockMessage;
import com.vreco.util.mq.Producer;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import org.junit.*;

/**
 *
 * @author Ben Aldrich
 */
public class ResendExpiredTest {

  private Conf conf;
  private RedisStore store;
  private Producer producer;
  private ResendExpired re;

  public ResendExpiredTest() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() throws IOException {
    conf = new MockConf().conf;
    store = new RedisStore(conf);
    producer = new Producer("vm://localhost");
    re = new ResendExpired(conf);
    store.deleteAll();

  }

  @After
  public void tearDown() {
    store.close();
    producer.close();
  }

  /**
   * Test of getOldMessages method, of class ResendExpired.
   */
  @Test
  public void testGetOldMessages() throws Exception {

    Message m1 = MockMessage.getBasicMessage(conf, "ResendExpired1Q");
    Message m2 = MockMessage.getBasicMessage(conf, "ResendExpired2Q");
    store.set(m1);
    store.set(m2);

    //Default timeout to be old is set to 200ms
    Thread.sleep(200);
    Collection<Message> oldMessages = re.getOldMessages(store);
    store.delete(m1);
    store.delete(m2);
    Assert.assertEquals(2, oldMessages.size());
  }

  /**
   * Test of resend method, of class ResendExpired.
   */
  @Test
  public void testResend() throws Exception {
    Message m1 = MockMessage.getBasicMessage(conf, "ResendExpired1Q");
    Message m2 = MockMessage.getBasicMessage(conf, "ResendExpired2Q");
    store.set(m1);
    store.set(m2);

    //Default timeout to be old is set to 200ms
    Thread.sleep(200);
    Collection<Message> oldMessages = re.getOldMessages(store);
    re.resend(producer, store, oldMessages);


    //These messages should no longer exist because the dates have changed
    Assert.assertFalse(store.exists(m1));
    Assert.assertFalse(store.exists(m2));

    //consume messages and validate that they exist in our store
    Message r1 = TestUtil.ConsumeMessage("ResendExpired1Q", conf);
    Message r2 = TestUtil.ConsumeMessage("ResendExpired2Q", conf);
    System.out.println(r1.getJsonStringMessage());
    System.out.println(r2.getJsonStringMessage());
    Assert.assertTrue(store.exists(r1));
    Assert.assertTrue(store.exists(r2));
    store.delete(r1);
    store.delete(r2);

    Assert.assertFalse(store.exists(r1));
    Assert.assertFalse(store.exists(r2));
  }

  @Test
  public void testResendRetryCount() throws Exception {
    conf.setValue("boomerang.resend.retry", "1");
    Message m1 = MockMessage.getBasicMessage(conf, "ResendExpired1Q");
    store.set(m1);
    re = new ResendExpired(conf);
    Thread.sleep(200);
    Collection<Message> oldMessages = re.getOldMessages(store);
    re.failMessages(oldMessages, store);
    Assert.assertEquals(1,oldMessages.size());
    m1.incrementRetry();
    store.set(m1);
    oldMessages = re.getOldMessages(store);
    re.failMessages(oldMessages, store);
    Assert.assertEquals(0,oldMessages.size());
    Assert.assertTrue(store.existsFailed(m1));
    store.deleteFailed(m1);
    Assert.assertFalse(store.existsFailed(m1));
  }
}