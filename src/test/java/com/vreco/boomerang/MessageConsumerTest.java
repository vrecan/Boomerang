package com.vreco.boomerang;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import com.vreco.boomerang.message.Message;
import com.vreco.boomerang.message.MockMessage;
import java.io.IOException;
import java.util.HashMap;
import javax.jms.JMSException;
import org.junit.*;

/**
 *
 * @author Ben Aldrich
 */
public class MessageConsumerTest {

  Conf conf;

  public MessageConsumerTest() {
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
  }

  @After
  public void tearDown() {
  }

  /**
   * Test of run method, of class MessageConsumer.
   */
  @Test
  public void testRun() throws IOException, JMSException {
    String forwardQueue = "TestMessageConsumer1, TestMessageConsumer2";
    HashMap<Thread, Long> threads = Main.getThreads(conf);
    Main.startThreads(threads);
    Message msg = MockMessage.getBasicMessage(conf, forwardQueue);
    TestUtil.sendBoomerangMessage(msg, conf);
    Message ConsumeMessage1 = TestUtil.ConsumeMessage("TestMessageConsumer1", conf);
    Message ConsumeMessage2 = TestUtil.ConsumeMessage("TestMessageConsumer2", conf);
    Assert.assertNotNull(ConsumeMessage1);
    Assert.assertNotNull(ConsumeMessage2);
    Assert.assertEquals(ConsumeMessage1.getJsonStringMessage(), ConsumeMessage2.getJsonStringMessage());
  }
}