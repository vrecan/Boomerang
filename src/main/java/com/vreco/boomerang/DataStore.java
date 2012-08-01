package com.vreco.boomerang;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Ben Aldrich
 */
public interface DataStore {
  public void set(String propKey, String value, Date date);
  public void batchSet(Map<String, String> batch);
  public String get(String fieldKey, Date date);
  public String get(String HashKey, String fieldKey);
  public void delete(String fieldKey, Date date);
  public Set<String> getKeys(Date date);
  public boolean exists(String fieldKey, Date date);
  public void close();

}
