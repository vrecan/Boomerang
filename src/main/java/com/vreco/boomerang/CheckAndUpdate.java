package com.vreco.boomerang;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple thread to check for messages that were not delivered by
 * the configurable timeout.
 * 
 * @author Ben Aldrich
 */
public class CheckAndUpdate implements Runnable {

  public CheckAndUpdate() {
  }

  @Override
  public void run() {
    Path dir = Paths.get("data/processing");

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
      for (Path file : stream) {
        System.out.println("File: " + file.getFileName());
      }
    } catch(IOException e) {
      System.out.println(e.getStackTrace().toString());
    }
  }
}
