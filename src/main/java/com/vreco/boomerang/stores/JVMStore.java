package com.vreco.boomerang.stores;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.vreco.boomerang.message.Message;

/**
 * @author Michael Golowka
 */
public class JVMStore implements Storage {

  protected ConcurrentHashMap<String, Message>                uuidMap;
  protected ConcurrentSkipListMap<Long, Map<String, Message>> timestampMap;

  /**
   * Creates a new in-JVM memory store for messages.
   */
  public JVMStore() {
    uuidMap = new ConcurrentHashMap<>();
    timestampMap = new ConcurrentSkipListMap<>();
  }

  @Override
  public void set(final Message message, final Message... messages) {
    if (message == null) {
      throw new NullPointerException("Message is null");
    }

    storeMessage(message);

    if (messages != null) {
      for (Message msg : messages) {
        storeMessage(msg);
      }
    }
  }

  protected void storeMessage(final Message message) {
    if (message == null) {
      // Ignore message
      return;
    }

    String uuid = message.getUUID();
    if (message.getTimestamp() == null) {
      Date timestamp = new Date();
      message.setTimestamp(timestamp);
    }

    uuidMap.put(uuid, message);

    Map<String, Message> messages = timestampMap.get(message.getTimestamp().getTime());
    if (messages == null) {
      messages = new HashMap<>();
      timestampMap.put(message.getTimestamp().getTime(), messages);
    }
    messages.put(uuid, message);
  }

  @Override
  public void set(final Collection<Message> messages) {
    if (messages == null) {
      throw new NullPointerException("Message collection is null");
    }

    for (Message msg : messages) {
      storeMessage(msg);
    }
  }

  @Override
  public void delete(final String uuid, final String... uuids) {
    if (uuid == null) {
      throw new NullPointerException("UUID is null");
    }

    deleteMessage(uuid);
  }

  protected void deleteMessage(final String uuid) {
    if (uuid == null) {
      throw new NullPointerException("UUID is null");
    }

    Message msg = uuidMap.get(uuid);
    if (msg == null) {
      return;
    }

    uuidMap.remove(uuid);

    Date timestamp = msg.getTimestamp();
    Map<String, Message> messages = timestampMap.get(timestamp.getTime());
    if (messages == null) {
      return;
    }

    messages.remove(uuid);

    if (messages.isEmpty()) {
      timestampMap.remove(timestamp.getTime());
    }
  }

  @Override
  public int deleteOlderThan(final Date timestamp) {
    ConcurrentNavigableMap<Long, Map<String, Message>> mapping = timestampMap.headMap(
        timestamp.getTime(), true);
    int count = mapping.size();

    // TODO Finish method

    return count;
  }

  @Override
  public int deleteTimestamp(final Date timestamp) {
    if (timestamp == null) {
      return 0;
    }

    Map<String, Message> mapping = timestampMap.get(timestamp.getTime());

    if (mapping == null) {
      return 0;
    }

    int count = mapping.entrySet().size();

    for (Entry<String, Message> entry : mapping.entrySet()) {
      uuidMap.remove(entry.getKey());
    }

    timestampMap.remove(timestamp.getTime());

    return count;
  }

  @Override
  public Message getMessage(final String uuid) {
    // TODO Finish method
    return null;
  }

  @Override
  public Iterator<Message> getMessagesOlderThan(final Date timestamp) {
    // TODO Finish method
    return null;
  }
}
