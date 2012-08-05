package com.vreco.boomerang.response;


import com.vreco.boomerang.message.ResponseMessage;
import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.conf.MockConf;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.*;

/**
 *
 * @author Ben Aldrich
 */
public class ResponseConsumerTest {
  ObjectMapper mapper = new ObjectMapper();
  final protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
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
    }

    @After
    public void tearDown() {
    }


  /**
   * Test of processResponse method, of class ResponseConsumer.
   */
  @Test
  public void testProcessResponseSuccess() throws JsonParseException, JsonMappingException, IOException {
    ResponseConsumer rc = new ResponseConsumer(conf);
    Date date = new Date();
    String sDate = sdf.format(date);
    String json = "{\"uuid\":\"afer35236236\",\"boomDate\":\"" + sDate + "\","
                  + "\"success\":\"true\",\"resetTimer\":\"false\"}";
    rc.processResponse(mapper.readValue(json, ResponseMessage.class)); 
  }

}