package com.vreco.boomerang;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.*;

/**
 *
 * @author Ben Aldrich
 */
public class StoreMessageTest {
  StoreMessage store;

    public StoreMessageTest() {
    }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

    @Before
    public void setUp() {
      store = new StoreMessage("localhost", "superslack");
    }

    @After
    public void tearDown() {
    }

  /**
   * Test of set method, of class StoreMessage.
   */
  @Test
  public void testPut() {

  }

  /**
   * Test of get method, of class StoreMessage.
   */
  @Test
  public void testSimple() {
    store.set("woo", "woov");
    String result = store.get("woo");
    store.delete("woo");
    Assert.assertEquals(result, "woov");
  }

  /**
   * Test of getKeys method, of class StoreMessage.
   */
  @Test
  public void testGetKeysSimple() {
    for(int i=0; i <= 2000; i++) {
      String k = UUID.randomUUID().toString();
      //store.set(k, "{\"processName\":\"proc\",\"uuid\":\"" + k + "\"}");
    }
    //Set<String> keys = store.getKeys();
//    store.delete("1");
//    store.delete("2");
//    store.delete("3");
//    store.delete("4");
//    store.delete("5");
//    store.delete("6");
    
  }
  
  /**
   * Test of getKeys method, of class StoreMessage.
   */
  @Test
  public void testGetKeysSimpleBatch() {
    HashMap<String, String> batch = new HashMap();
    for(int i=0; i <= 20000; i++) {
      String k = UUID.randomUUID().toString();
      batch.put(k, "{\"processName\":\"proc\",\"uuid\":\"" + k + "\"}");
    }
    store.batchSet(batch);
  }  

  /**
   * Test of exists method, of class StoreMessage.
   */
  @Test
  public void testExists() {
    store.set("1", "woov");
    Assert.assertTrue(store.exists("1"));
    store.delete("1");
  }
  
  @Test
  public void testExistsFalse() {
    Assert.assertFalse(store.exists("1"));
  }

}