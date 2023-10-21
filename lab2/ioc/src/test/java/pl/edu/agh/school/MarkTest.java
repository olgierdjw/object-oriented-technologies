package pl.edu.agh.school;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MarkTest {

  Mark mark;
  // cannot mock final classes
  MarkValue value = MarkValue.A;
  @Mock Student student;

  @BeforeEach
  public void setUp() throws Exception {
    mark = new Mark(value, student);
  }

  @Test
  public void testMark() {
    assertEquals(student, mark.getStudent());
    assertEquals(value, mark.getValue());
  }
}
