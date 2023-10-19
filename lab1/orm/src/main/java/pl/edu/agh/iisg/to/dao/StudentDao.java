package pl.edu.agh.iisg.to.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Grade;
import pl.edu.agh.iisg.to.model.Student;

public class StudentDao extends GenericDao<Student> {

  public Optional<Student> create(
      final String firstName, final String lastName, final int indexNumber) {
    Student student = new Student(firstName, lastName, indexNumber);
    try {
      this.save(student);
      return Optional.of(student);
    } catch (PersistenceException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public Optional<Student> findByIndexNumber(final int indexNumber) {
    Session session = this.currentSession();
    Student student =
        session
            .createQuery("SELECT s FROM Student s WHERE s.indexNumber = :index", Student.class)
            .setParameter("index", indexNumber)
            .getSingleResult();
    return Optional.of(student);
  }

  public Map<Course, Float> createReport(final Student student) {
    Map<Course, Float> report = new HashMap<>();
    student.gradeSet().stream()
        .collect(Collectors.groupingBy(Grade::course))
        .forEach(
            (course, grades) -> {
              float total = grades.stream().map(Grade::grade).reduce(0F, Float::sum);
              report.put(course, total / grades.size());
            });
    return report;
  }
}
