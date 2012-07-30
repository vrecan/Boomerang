package com.vreco.boomerang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * Store messages.
 *
 * @author Ben Aldrich
 */
public class StoreMessage implements AutoCloseable{

  Jedis jedis;
  String hostname;

  public StoreMessage(String serverHostname, String hostname) {
    jedis = new Jedis(serverHostname);
    this.hostname = hostname;
  }

  public void set(String key, String value) {
    jedis.hset(hostname, key, value);
  }
  
  public void batchSet(Map<String, String> batch) {
    Pipeline pipe = jedis.pipelined();
    ArrayList<Response<Long>> responses = new ArrayList();
    for (Map.Entry<String, String> message : batch.entrySet()) {
      responses.add(pipe.hset(hostname, message.getKey(), message.getValue()));
    }
    pipe.sync();
    pipe.exec();
    for(Response<Long> r : responses) {
      r.get();
    }

  }

  public String get(String key) {
    List<String> hvals = jedis.hmget(hostname, key);
    if(hvals.isEmpty()) {
      return null;
    }
    return hvals.get(0);
  }

  public void delete(String key) {
    jedis.hdel(hostname, key);
  }
  
  public Set<String> getKeys() {
    return jedis.hkeys(hostname);
  }
  
  public boolean exists(String key) {
    return jedis.hexists(hostname, key);
  }
  
  public void deleteAll() {
    jedis.flushDB();
  }
  
  @Override
  public void close() {
    jedis.disconnect();
  }
}