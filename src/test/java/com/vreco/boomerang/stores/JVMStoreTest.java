package com.vreco.boomerang.stores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.vreco.boomerang.message.Message;

public class JVMStoreTest {

  private JVMStore store;

  @Before
  public void setUp() throws Exception {
    store = new JVMStore();
  }

  @Test(expected = NullPointerException.class)
  public void testSetMessageMessageArray_nullFirst() {
    store.set((Message) null);
  }

  @Test
  public void testSetMessageMessageArray_msgGood() {
    Message msg = new Message();
    msg.setProcessName("junit");
    msg.setUUID("00001");
    msg.setMessage("testSetMessageMessageArray_multiMsgGood1");
    msg.setTimestamp(new Date());

    store.set(msg);

    ConcurrentHashMap<String, Message> expectedUuidMap = new ConcurrentHashMap<>();
    expectedUuidMap.put(msg.getUUID(), msg);

    ConcurrentSkipListMap<Long, Map<String, Message>> expectedTimestampMap = new ConcurrentSkipListMap<>();
    Map<String, Message> msgMap = new HashMap<String, Message>();
    msgMap.put(msg.getUUID(), msg);
    expectedTimestampMap.put(msg.getTimestamp().getTime(), msgMap);

    Assert.assertEquals(expectedUuidMap, store.uuidMap);
    Assert.assertEquals(expectedTimestampMap, store.timestampMap);
  }

  @Test
  public void testSetMessageMessageArray_multiMsgGood() {
    Message msg1 = new Message();
    msg1.setProcessName("junit");
    msg1.setUUID("00001");
    msg1.setMessage("testSetMessageMessageArray_multiMsgGood1");
    msg1.setTimestamp(new Date());

    Message msg2 = new Message();
    msg2.setProcessName("junit");
    msg2.setUUID("00002");
    msg2.setMessage("testSetMessageMessageArray_multiMsgGood2");
    msg2.setTimestamp(new Date(msg1.getTimestamp().getTime() + 1));

    store.set(msg1, msg2);

    ConcurrentHashMap<String, Message> expectedUuidMap = new ConcurrentHashMap<>();
    expectedUuidMap.put(msg1.getUUID(), msg1);
    expectedUuidMap.put(msg2.getUUID(), msg2);

    ConcurrentSkipListMap<Long, Map<String, Message>> expectedTimestampMap = new ConcurrentSkipListMap<>();
    Map<String, Message> msg1Map = new HashMap<String, Message>();
    msg1Map.put(msg1.getUUID(), msg1);
    expectedTimestampMap.put(msg1.getTimestamp().getTime(), msg1Map);

    Map<String, Message> msg2Map = new HashMap<String, Message>();
    msg2Map.put(msg2.getUUID(), msg2);
    expectedTimestampMap.put(msg2.getTimestamp().getTime(), msg2Map);

    Assert.assertEquals(expectedUuidMap, store.uuidMap);
    Assert.assertEquals(expectedTimestampMap, store.timestampMap);
  }

  @Test
  public void testSetMessageMessageArray_multiMsgWithNull() {
    Message msg1 = new Message();
    msg1.setProcessName("junit");
    msg1.setUUID("00001");
    msg1.setMessage("testSetMessageMessageArray_multiMsgGood1");
    msg1.setTimestamp(new Date());

    store.set(msg1, null);

    ConcurrentHashMap<String, Message> expectedUuidMap = new ConcurrentHashMap<>();
    expectedUuidMap.put(msg1.getUUID(), msg1);

    ConcurrentSkipListMap<Long, Map<String, Message>> expectedTimestampMap = new ConcurrentSkipListMap<>();
    Map<String, Message> msg1Map = new HashMap<String, Message>();
    msg1Map.put(msg1.getUUID(), msg1);
    expectedTimestampMap.put(msg1.getTimestamp().getTime(), msg1Map);

    Assert.assertEquals(expectedUuidMap, store.uuidMap);
    Assert.assertEquals(expectedTimestampMap, store.timestampMap);
  }

  @Test
  public void testSetMessageMessageArray_multiMsgWithNulls() {
    Message msg1 = new Message();
    msg1.setProcessName("junit");
    msg1.setUUID("00001");
    msg1.setMessage("testSetMessageMessageArray_multiMsgGood1");
    msg1.setTimestamp(new Date());

    store.set(msg1, null, null, null);

    ConcurrentHashMap<String, Message> expectedUuidMap = new ConcurrentHashMap<>();
    expectedUuidMap.put(msg1.getUUID(), msg1);

    ConcurrentSkipListMap<Long, Map<String, Message>> expectedTimestampMap = new ConcurrentSkipListMap<>();
    Map<String, Message> msg1Map = new HashMap<String, Message>();
    msg1Map.put(msg1.getUUID(), msg1);
    expectedTimestampMap.put(msg1.getTimestamp().getTime(), msg1Map);

    Assert.assertEquals(expectedUuidMap, store.uuidMap);
    Assert.assertEquals(expectedTimestampMap, store.timestampMap);
  }

  @Test
  public void testStoreMessage_null() {
    store.storeMessage(null);
    Assert.assertTrue(store.uuidMap.isEmpty());
    Assert.assertTrue(store.timestampMap.isEmpty());
  }

  @Test
  public void testStoreMessage_msgNormal() {
    Message msg = new Message();
    msg.setProcessName("junit");
    msg.setUUID("00001");
    msg.setMessage("testSetMessageMessageArray_msgNormal");
    msg.setTimestamp(new Date());

    store.storeMessage(msg);

    Assert.assertEquals(1, store.uuidMap.size());
    Message actual = store.uuidMap.values().iterator().next();
    Assert.assertEquals(msg, actual);

    Assert.assertEquals(1, store.timestampMap.size());
    Map<String, Message> messages = store.timestampMap.values().iterator().next();
    Assert.assertEquals(1, messages.size());
    Assert.assertEquals(msg, messages.values().iterator().next());
  }

  @Test
  public void testSetCollectionOfMessage_collection() {
    Message msg = new Message();
    msg.setProcessName("junit");
    msg.setUUID("00001");
    msg.setMessage("testSetMessageMessageArray_msgNormal");
    msg.setTimestamp(new Date());

    Collection<Message> msgCol = new ArrayList<>();
    msgCol.add(msg);

    store.set(msgCol);

    Assert.assertEquals(1, store.uuidMap.size());
    Message actual = store.uuidMap.values().iterator().next();
    Assert.assertEquals(msg, actual);

    Assert.assertEquals(1, store.timestampMap.size());
    Map<String, Message> messages = store.timestampMap.values().iterator().next();
    Assert.assertEquals(1, messages.size());
    Assert.assertEquals(msg, messages.values().iterator().next());
  }

  @Test(expected = NullPointerException.class)
  public void testSetCollectionOfMessage_null() {
    store.set((Collection<Message>) null);
  }

  @Test(expected = NullPointerException.class)
  public void testDelete_null() {
    store.delete(null);
  }

  @Test
  public void testDelete_oneMsg() {
    Message msg = new Message();
    msg.setProcessName("junit");
    msg.setUUID("00001");
    msg.setMessage("testSetMessageMessageArray_multiMsgGood1");
    msg.setTimestamp(new Date());

    store.set(msg);

    store.delete(msg.getUUID());

    ConcurrentHashMap<String, Message> expectedUuidMap = new ConcurrentHashMap<>();

    ConcurrentSkipListMap<Long, Map<String, Message>> expectedTimestampMap = new ConcurrentSkipListMap<>();

    Assert.assertEquals(expectedUuidMap, store.uuidMap);
    Assert.assertEquals(expectedTimestampMap, store.timestampMap);
  }

  @Test
  public void testDelete_oneMsgNull() {
    Message msg = new Message();
    msg.setProcessName("junit");
    msg.setUUID("00001");
    msg.setMessage("testSetMessageMessageArray_multiMsgGood1");
    msg.setTimestamp(new Date());

    store.set(msg);

    store.delete(msg.getUUID(), null);

    ConcurrentHashMap<String, Message> expectedUuidMap = new ConcurrentHashMap<>();

    ConcurrentSkipListMap<Long, Map<String, Message>> expectedTimestampMap = new ConcurrentSkipListMap<>();

    Assert.assertEquals(expectedUuidMap, store.uuidMap);
    Assert.assertEquals(expectedTimestampMap, store.timestampMap);
  }

  @Test
  public void testDelete_twoMsgs() {
    Message msg1 = new Message();
    msg1.setProcessName("junit");
    msg1.setUUID("00001");
    msg1.setMessage("testSetMessageMessageArray_multiMsgGood1");
    msg1.setTimestamp(new Date());

    Message msg2 = new Message();
    msg2.setProcessName("junit");
    msg2.setUUID("00002");
    msg2.setMessage("testSetMessageMessageArray_multiMsgGood2");
    msg2.setTimestamp(new Date());

    store.set(msg1, msg2);

    store.delete(msg2.getUUID());

    ConcurrentHashMap<String, Message> expectedUuidMap = new ConcurrentHashMap<>();
    expectedUuidMap.put(msg1.getUUID(), msg1);

    ConcurrentSkipListMap<Long, Map<String, Message>> expectedTimestampMap = new ConcurrentSkipListMap<>();
    Map<String, Message> msg1Map = new HashMap<String, Message>();
    msg1Map.put(msg1.getUUID(), msg1);
    expectedTimestampMap.put(msg1.getTimestamp().getTime(), msg1Map);

    Assert.assertEquals(expectedUuidMap, store.uuidMap);
    Assert.assertEquals(expectedTimestampMap, store.timestampMap);
  }

  @Test
  public void testDeleteMessage() {
    Message msg = new Message();
    msg.setProcessName("junit");
    msg.setUUID("00001");
    msg.setMessage("testSetMessageMessageArray_multiMsgGood1");
    msg.setTimestamp(new Date());

    store.set(msg);

    store.deleteMessage(msg.getUUID());

    ConcurrentHashMap<String, Message> expectedUuidMap = new ConcurrentHashMap<>();

    ConcurrentSkipListMap<Long, Map<String, Message>> expectedTimestampMap = new ConcurrentSkipListMap<>();

    Assert.assertEquals(expectedUuidMap, store.uuidMap);
    Assert.assertEquals(expectedTimestampMap, store.timestampMap);
  }

//  @Test
//  public void testDeleteOlderThan() {
//    Assert.fail("Not yet implemented");
//  }

  @Test
  public void testDeleteTimestamp_null() {
    int actual = store.deleteTimestamp(null);
    Assert.assertEquals(0, actual);
  }

  @Test
  public void testDeleteTimestamp_oneMsg() {
    Message msg = new Message();
    msg.setProcessName("junit");
    msg.setUUID("00001");
    msg.setMessage("testSetMessageMessageArray_multiMsgGood1");
    msg.setTimestamp(new Date());

    store.set(msg);

    store.deleteTimestamp(msg.getTimestamp());

    ConcurrentHashMap<String, Message> expectedUuidMap = new ConcurrentHashMap<>();

    ConcurrentSkipListMap<Long, Map<String, Message>> expectedTimestampMap = new ConcurrentSkipListMap<>();

    Assert.assertEquals(expectedUuidMap, store.uuidMap);
    Assert.assertEquals(expectedTimestampMap, store.timestampMap);
  }

  @Test
  public void testDeleteTimestamp_missingTimestamp() {
    Message msg = new Message();
    msg.setProcessName("junit");
    msg.setUUID("00001");
    msg.setMessage("testSetMessageMessageArray_multiMsgGood1");
    msg.setTimestamp(new Date());

    store.set(msg);

    store.deleteTimestamp(new Date(msg.getTimestamp().getTime() + 1));

    ConcurrentHashMap<String, Message> expectedUuidMap = new ConcurrentHashMap<>();
    expectedUuidMap.put(msg.getUUID(), msg);

    ConcurrentSkipListMap<Long, Map<String, Message>> expectedTimestampMap = new ConcurrentSkipListMap<>();
    Map<String, Message> msg1Map = new HashMap<String, Message>();
    msg1Map.put(msg.getUUID(), msg);
    expectedTimestampMap.put(msg.getTimestamp().getTime(), msg1Map);
  }

//  @Test
//  public void testGetMessage() {
//    Assert.fail("Not yet implemented");
//  }
//
//  @Test
//  public void testGetMessagesOlderThan() {
//    Assert.fail("Not yet implemented");
//  }
}
