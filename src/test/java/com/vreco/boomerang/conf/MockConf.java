package com.vreco.boomerang.conf;

import java.io.IOException;
import java.util.Properties;

/**
 * Mock configuration file.
 *
 * @author Ben Aldrich
 */
public class MockConf {
  public Conf conf;
  
  public MockConf() throws IOException {
    Properties prop = new Properties();
    prop.setProperty("mq.connection.url", "vm://localhost?broker.persistent=false");
//    prop.setProperty("mq.connection.url", "tcp://localhost:61616?jms.prefetchPolicy.all=5");    
    prop.setProperty("app.name", "boomerang");
    prop.setProperty("mq.connection.timeout","2000");
    prop.setProperty("mq.processing.queue", "boomerangQ");
    prop.setProperty("data.redis.url", "localhost");
    prop.setProperty("mq.response.queue", "boomerangResponseQ");
    
    //Message labels
    prop.setProperty("boomerang.date.label", "boomDate");
    prop.setProperty("boomerang.uuid.label", "boomUuid");
    prop.setProperty("boomerang.retry.label", "boomRetry");   
    prop.setProperty("boomerang.producer.label", "boomQueues");
    
    //Response conf options
    prop.setProperty("boomerang.response.queue.label", "boomRQueue");
    prop.setProperty("boomerang.response.success.label", "boomSuccess");

    prop.setProperty("boomerang.resend.default", "200");
    prop.setProperty("boomerang.producer.ttl.default", "60000");
    prop.setProperty("boomerang.resend.retry", "2");
    conf = new Conf(prop);
  }
  
  public void setValue(String key, String value) {
    conf.setValue(key, value);
  }

}
