package com.vreco.boomerang.datastore;

import com.vreco.boomerang.message.BoomerangMessage;
import com.vreco.boomerang.message.Message;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collection;

/**
 *
 * @author Ben Aldrich
 */
public interface DataStore extends AutoCloseable {

  public void set(Message msg) throws IOException;

  public void setFailed(Message msg) throws IOException;

  public void batchSet(Collection<Message> msgs);

  public String get(Message msg) throws ParseException;

  public String getFailed(Message msg) throws ParseException;

  public Collection<BoomerangMessage> getLastNMessages(final int n) throws IOException, ParseException;

  public void delete(Message msg) throws ParseException;

  public void deleteFailed(Message msg) throws ParseException;

  public boolean exists(Message msg) throws ParseException;

  public boolean existsFailed(Message msg) throws ParseException;

  @Override
  public void close();
}
