package com.vreco.boomerang;

import java.text.SimpleDateFormat;
import java.util.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

/**
 * Store messages.
 *
 * @author Ben Aldrich
 */
public class RedisStore implements DataStore, AutoCloseable {

  final Jedis jedis;
  final String appName;
  final protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

  public RedisStore(String connectionHostName, String appName) {
    jedis = new Jedis(connectionHostName);
    this.appName = appName;
  }

  @Override
  public void set(String propKey, String value, Date date) {
    jedis.hset(getHashKey(appName, date), propKey, value);
  }

  @Override
  public void batchSet(Map<String, String> batch) {
    Pipeline pipe = jedis.pipelined();
    ArrayList<Response<Long>> responses = new ArrayList();
    for (Map.Entry<String, String> message : batch.entrySet()) {
      //responses.add(pipe.hset(getHashKey(appName, date), message.getKey(), message.getValue()));
    }

    pipe.sync();

    for (Response<Long> r : responses) {
      if (r.get().intValue() != 1) {
        //throw new IOException("Failed to insert batch!");
      }
    }

  }

  @Override
  public String get(String fieldKey, Date date) {
    List<String> hvals = jedis.hmget(getHashKey(appName, date), fieldKey);
    if (hvals.isEmpty()) {
      return null;
    }
    return hvals.get(0);
  }

  @Override
  public String get(String HashKey, String fieldKey) {
    List<String> hvals = jedis.hmget(HashKey, fieldKey);
    if (hvals.isEmpty()) {
      return null;
    }
    return hvals.get(0);

  }

  @Override
  public void delete(String fieldKey, Date date) {
    jedis.hdel(getHashKey(appName, date), fieldKey);
  }

  @Override
  public Set<String> getKeys(Date date) {
    return jedis.hkeys(getHashKey(appName, date));
  }

  @Override
  public boolean exists(String fieldKey, Date date) {
    return jedis.hexists(getHashKey(appName, date), fieldKey);
  }

  public void deleteAll() {
    jedis.flushDB();
  }

  protected String getHashKey(String prefix, Date date) {
    StringBuilder sb = new StringBuilder(prefix);
    sb.append(sdf.format(date));
    return sb.toString();
  }

  @Override
  public void close() {
    jedis.disconnect();
  }
}