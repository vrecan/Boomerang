package com.vreco.boomerang.conf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Simple configuration abstraction. This will allow us to do basic configuration checking to make sure the application
 * has been setup correctly. And allow us to easily change the implementation later if we choose.
 *
 * @author Ben Aldrich
 */
public class Conf {

  private Properties conf = new Properties();

  public Conf(final String path) throws IOException {
    try (InputStream stream = new FileInputStream(path)) {
      conf.load(stream);
    } catch (IOException e) {
      throw new IOException("Failed to read config file", e);
    }

  }
  
  public Conf(Properties conf) throws IOException {
    this.conf = conf;
  }  

  public void validateFields() {
  }

  /**
   * Get value.
   *
   * @param key
   * @return
   */
  public String getValue(String key) {
    return conf.getProperty(key);
  }

  /**
   * Get value with default if none exists.
   *
   * @param key
   * @param defaultValue
   * @return
   */
  public String getValue(String key, String defaultValue) {
    return conf.getProperty(key, defaultValue);
  }

  public int getIntValue(String key, int defaultValue) {
    try {
      int i = Integer.parseInt(conf.getProperty(key));
      return i;
    } catch (Exception e) {
      return defaultValue;
    }
  }
}
