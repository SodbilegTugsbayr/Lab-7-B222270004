package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;

public class TimeConflictExceptionTest {
  @Test
  public void testConstructors() {
    TimeConflictException ex1 = new TimeConflictException();
    assertNotNull("Default constructor should work", ex1);

    TimeConflictException ex2 = new TimeConflictException("Test message");
    assertEquals("Message constructor should set message", "Test message", ex2.getMessage());

    TimeConflictException ex3 = new TimeConflictException(new RuntimeException("Cause"));
    assertNotNull("Cause constructor should work", ex3.getCause());

    TimeConflictException ex4 = new TimeConflictException("Test message", new RuntimeException("Cause"));
    assertEquals("Message constructor should set message", "Test message", ex4.getMessage());
    assertNotNull("Message and cause constructor should set cause", ex4.getCause());
  }

  @Test
  public void testFullConstructor() {
    TimeConflictException ex = new TimeConflictException("m", new RuntimeException("c"), true, false);
    assertEquals("m", ex.getMessage());
    assertNotNull(ex.getCause());
  }
}