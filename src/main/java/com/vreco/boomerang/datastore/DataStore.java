package com.vreco.boomerang.datastore;

import com.vreco.boomerang.message.Message;
import com.vreco.boomerang.message.ResponseMessage;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Ben Aldrich
 */
public interface DataStore extends AutoCloseable {

  public void set(Message msg) throws IOException;

  public void batchSet(Collection<Message> msgs);

  public String get(ResponseMessage msg) throws ParseException;

  public void delete(ResponseMessage msg) throws ParseException;

  public Set<String> getKeys(Date date);

  public boolean exists(Message msg);

  public boolean exists(ResponseMessage msg) throws ParseException;

  @Override
  public void close();
}
