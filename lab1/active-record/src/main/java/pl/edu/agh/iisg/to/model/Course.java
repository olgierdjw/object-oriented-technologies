package pl.edu.agh.iisg.to.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import pl.edu.agh.iisg.to.executor.QueryExecutor;

public class Course {

  public static final String TABLE_NAME = "course";

  private static final Logger logger = Logger.getGlobal();

  private final int id;

  private final String name;

  private List<Student> enrolledStudents;

  private boolean isStudentsListDownloaded = false;

  Course(final int id, final String name) {
    this.id = id;
    this.name = name;
  }

  public static Optional<Course> create(final String name) {
    String insertSql = "INSERT INTO course (name) VALUES (?);";
    Object[] args = {name};

    try {
      int id = QueryExecutor.createAndObtainId(insertSql, args);
      return Course.findById(id);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  public static Optional<Course> findById(final int id) {
    String findByIdSql = "SELECT * FROM course WHERE id = ?";
    Object[] args = {id};

    try (ResultSet rs = QueryExecutor.read(findByIdSql, args)) {
      if (rs.next()) {
        return Optional.of(new Course(rs.getInt("id"), rs.getString("name")));
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  public boolean enrollStudent(final Student student) {
    String enrollStudentSql = "INSERT INTO student_course (student_id, course_id) VALUES (?, ?)";
    Object[] args = {student.id(), id};

    try {
      QueryExecutor.createAndObtainId(enrollStudentSql, args);
      return true;

    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  // to many db queries (Student.findById)
  //    public List<Student> studentList() {
  //        String findStudentListSql = "SELECT student_id FROM student_course WHERE course_id = ?";
  //        Object[] args = {
  //                id
  //        };
  //
  //        List<Student> resultList = new LinkedList<>();
  //
  //        try {
  //            ResultSet rs = QueryExecutor.read(findStudentListSql, args);
  //            while (rs.next()) {
  //                int studentId = rs.getInt("student_id");
  //                Optional<Student> student = Student.findById(studentId);
  //                student.ifPresent(resultList::add);
  //            }
  //        } catch (SQLException e) {
  //            e.printStackTrace();
  //            return resultList;
  //        }
  //
  //        return resultList;
  //    }

  public List<Student> studentList() {
    String findStudentListSql =
        "SELECT * FROM student_course sc INNER JOIN student s ON sc.student_id = s.id WHERE"
            + " sc.course_id = ?";
    Object[] args = {id};

    List<Student> resultList = new LinkedList<>();

    try {
      ResultSet rs = QueryExecutor.read(findStudentListSql, args);
      while (rs.next()) {
        Student newStudent =
            new Student(
                rs.getInt("student_id"),
                rs.getString(Student.Columns.FIRST_NAME),
                rs.getString(Student.Columns.LAST_NAME),
                rs.getInt(Student.Columns.INDEX_NUMBER));
        System.out.println(newStudent.hashCode());
        resultList.add(newStudent);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return resultList;
    }

    return resultList;
  }

  public List<Student> cachedStudentsList() {
    boolean firstTime = !isStudentsListDownloaded;

    if (firstTime) {
      isStudentsListDownloaded = true;
      enrolledStudents = studentList();
    }
    return enrolledStudents;
  }

  public int id() {
    return id;
  }

  public String name() {
    return name;
  }

  public static class Columns {

    public static final String ID = "id";

    public static final String NAME = "name";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Course course = (Course) o;

    if (id != course.id) return false;
    return name.equals(course.name);
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + name.hashCode();
    return result;
  }
}
