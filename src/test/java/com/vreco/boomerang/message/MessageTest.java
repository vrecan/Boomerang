package com.vreco.boomerang.message;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import java.io.IOException;
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

}
