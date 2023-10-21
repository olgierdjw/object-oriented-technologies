package pl.edu.agh.school;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LessonTest {

  Lesson lesson;
  @Mock Subject subject;
  @Mock Student student;

  @BeforeEach
  public void setUp() throws Exception {
    lesson = new Lesson(subject);
  }

  @Test
  public void testGetSubject() {
    assertEquals(subject, lesson.getSubject());
  }

  @Test
  public void testGetDate() {
    assertNotNull(lesson.getDate());
    // check if date is not in the future
    assertFalse(lesson.getDate().before(Calendar.getInstance()));
  }

  @Test
  public void testRegisterPresence() {
    lesson.registerPresence(student);
    assertTrue(lesson.isPresent(student));
  }
}
