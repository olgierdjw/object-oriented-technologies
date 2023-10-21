package pl.edu.agh.school.guice;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoSet;
import com.google.inject.name.Names;
import pl.edu.agh.logger.ConsoleMessageSerializer;
import pl.edu.agh.logger.FileMessageSerializer;
import pl.edu.agh.logger.IMessageSerializer;
import pl.edu.agh.logger.Logger;
import pl.edu.agh.school.persistence.PersistenceManager;
import pl.edu.agh.school.persistence.SerializablePersistenceManager;

public class SchoolModule extends com.google.inject.AbstractModule {
  @Provides
  PersistenceManager providePersistenceManager(
      SerializablePersistenceManager serializablePersistenceManager) {
    return serializablePersistenceManager;
  }

  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named("teachers-file")).toInstance("guice-teachers.dat");
    bind(String.class).annotatedWith(Names.named("class-file")).toInstance("guice-classes.dat");
    bind(String.class).annotatedWith(Names.named("logFilename")).toInstance("persistence.log");
    bind(Logger.class).in(Singleton.class);
  }

  @ProvidesIntoSet
  IMessageSerializer provideConsoleSerializer(ConsoleMessageSerializer consoleMessageSerializer) {
    return consoleMessageSerializer;
  }

  @ProvidesIntoSet
  IMessageSerializer provideFileSerializer(FileMessageSerializer fileMessageSerializer) {
    return fileMessageSerializer;
  }
}
