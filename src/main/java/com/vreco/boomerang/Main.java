package com.vreco.boomerang;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.response.ResponseConsumer;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Ben Aldrich
 */
public final class Main {

  Logger logger = LoggerFactory.getLogger(Main.class);

  public Main() {
  }

  protected void start() throws JMSException {
    SimpleShutdown shutdown = SimpleShutdown.getInstance();

    HashMap<Thread, Long> threads = new HashMap();
    try {
      Runtime.getRuntime().addShutdownHook(shutdown);
      Conf conf = new Conf("conf/boomerang.conf");

      logger.info("starting Threads.");
      threads = getThreads(conf);
      startThreads(threads);
    } catch (Throwable e) {
      shutdown.setShutdown(true);
      logger.error("Caught unexpected exception, exiting.", e);

    }

    while (!shutdown.isShutdown()) {
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        logger.warn("Interrupted sleep", e);
      }
    }

    gracefulShutdown(shutdown, threads);
    logger.info("Finished shutdown");
  }

  /**
   * Gracefully shutdown our threads.
   *
   * @param shutdown
   */
  protected void gracefulShutdown(final SimpleShutdown shutdown, final HashMap<Thread, Long> threads) {
    shutdown.setShutdown(true);
    logger.info("starting graceful shutdown...");
    try {
      joinThreads(threads);
    } catch (Exception e) {
      logger.error("failed to join thread", e);
    }
    shutdown.setFinished(true);
  }

  public static HashMap<Thread, Long> getThreads(Conf conf) {
    HashMap<Thread, Long> threads = new HashMap();
    threads.put(new Thread(new MessageConsumer(conf)), Long.parseLong("10000"));
    threads.put(new Thread(new ResponseConsumer(conf)), Long.parseLong("10000"));
    threads.put(new Thread(new ResendExpired(conf)), Long.parseLong("10000"));
    return threads;
  }

  /**
   * Start threads.
   *
   * @param threads
   */
  public static void startThreads(HashMap<Thread, Long> threads) {
    for (Map.Entry<Thread, Long> map : threads.entrySet()) {
      map.getKey().start();
    }
  }

  /**
   * Join threads.
   *
   * @param threads
   */
  public static void joinThreads(HashMap<Thread, Long> threads) throws InterruptedException {
    for (Map.Entry<Thread, Long> map : threads.entrySet()) {
      map.getKey().join(map.getValue());
    }
  }

  public static void main(final String args[]) throws JMSException {
    new Main().start();
  }
}