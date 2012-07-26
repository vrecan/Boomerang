package com.vreco.boomerang;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Ben Aldrich
 */
public final class FileWrite {

  private static Logger logger = Logger.getLogger(FileWrite.class);
  private static FileWrite instance = null;

  protected FileWrite() {
  }

  public synchronized static FileWrite getInstance() {
    if (instance == null) {
      instance = new FileWrite();
    }
    return instance;
  }

  /**
   * Write the set file to disk.
   *
   * @throws IOException
   */
  public synchronized void write(String message, String path) throws IOException {
    //create files by hour
    SimpleDateFormat dfm = new SimpleDateFormat("MM-dd-yyyy-HH");
    String dateString = dfm.format(new Date());
    File file = new File(path + "/" + "temptext" + "_" + dateString + ".json");
    //use buffering & append to the file instead of overwritting it.
    Writer output = new BufferedWriter(new FileWriter(file, true));
    try {
      output.write("temptext");
      output.write("\n");
      logger.info("message written to : " + file.getAbsolutePath());
    } finally {
      output.close();
    }
  }
}
