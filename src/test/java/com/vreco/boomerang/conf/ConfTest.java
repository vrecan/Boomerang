package com.vreco.boomerang.conf;

import java.io.IOException;
import java.util.Properties;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author Ben Aldrich
 */
public class ConfTest {

    public ConfTest() {
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
   * Test of getValue method, of class Conf.
   */
  @Test
  public void testGetValue_String() throws IOException {
    Properties prop = new Properties();
    prop.setProperty("key", "woo");
    Conf conf = new Conf(prop);
    Assert.assertEquals(conf.getValue("key"), prop.getProperty("key"));
  }

  /**
   * Test of getValue method, of class Conf.
   */
  @Test
  public void testGetValue_String_Default() throws IOException {
    Properties prop = new Properties();
    Conf conf = new Conf(prop);
    Assert.assertEquals(conf.getValue("key", "default"), "default");   

  }
  /**
   * Test of getIntValue method, of class Conf.
   */
  @Test
  public void testGetIntValue() throws IOException {
    Properties prop = new Properties();
    prop.setProperty("key", "25");    
    Conf conf = new Conf(prop);
    Assert.assertEquals(conf.getIntValue("key", 200), 25);   
  }
  
  /**
   * Test of getIntValue method, of class Conf.
   */
  @Test
  public void testGetIntValueDefault() throws IOException {
    Properties prop = new Properties();
    prop.setProperty("key", "woo");    
    Conf conf = new Conf(prop);
    Assert.assertEquals(conf.getIntValue("key", 200), 200);   
  }  

}