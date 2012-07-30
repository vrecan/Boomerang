package com.vreco.boomerang;

import com.vreco.boomerang.conf.Conf;
import com.vreco.util.shutdownhooks.SimpleShutdown;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Ben Aldrich
 */
public final class Main {

  private Logger logger;

  public Main() {
  }

  protected void start() throws JMSException {
    SimpleShutdown shutdown = SimpleShutdown.getInstance();

    Conf conf = null;
    HashMap<Thread, Long> threads = new HashMap();
    try {
      Runtime.getRuntime().addShutdownHook(shutdown);
      conf = new Conf("conf/boomerang.conf");
      logger = getLog4J(conf);

      logger.info("starting Threads.");
      threads = getThreads();
      startThreads(threads);
    } catch (Throwable e) {
      shutdown.setShutdown(true);
      System.out.print(e);
      System.out.println("Caught unexpected exception, exiting");
      if (logger != null) {
        logger.fatal("Caught unexpected exception, exiting.", e);
      } else {
        System.out.println("Caught unexpected exception, exiting.");
        System.out.print(e.getStackTrace().toString());
      }
    }
    while (!shutdown.isShutdown()) {
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        logger.warn("Interrupted sleep", e);
      }
    }

    gracefulShutdown(shutdown, threads);
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

  public static HashMap<Thread, Long> getThreads() {
    HashMap<Thread, Long> threads = new HashMap();
    threads.put(new Thread(new CheckAndUpdate()), Long.parseLong("30000"));
    threads.put(new Thread(new QueueConsumer()), Long.parseLong("10000"));
    threads.put(new Thread(new ResendExpired()), Long.parseLong("10000"));
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

  /**
   * Load log4j config.
   *
   * @throws ComplianceException
   */
  protected Logger getLog4J(final Conf conf) throws IOException {
    File log4jFile = new File(conf.getValue("log4j.conf", "conf/log4j.conf"));
    if (!log4jFile.exists()) {
      throw new IOException("No log4j conf file found: " + log4jFile.toString());
    }
    PropertyConfigurator.configureAndWatch(log4jFile.getAbsolutePath());
    return logger = Logger.getLogger(Main.class);
  }

  public static void main(final String args[]) throws JMSException {
    new Main().start();
  }
}