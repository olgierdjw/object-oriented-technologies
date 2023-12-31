package pl.edu.agh.school.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import pl.edu.agh.logger.Logger;
import pl.edu.agh.school.SchoolClass;
import pl.edu.agh.school.Teacher;

public final class SerializablePersistenceManager implements PersistenceManager {

  private final Logger log;

  private String teachersStorageFileName;

  private String classStorageFileName;

  @Inject
  public SerializablePersistenceManager(
      @Named("teachers-file") String teachersFileName,
      @Named("class-file") String classFileName,
      Logger log) {
    teachersStorageFileName = teachersFileName;
    classStorageFileName = classFileName;
    this.log = log;
    log.log("working!");
  }

  public void saveTeachers(List<Teacher> teachers) {
    if (teachers == null) {
      throw new IllegalArgumentException();
    }
    try (ObjectOutputStream oos =
        new ObjectOutputStream(new FileOutputStream(teachersStorageFileName))) {
      oos.writeObject(teachers);
      log.log("teachers saved");
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException(e);
    } catch (IOException e) {
      log.log("There was an error while saving the teachers data", e);
    }
  }

  @SuppressWarnings("unchecked")
  public List<Teacher> loadTeachers() {
    ArrayList<Teacher> res = null;
    try (ObjectInputStream ios =
        new ObjectInputStream(new FileInputStream(teachersStorageFileName))) {

      res = (ArrayList<Teacher>) ios.readObject();
      log.log("teachers loaded");
    } catch (FileNotFoundException e) {
      res = new ArrayList<>();
    } catch (IOException e) {
      log.log("There was an error while loading the teachers data", e);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
    return res;
  }

  public void saveClasses(List<SchoolClass> classes) {
    if (classes == null) {
      throw new IllegalArgumentException();
    }
    try (ObjectOutputStream oos =
        new ObjectOutputStream(new FileOutputStream(classStorageFileName))) {

      oos.writeObject(classes);
      log.log("classes saved");
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException(e);
    } catch (IOException e) {
      log.log("There was an error while saving the classes data", e);
    }
  }

  @SuppressWarnings("unchecked")
  public List<SchoolClass> loadClasses() {
    ArrayList<SchoolClass> res = null;
    try (ObjectInputStream ios = new ObjectInputStream(new FileInputStream(classStorageFileName))) {
      res = (ArrayList<SchoolClass>) ios.readObject();
      log.log("classes loaded");
    } catch (FileNotFoundException e) {
      res = new ArrayList<>();
    } catch (IOException e) {
      log.log("There was an error while loading the classes data", e);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }
    return res;
  }
}
