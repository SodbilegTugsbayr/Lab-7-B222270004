package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;

public class MeetingExtraTest {

  @Test
  public void testSettersAndGetters() {
    Meeting m = new Meeting();
    m.setMonth(4);
    m.setDay(12);
    m.setStartTime(8);
    m.setEndTime(9);
    m.setDescription("Breakfast");
    Room r = new Room("R100");
    m.setRoom(r);

    assertEquals(4, m.getMonth());
    assertEquals(12, m.getDay());
    assertEquals(8, m.getStartTime());
    assertEquals(9, m.getEndTime());
    assertEquals("Breakfast", m.getDescription());
    assertSame(r, m.getRoom());
    assertNotNull(m.getAttendees());
  }

  @Test
  public void testAddRemoveAttendee_edge() {
    Meeting m = new Meeting(1, 1, 10, 11);
    Person p = new Person("Eve");
    // Initially no attendees
    assertEquals(0, m.getAttendees().size());
    m.addAttendee(p);
    assertEquals(1, m.getAttendees().size());
    m.removeAttendee(p);
    assertEquals(0, m.getAttendees().size());
  }

  @Test
  public void testToString_noAttendees() {
    Meeting m = new Meeting(2, 2, 14, 15);
    m.setDescription("Solo");
    Room r = new Room("R1");
    m.setRoom(r);
    String s = m.toString();
    assertTrue(s.contains("2/2, 14 - 15"));
    assertTrue(s.contains("R1"));
    assertTrue(s.contains("Solo"));
    assertTrue(s.contains("Attending"));
  }

  @Test
  public void testToString_nullDescription() {
    Meeting m = new Meeting(3, 3, 10, 11);
    m.setRoom(new Room("R2"));
    m.setDescription(null);
    String s = m.toString();
    assertTrue(s.contains("3/3, 10 - 11"));
    assertTrue(s.contains("R2"));
    assertTrue(s.contains("Attending"));
  }
}
