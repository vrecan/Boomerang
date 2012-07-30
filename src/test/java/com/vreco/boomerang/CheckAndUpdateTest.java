package com.vreco.boomerang;

import java.util.HashMap;
import junit.framework.TestCase;

/**
 *
 * @author Ben Aldrich
 */
public class CheckAndUpdateTest extends TestCase {
    
    public CheckAndUpdateTest(String testName) {
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

  /**
   * Test of run method, of class CheckAndUpdate.
   */
  public void testRun() throws InterruptedException {
    HashMap<Thread, Long> threads = new HashMap();
    threads.put(new Thread(new CheckAndUpdate()), Long.parseLong("30000"));
    Main.startThreads(threads);
    Main.joinThreads(threads);
  }

}
