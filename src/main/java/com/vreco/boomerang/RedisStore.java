package com.vreco.boomerang;

import com.vreco.boomerang.message.Message;
import com.vreco.boomerang.message.ResponseMessage;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.codehaus.jackson.map.ObjectMapper;
import redis.clients.jedis.Jedis;

/**
 * Store messages in Redis.
 *
 * @author Ben Aldrich
 */
public class RedisStore implements DataStore, AutoCloseable {

  final Jedis jedis;
  final String appName;
  final ObjectMapper mapper = new ObjectMapper();
  final protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

  /**
   * Initialize data store.
   *
   * @param connectionHostName
   * @param appName
   */
  public RedisStore(String connectionHostName, String appName) {
    jedis = new Jedis(connectionHostName);
    this.appName = appName;
  }

//  @Override
//  public void batchSet(Map<String, String> batch) {
//    Pipeline pipe = jedis.pipelined();
//    ArrayList<Response<Long>> responses = new ArrayList();
//    for (Map.Entry<String, String> message : batch.entrySet()) {
//      //responses.add(pipe.hset(getHashKey(appName, date), message.getKey(), message.getValue()));
//    }
//
//    pipe.sync();
//
//    for (Response<Long> r : responses) {
//      if (r.get().intValue() != 1) {
//        //throw new IOException("Failed to insert batch!");
//      }
//    }
//
//  }
  @Override
  public Set<String> getKeys(Date date) {
    return jedis.hkeys(getHashKey(appName, date));
  }

  /**
   * Delete all data in the db.
   */
  public void deleteAll() {
    jedis.flushDB();
  }

  /**
   * Build hash key.
   *
   * @param prefix
   * @param date
   * @return new hash key.
   */
  protected String getHashKey(String prefix, Date date) {
    StringBuilder sb = new StringBuilder(prefix);
    sb.append(sdf.format(date));
    return sb.toString();
  }

  /**
   * Close / disconnect from our data store.
   */
  @Override
  public void close() {
    jedis.disconnect();
  }

  /**
   * Get our stored message from redis.
   *
   * @param msg
   * @return
   * @throws ParseException
   */
  @Override
  public String get(ResponseMessage msg) throws ParseException {
    List<String> hvals;
    hvals = jedis.hmget(getHashKey(appName, sdf.parse(msg.getDate())), msg.getUuid());

    if (hvals.isEmpty()) {
      return null;
    }
    return hvals.get(0);

  }

  /**
   * Delete our stored message.
   *
   * @param msg
   * @throws ParseException
   */
  @Override
  public void delete(ResponseMessage msg) throws ParseException {
    jedis.hdel(getHashKey(appName, sdf.parse(msg.getDate())), msg.getUuid());
  }

  /**
   * Check to see if a message exists in the data store.
   *
   * @param msg
   * @return
   * @throws ParseException
   */
  @Override
  public boolean exists(ResponseMessage msg) throws ParseException {
    return jedis.hexists(getHashKey(appName, sdf.parse(msg.getDate())), msg.getUuid());
  }

  /**
   * Add a message to the data store.
   *
   * @param msg
   * @throws IOException
   */
  @Override
  public void set(Message msg) throws IOException {
    try {
      System.out.println("setting: " + getHashKey(appName, msg.getDate()).toString());
      jedis.hset(getHashKey(appName, msg.getDate()), msg.getUuid(), mapper.writeValueAsString(msg.getMsg()));
    } catch (Exception e) {
      throw new IOException("Failed to set message in store.", e);
    }
  }

  /**
   * Add a collection of messages in the data store.
   *
   * @param msgs
   */
  @Override
  public void batchSet(Collection<Message> msgs) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * Check to see if a message exists in the data store.
   *
   * @param msg
   * @return
   */
  @Override
  public boolean exists(Message msg) {
    return jedis.hexists(getHashKey(appName, msg.getDate()), msg.getUuid());
  }
}