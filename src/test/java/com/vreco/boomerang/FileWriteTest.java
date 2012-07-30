/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vreco.boomerang;

import com.vreco.boomerang.message.Message;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.*;

/**
 *
 * @author baldrich
 */
public class FileWriteTest {
  
  public FileWriteTest() {
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
   * Test of getInstance method, of class FileWrite.
   */
  @Test
  public void testGetInstance() {
    FileWrite fileWrite = FileWrite.getInstance();
  }

  /**
   * Test of write method, of class FileWrite.
   */
  @Test
  public void testWrite() throws Exception {
    FileWrite fileWrite = FileWrite.getInstance();
    //fileWrite.write(null, null);
  }
}
