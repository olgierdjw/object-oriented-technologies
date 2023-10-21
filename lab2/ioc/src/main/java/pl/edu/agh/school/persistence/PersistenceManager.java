package pl.edu.agh.school.persistence;

import java.util.List;
import pl.edu.agh.school.SchoolClass;
import pl.edu.agh.school.Teacher;

public interface PersistenceManager {
  public void saveTeachers(List<Teacher> teachers);

  public List<Teacher> loadTeachers();

  public void saveClasses(List<SchoolClass> classes);

  public List<SchoolClass> loadClasses();
}
