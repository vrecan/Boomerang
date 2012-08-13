package com.vreco.boomerang.message;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 *
 * @author Ben Aldrich
 */
public class MessageTest extends TestCase {

  Conf conf;
  protected final SimpleDateFormat msgDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

  public MessageTest(String testName) {
    super(testName);

  }

  @Override
  protected void setUp() throws Exception {
    conf = new MockConf().conf;
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testCreateExpectedMsg() throws IOException {

    String json = "{\"test\":\"proc\",\"something\":\"uuid\", \"" + conf.getValue("boomerang.producer.label")
            + "\":\"test\"}";
    BoomerangMessage msg = new BoomerangMessage(json, conf);
    Assert.assertEquals("test", msg.getDestination());
    Assert.assertNotNull(msg.getUuid());
  }

  /**
   * Test to see if date already exist in the message we should use it.
   */
  public void testMessageWithExistingDate() throws Exception {

    String expDate = "2012081115584545";


    String json = "{\"test\":\"proc\",\"something\":\"uuid\", \"" + conf.getValue("boomerang.producer.label")
            + "\":\"test\", \"" + conf.getValue("boomerang.date.label") + "\":\"" + expDate + "\"}";
    BoomerangMessage msg = new BoomerangMessage(json, conf);
    Assert.assertEquals("test", msg.getDestination());
    Assert.assertNotNull(msg.getUuid());
    Date exp = msg.msgDateFormat.parse(expDate);
    Assert.assertEquals(exp, msg.getDate());

  }

  /**
   * Test to see if date already exist in the message we should use it.
   */
  public void testMessageWithExistingUUID() throws Exception {

    String exp = "abc-2352627-6";


    String json = "{\"test\":\"proc\",\"something\":\"uuid\", \"" + conf.getValue("boomerang.producer.label")
            + "\":\"test\", \"" + conf.getValue("boomerang.uuid.label") + "\":\"" + exp + "\"}";
    BoomerangMessage msg = new BoomerangMessage(json, conf);
    Assert.assertEquals("test", msg.getDestination());
    Assert.assertNotNull(msg.getUuid());
    Assert.assertEquals(exp, msg.getUuid());

  }

  /**
   * Test to see if date already exist in the message we should use it.
   */
  public void testMessageSuccessResponse() throws Exception {
    Date Date = new Date();
    ResponseMessage basicResponseSuccess = MockMessage.getBasicResponseSuccess(conf, msgDateFormat.format(Date));
    Assert.assertTrue(basicResponseSuccess.isSuccess());
  }

  public void testRawResponseSuccess() throws Exception {
    String json = "{\"boomUuid\":\"0ca14d7a-8b90-42a1-a4ea-999bfcd52d5e\",\"boomQueues\":\"fullcycleQ\",\"boomSuccess\":true,\"boomDate\":\"20120812155553726\",\"boomRetry\":0}";
    ResponseMessage msg = new ResponseMessage(json, conf);
    Assert.assertTrue(msg.isSuccess());
  }
}
