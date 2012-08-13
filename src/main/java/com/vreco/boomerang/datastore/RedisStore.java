package com.vreco.boomerang.datastore;

import com.vreco.boomerang.conf.Conf;
import com.vreco.boomerang.message.Message;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;

/**
 * Store messages in Redis.
 *
 * @author Ben Aldrich
 */
public class RedisStore implements DataStore, AutoCloseable {

  final Jedis jedis;
  final String appName;
  final Logger logger = LoggerFactory.getLogger(RedisStore.class);
  final protected SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
  private final Conf conf;

  /**
   * Initialize data store.
   *
   * @param connectionHostName
   * @param appName
   */
  public RedisStore(Conf conf) {
    jedis = new Jedis(conf.getValue("data.redis.url"));
    this.appName = conf.getValue("app.name");
    this.conf = conf;
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
  /**
   * Get our stored message from redis.
   *
   * @param msg
   * @return
   * @throws ParseException
   */
  @Override
  public String get(final Message msg) throws ParseException {
    final String key = getHashKey(appName, msg.getUuid(), msg.getDate());
    return jedis.hget(key, msg.getUuid());
  }
  
  /**
   * Get our failed stored message from redis.
   *
   * @param msg
   * @return
   * @throws ParseException
   */
  @Override
  public String getFailed(final Message msg) throws ParseException {
    final String key = getFailedHashKey(appName, msg.getUuid(), msg.getDate());
    return jedis.hget(key, msg.getUuid());
  }  

  @Override
  public Collection<Message> getLastNMessages(final int n) throws IOException, ParseException {
    ArrayList<Message> msgs = new ArrayList();
    Set<String> zrange = jedis.zrange(getZKey(), 0, n);
    for (String dateAndID : zrange) {
      String[] split = dateAndID.split(":");
      String key = getHashKey(appName, split[1], new Date(Long.parseLong(split[0])));
      String jsonMsg = jedis.hget(key, split[1]);
      if (jsonMsg != null) {
        msgs.add(new Message(jsonMsg, conf));
      }
    }
    return msgs;
  }

  /**
   * Check to see if a message exists in the data store.
   *
   * @param msg
   * @return
   * @throws ParseException
   */
  @Override
  public boolean exists(Message msg) throws ParseException {
    final String key = getHashKey(appName, msg.getUuid(), msg.getDate());
    final String zkey = getZKey();
    final String zvalue = getZValue(msg.getDate(), msg.getUuid());
    final boolean hexists = jedis.hexists(key, msg.getUuid());
    final boolean zexists = jedis.zscore(zkey, zvalue) != null;
    if (hexists || zexists) {
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * Check to see if a message exists in the data store.
   *
   * @param msg
   * @return
   * @throws ParseException
   */
  @Override
  public boolean existsFailed(Message msg) throws ParseException {
    final String key = getFailedHashKey(appName, msg.getUuid(), msg.getDate());
    final String zkey = getFailedZKey();
    final String zvalue = getZValue(msg.getDate(), msg.getUuid());
    final boolean hexists = jedis.hexists(key, msg.getUuid());
    final boolean zexists = jedis.zscore(zkey, zvalue) != null;
    if (hexists || zexists) {
      return true;
    } else {
      return false;
    }
  }  

  /**
   * Add a message to the data store.
   *
   * @param msg
   * @throws IOException
   */
  @Override
  public void set(final Message msg) throws IOException {
    try {
      ArrayList<Response<Long>> responses = new ArrayList(2);
      final Date date = msg.getDate();
      final String key = getHashKey(appName, msg.getUuid(), date);
      final String zValue = getZValue(date, msg.getUuid());
      final String zKey = getZKey();

      Transaction t = jedis.multi();
      logger.debug("setting: {}", key);
      logger.debug("zkey: {}", zKey);
      logger.debug("zValue: {}", zValue);
      responses.add(t.hset(key, msg.getUuid(), msg.getJsonStringMessage()));
      responses.add(t.zadd(zKey.getBytes("UTF8"), 0, zValue.getBytes("UTF8")));
      t.exec();
      for (Response<Long> r : responses) {
        logger.debug("set & zadd responses: {}", r.get());
      }
    } catch (RuntimeException e) {
      throw new IOException("Failed to set message in store.", e);
    }
  }

  @Override
  public void setFailed(final Message msg) throws IOException {
    try {
      ArrayList<Response<Long>> responses = new ArrayList(2);
      final Date date = msg.getDate();
      final String key = getFailedHashKey(appName, msg.getUuid(),date);
      final String zValue = getZValue(date, msg.getUuid());
      final String zKey = getFailedZKey();

      Transaction t = jedis.multi();
      logger.debug("setting: {}", key);
      logger.debug("zkey: {}", zKey);
      logger.debug("zValue: {}", zValue);
      responses.add(t.hset(key, msg.getUuid(), msg.getJsonStringMessage()));
      responses.add(t.zadd(zKey.getBytes("UTF8"), 0, zValue.getBytes("UTF8")));
      t.exec();
      for (Response<Long> r : responses) {
        logger.debug("set & zadd responses: {}", r.get());
      }
    } catch (RuntimeException e) {
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
   * Delete our stored message.
   *
   * @param msg
   * @throws ParseException
   */
  @Override
  public void delete(Message msg) throws ParseException {
    ArrayList<Response<Long>> responses = new ArrayList(2);
    Transaction t = jedis.multi();
    final String key = getHashKey(appName, msg.getUuid(), msg.getDate());
    final String zkey = getZKey();
    final String zvalue = getZValue(msg.getDate(), msg.getUuid());
    responses.add(t.zrem(zkey, zvalue));
    responses.add(t.hdel(key, msg.getUuid()));
    t.exec();
      for (Response<Long> r : responses) {
        logger.debug("zrem & hdel responses: {}", r.get());
      }    
  }
  
  /**
   * Delete our stored message.
   *
   * @param msg
   * @throws ParseException
   */
  @Override
  public void deleteFailed(Message msg) throws ParseException {
    ArrayList<Response<Long>> responses = new ArrayList(2);
    Transaction t = jedis.multi();
    final String key = getFailedHashKey(appName, msg.getUuid(), msg.getDate());
    final String zkey = getFailedZKey();
    final String zvalue = getZValue(msg.getDate(), msg.getUuid());
    responses.add(t.zrem(zkey, zvalue));
    responses.add(t.hdel(key, msg.getUuid()));
    t.exec();
      for (Response<Long> r : responses) {
        logger.debug("zrem & hdel responses: {}", r.get());
      }    
  }  

  /**
   * Delete all data in the db.
   */
  public void deleteAll() {
    jedis.flushDB();
  }

  protected long zSize() {
    return jedis.zcard(getZKey());
  }

  protected String getZKey() {
    StringBuilder sb = new StringBuilder(appName);
    sb.append(":uuidByDate");
    return sb.toString();
  }

  protected String getFailedZKey() {
    StringBuilder sb = new StringBuilder(appName);
    sb.append(":failedUuidByDate");
    return sb.toString();
  }

  protected String getZValue(Date date, String uuid) {
    StringBuilder sb = new StringBuilder();
    sb.append(date.getTime());
    sb.append(":");
    sb.append(uuid);
    return sb.toString();
  }

  /**
   * Build hash key.
   *
   * @param prefix
   * @param date
   * @return new hash key.
   */
  protected String getHashKey(String prefix, String uuid, Date date) {
    StringBuilder sb = new StringBuilder(prefix);
    sb.append(":");
    sb.append(uuid);
    sb.append(":");
    sb.append(dateFormat.format(date));
    return sb.toString();
  }

  /**
   * Build hash key.
   *
   * @param prefix
   * @param date
   * @return new hash key.
   */
  protected String getFailedHashKey(String prefix, String uuid, Date date) {
    StringBuilder sb = new StringBuilder("failed");
    sb.append(":");
    sb.append(prefix);
    sb.append(":");
    sb.append(uuid);
    sb.append(":");
    sb.append(dateFormat.format(date));
    return sb.toString();
  }

  /**
   * Close / disconnect from our data store.
   */
  @Override
  public void close() {
    jedis.disconnect();
  }

}