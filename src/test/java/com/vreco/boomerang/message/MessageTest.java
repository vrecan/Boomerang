package com.vreco.boomerang.message;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public class MessageTest extends TestCase {

  Conf conf;

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
    ObjectMapper mapper = new ObjectMapper();
    String json = "{\"test\":\"proc\",\"something\":\"uuid\", \"" + conf.getValue("boomerang.producer.label")
            + "\":\"test\"}";
    HashMap<String, Object> hMsg = mapper.readValue(json, HashMap.class);
    Message msg = new Message(hMsg, conf);
    Assert.assertEquals("test", msg.getDestination());
    Assert.assertNotNull(msg.getUuid());
  }

  /**
   * Test to see if date already exist in the message we should use it.
   */
  public void testMessageWithExistingDate() throws Exception {

    String expDate = "2012081115584545";

    ObjectMapper mapper = new ObjectMapper();
    String json = "{\"test\":\"proc\",\"something\":\"uuid\", \"" + conf.getValue("boomerang.producer.label")
            + "\":\"test\", \"" + conf.getValue("boomerang.date.label") + "\":\"" + expDate + "\"}";
    HashMap<String, Object> hMsg = mapper.readValue(json, HashMap.class);
    Message msg = new Message(hMsg, conf);
    Assert.assertEquals("test", msg.getDestination());
    Assert.assertNotNull(msg.getUuid());
    Date exp = msg.msgDateFormat.parse(expDate);
    Assert.assertEquals(exp, msg.getDate());

  }

  /**
   * Test to see if date already exist in the message we should use it.
   */
  public void testMessageWithExistingUUID() throws Exception {

    String exp= "abc-2352627-6";

    ObjectMapper mapper = new ObjectMapper();
    String json = "{\"test\":\"proc\",\"something\":\"uuid\", \"" + conf.getValue("boomerang.producer.label")
            + "\":\"test\", \"" + conf.getValue("boomerang.uuid.label") + "\":\"" + exp + "\"}";
    HashMap<String, Object> hMsg = mapper.readValue(json, HashMap.class);
    Message msg = new Message(hMsg, conf);
    Assert.assertEquals("test", msg.getDestination());
    Assert.assertNotNull(msg.getUuid());
    Assert.assertEquals(exp, msg.getUuid());

  }
}
