package pl.edu.agh.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Logger {

  protected DateFormat dateFormat;

  @Inject protected Set<IMessageSerializer> registeredSerializers;

  public static Logger getInstance() {
    if (logger == null) throw new IllegalStateException("");
    return logger;
  }

  private static Logger logger = null;

  @Inject
  public Logger(Set<IMessageSerializer> registeredSerializers) {
    init();
    if (registeredSerializers == null) {
      throw new IllegalArgumentException("null argument");
    }
    this.registeredSerializers = registeredSerializers;
    Logger.logger = this;
  }

  public void registerSerializer(IMessageSerializer messageSerializer) {
    registeredSerializers.add(messageSerializer);
  }

  public void log(String message) {
    log(message, null);
  }

  public void log(String message, Throwable error) {
    for (IMessageSerializer messageSerializer : registeredSerializers) {
      String formattedMessage =
          dateFormat.format(new Date()) + ": " + message + (error != null ? error.toString() : "");
      messageSerializer.serializeMessage(formattedMessage);
    }
  }

  private void init() {
    dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
  }
}
