package com.vreco.boomerang;

import com.vreco.boomerang.message.Message;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author Ben Aldrich
 */
public final class FileWrite {

  private static Logger logger = Logger.getLogger(FileWrite.class);
  private static FileWrite instance = null;
  private ObjectMapper mapper = new ObjectMapper();  

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
  public synchronized void write(Message msg, String path) throws IOException {
    
    String jsonMsg = mapper.writeValueAsString(msg);
    BufferedWriter writer =
      Files.newBufferedWriter(
      FileSystems.getDefault().getPath(".", path + "/" + msg.getUUID()),
      Charset.forName("UTF-8"),
      StandardOpenOption.CREATE);
    writer.write(jsonMsg, 0, jsonMsg.length());
  }
}
