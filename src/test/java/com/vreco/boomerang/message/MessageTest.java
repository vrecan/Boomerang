package com.vreco.boomerang.message;

import java.io.IOException;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public class MessageTest extends TestCase {

  public MessageTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testCreateExpectedMsg() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{\"processName\":\"proc\",\"uuid\":\"uuid\"}";
    Message msg = mapper.readValue(jsonString, Message.class);
    Assert.assertEquals("proc", msg.getProcessName());
    Assert.assertEquals("uuid", msg.getUUID());
  }
  
  public void testCreateMsgExtraFields() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{\"processName\":\"proc\",\"uuid\":\"uuid\", \"something\":\"new\"}";
    Message msg = mapper.readValue(jsonString, Message.class);
    Assert.assertEquals("proc", msg.getProcessName());
    Assert.assertEquals("uuid", msg.getUUID());
  }
  
  public void testCreateMsgMissingUuid() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    String jsonString = "{\"processName\":\"proc\",\"something\":\"new\"}";
    Message msg = mapper.readValue(jsonString, Message.class);
    Assert.assertEquals("proc", msg.getProcessName());
  }  
  
}
