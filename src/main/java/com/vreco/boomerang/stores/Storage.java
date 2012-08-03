package com.vreco.boomerang.stores;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.vreco.boomerang.message.Message;

public interface Storage {
  /**
   * Records the message in the data store. If a record already exists with the same uuid, setting
   * it again will overwrite the previous entry.
   *
   * @param message
   *          The message to set in the data store. This may not be null.
   * @param messages
   *          Additional messages to set in the data store. This may be empty or null.
   * @throws NullPointerException
   *           If message is null
   */
  public void set(Message message, Message... messages);

  /**
   * This is equivalent to calling set(Message, Message...).
   *
   * @param messages
   *          Collection of message to be set in the data store. This may not be null. This may be
   *          empty, but no actions will be taken
   * @throws NullPointerException
   *           If messages is null
   */
  public void set(Collection<Message> messages);

  /**
   * Deletes the given uuid from the data store. This allows for additional
   *
   * @param uuid
   *          The uuid of the message to delete from the data store. This may not be null.
   * @param uuids
   *          Additional uuids to delete from the data store. This may be empty or null
   * @throws NullPointerException
   *           If uuid is null or empty
   */
  public void delete(String uuid, String... uuids);

  /**
   * Deletes all messages that have a timestamp less than or equal to the given timestamp
   *
   * @param timestamp
   * @return
   * @throws NullPointerException
   *           If timestamp is null
   */
  public int deleteOlderThan(Date timestamp);

  /**
   * Deletes all messages that have the exact same timestamp from the data store.
   *
   * @param timestamp
   *          The timestamp of the message(s) to delete. This may not be null
   * @return The number of messages deleted
   * @throws NullPointerException
   *           If timestamp is null
   */
  public int deleteTimestamp(Date timestamp);

  /**
   * Fetches the message from the data store associated with the uuid. If no message is found, this
   * will return null.
   *
   * @param uuid
   *          The unique ID of the message to find in the data store. This may not be null.
   * @return A message with the uuid specified, or null if no message found.
   * @throws NullPointerException
   *           If uuid is null or empty
   */
  public Message getMessage(String uuid);

  /**
   * Creates an iterator of messages to fetch all of the messages that have a timestamp less than or
   * equal to the given timestamp.<br>
   * <br>
   * <b>WARNING:</b> There is inherently a disconnect between calling the hasNext() method and the
   * next() method. A message could be removed from the data store by another thread after the
   * hasNext() is called and before the next() is called. Any usage of the iterator will have to
   * account for nulls coming from the next() method.
   *
   * @param timestamp
   */
  public Iterator<Message> getMessagesOlderThan(Date timestamp);
}
