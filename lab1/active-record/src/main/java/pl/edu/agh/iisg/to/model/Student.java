package pl.edu.agh.iisg.to.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import pl.edu.agh.iisg.to.executor.QueryExecutor;

public class Student {
  private final int id;

  private final String firstName;

  private final String lastName;

  private final int indexNumber;

  Student(final int id, final String firstName, final String lastName, final int indexNumber) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.indexNumber = indexNumber;
  }

  public static Optional<Student> create(
      final String firstName, final String lastName, final int indexNumber) {

    String sql = "INSERT INTO student (first_name, last_name, index_number) VALUES (?, ?, ?)";

    Object[] args = {firstName, lastName, indexNumber};

    try {
      int insertedStudentId = QueryExecutor.createAndObtainId(sql, args);
      return Student.findById(insertedStudentId);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return Optional.empty();
  }

  public static Optional<Student> findByIndexNumber(final int indexNumber) {
    String query = "SELECT * FROM student WHERE " + Columns.INDEX_NUMBER + " = ?";
    try (ResultSet rs = QueryExecutor.read(query, indexNumber)) {
      if (rs.next()) {
        return Optional.of(
            new Student(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getInt("index_number")));
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  public static Optional<Student> findById(final int id) {
    String sql = "SELECT * FROM student WHERE id = (?)";
    return find(id, sql);
  }

  private static Optional<Student> find(int value, String sql) {
    Object[] args = {value};
    try (ResultSet rs = QueryExecutor.read(sql, args)) {
      if (rs.next()) {
        return Optional.of(
            new Student(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getInt("index_number")));
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  public Map<Course, Float> createReport() {
    String studentGrades =
        "SELECT * FROM grade g INNER JOIN course c ON g.course_id = c.id WHERE g.student_id = ?";
    Object[] args = {id};

    // temporary data
    Map<Course, LinkedList<Float>> allGrades = new HashMap<>();

    // get all grades
    try (ResultSet rs = QueryExecutor.read(studentGrades, args)) {
      while (rs.next()) {
        int course_id = rs.getInt("course_id");
        Course c = Course.findById(course_id).orElseThrow();

        float grade = rs.getFloat("grade");

        boolean newCourse = !allGrades.containsKey(c);
        if (newCourse) allGrades.put(c, new LinkedList<>());
        allGrades.get(c).add(grade);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // calculate report
    Map<Course, Float> report = new HashMap<>();
    allGrades.forEach(
        (course, floats) -> {
          Float avg = floats.stream().reduce((float) 0, Float::sum) / floats.size();
          report.put(course, avg);
        });
    return report;
  }

  public int id() {
    return id;
  }

  public String firstName() {
    return firstName;
  }

  public String lastName() {
    return lastName;
  }

  public int indexNumber() {
    return indexNumber;
  }

  public static class Columns {

    public static final String ID = "id";

    public static final String FIRST_NAME = "first_name";

    public static final String LAST_NAME = "last_name";

    public static final String INDEX_NUMBER = "index_number";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Student student = (Student) o;

    if (id != student.id) return false;
    if (indexNumber != student.indexNumber) return false;
    if (!firstName.equals(student.firstName)) return false;
    return lastName.equals(student.lastName);
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + firstName.hashCode();
    result = 31 * result + lastName.hashCode();
    result = 31 * result + indexNumber;
    return result;
  }
}
