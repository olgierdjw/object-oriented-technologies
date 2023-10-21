package pl.edu.agh.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Named;

public class FileMessageSerializer implements IMessageSerializer {
  private final String filename;

  @Inject
  public FileMessageSerializer(@Named("logFilename") String filename) {
    this.filename = filename;
  }

  @Override
  public void serializeMessage(String message) {
    try (var output = new BufferedWriter(new FileWriter(filename, true))) {
      output.write(message + "\n");
      output.flush();
    } catch (IOException e) {
      System.err.println("FileMessageSerializer error: " + e.getMessage());
    }
  }
}
