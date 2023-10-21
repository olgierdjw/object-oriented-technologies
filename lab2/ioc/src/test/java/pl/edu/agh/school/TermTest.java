package pl.edu.agh.school;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TermTest {

  private static final int DURATION = 45;
  Term term;
  DayOfWeek day = DayOfWeek.SATURDAY;
  Date date;
  @Mock Subject subject;

  @BeforeEach
  public void setUp() throws Exception {
    date = new Date();
    term = new Term(day, date, DURATION);
  }

  @Test
  public void testTermData() {
    assertEquals(day, term.getDayOfWeek());
    assertEquals(DURATION, term.getDuration());
    assertEquals(date.getTime(), term.getStartTime().getTime());
  }

  @Test
  public void testGetSubject() {
    assertNull(term.getSubject());
    term.setSubject(subject);
    assertEquals(subject, term.getSubject());
  }
}
