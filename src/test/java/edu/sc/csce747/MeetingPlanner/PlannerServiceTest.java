package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;

public class PlannerServiceTest {
  @Test
  public void testPlannerServiceConstructor() {
    PlannerService service = new PlannerService();
    assertNotNull(service);
  }

  @Test
  public void testScheduleVacationSameDay() throws Exception {
    Person p = new Person("Liam");
    List<String> conflicts = PlannerService.scheduleVacation(p, 5, 10, 5, 10);
    assertNotNull(conflicts);
    assertTrue(conflicts.isEmpty());
    assertNotNull(p.getMeeting(5, 10, 0));
    assertEquals("vacation", p.getMeeting(5, 10, 0).getDescription());
  }

  @Test
  public void testApplyMeetingSuccess() throws Exception {
    Room r = new Room("R1");
    Person p = new Person("Alice");
    ArrayList<Person> attendees = new ArrayList<Person>();
    attendees.add(p);

    Meeting m = new Meeting(5, 10, 9, 10, attendees, r, "Standup");

    String err = PlannerService.applyMeeting(r, attendees, m);
    assertNull("Expected no error when applying a non-conflicting meeting", err);

    // Verify room has the meeting
    Meeting stored = r.getMeeting(5, 10, 0);
    assertNotNull(stored);
    assertEquals("Standup", stored.getDescription());
  }

  @Test
  public void testApplyMeetingRoomConflict() throws Exception {
    Room r = new Room("R2");
    // Seed an existing meeting in the room
    Meeting existing = new Meeting(6, 1, 10, 12, new ArrayList<Person>(), r, "Existing");
    r.addMeeting(existing);

    Person p = new Person("Bob");
    ArrayList<Person> attendees = new ArrayList<Person>();
    attendees.add(p);
    Meeting m = new Meeting(6, 1, 11, 13, attendees, r, "Overlap");

    String err = PlannerService.applyMeeting(r, attendees, m);
    assertNotNull("Expected an error message for room conflict", err);
    assertTrue(err.contains("Conflict for room") || err.toLowerCase().contains("conflict"));
  }

  @Test
  public void testApplyMeetingAttendeeConflict() throws Exception {
    Room r = new Room("R3");
    Person p = new Person("Carol");

    // Seed an existing meeting on the person's calendar
    Meeting existing = new Meeting(7, 2, 14, 15, new ArrayList<Person>(), new Room("X"), "Busy");
    p.addMeeting(existing);

    ArrayList<Person> attendees = new ArrayList<Person>();
    attendees.add(p);
    Meeting m = new Meeting(7, 2, 14, 16, attendees, r, "OverlapAttendee");

    String err = PlannerService.applyMeeting(r, attendees, m);
    assertNotNull("Expected an error message for attendee conflict", err);
    assertTrue(err.contains("Conflict for attendee") || err.toLowerCase().contains("conflict"));
  }

  @Test
  public void testScheduleVacationNoConflict() throws Exception {
    Person p = new Person("Dan");
    List<String> conflicts = PlannerService.scheduleVacation(p, 8, 1, 8, 3);
    assertNotNull(conflicts);
    assertTrue(conflicts.isEmpty());
    // Verify days were booked
    Meeting m0 = p.getMeeting(8, 1, 0);
    assertNotNull(m0);
    assertEquals("vacation", m0.getDescription());
  }

  @Test
  public void testScheduleVacationConflict() throws Exception {
    Person p = new Person("Eve");
    // Seed an existing meeting into the conflict range
    Meeting existing = new Meeting(9, 5, 10, 11, new ArrayList<Person>(), new Room("RZ"), "Existing");
    p.addMeeting(existing);

    List<String> conflicts = PlannerService.scheduleVacation(p, 9, 4, 9, 6);
    assertNotNull(conflicts);
    assertFalse(conflicts.isEmpty());
    boolean found = false;
    for (String c : conflicts) {
      if (c.contains("There is a conflict for date 9/5")) {
        found = true;
        break;
      }
    }
    assertTrue("Expected a conflict message for 9/5", found);
  }

  @Test
  public void testApplyMeetingEmptyAttendees() throws Exception {
    Room r = new Room("Empty");
    List<Person> attendees = new ArrayList<>();
    Meeting m = new Meeting(2, 2, 8, 9, new ArrayList<Person>(), r, "NoOne");

    String err = PlannerService.applyMeeting(r, attendees, m);
    assertNull(err);
    Meeting stored = r.getMeeting(2, 2, 0);
    assertNotNull(stored);
    assertEquals("NoOne", stored.getDescription());
  }

  @Test
  public void testScheduleVacationMultiMonthNoConflicts() throws Exception {
    Person p = new Person("Frank");
    List<String> conflicts = PlannerService.scheduleVacation(p, 1, 30, 3, 2);
    assertNotNull(conflicts);
    assertTrue(conflicts.isEmpty());
    // Verify a day in start month and end month were booked
    assertNotNull(p.getMeeting(1, 30, 0));
    assertNotNull(p.getMeeting(3, 2, 0));
  }

  @Test(expected = TimeConflictException.class)
  public void testScheduleVacationInvalidMonthThrows() throws Exception {
    Person p = new Person("Greg");
    // Invalid month should cause TimeConflictException from Calendar
    PlannerService.scheduleVacation(p, 13, 1, 13, 2);
  }

  @Test
  public void testScheduleVacationBookingPhaseException() throws Exception {
    // Create a Person subclass that pretends no conflicts in the check phase,
    // but throws a TimeConflictException during addMeeting for a specific date
    class FlakyPerson extends Person {
      public FlakyPerson(String name) {
        super(name);
      }

      @Override
      public boolean isBusy(int month, int day, int start, int end) throws TimeConflictException {
        // Always report not busy so the check phase passes
        return false;
      }

      @Override
      public void addMeeting(Meeting meeting) throws TimeConflictException {
        // Throw when attempting to book month=4 day=2 to simulate a late conflict
        if (meeting.getMonth() == 4 && meeting.getDay() == 2) {
          throw new TimeConflictException("Late conflict for " + meeting.getMonth() + "/" + meeting.getDay());
        }
        super.addMeeting(meeting);
      }
    }

    FlakyPerson p = new FlakyPerson("Hank");
    // Request a vacation spanning from 4/1 to 4/3; booking should fail when booking
    // 4/2
    List<String> conflicts = PlannerService.scheduleVacation(p, 4, 1, 4, 3);
    assertNotNull(conflicts);
    assertFalse(conflicts.isEmpty());
    boolean found = false;
    for (String c : conflicts) {
      if (c.contains("Late conflict for 4/2") || c.contains("4/2")) {
        found = true;
        break;
      }
    }
    assertTrue("Expected booking-phase conflict for 4/2", found);
  }

  @Test
  public void testScheduleVacationInterveningMonthConflict() throws Exception {
    Person p = new Person("Ivy");
    // Seed an existing meeting in an intervening month (month 2)
    Meeting existing = new Meeting(2, 10, 9, 10, new ArrayList<Person>(), new Room("RM"), "Busy2");
    p.addMeeting(existing);

    List<String> conflicts = PlannerService.scheduleVacation(p, 1, 30, 3, 5);
    assertNotNull(conflicts);
    assertFalse(conflicts.isEmpty());
    boolean found = false;
    for (String c : conflicts) {
      if (c.contains("There is a conflict for date 2/10")) {
        found = true;
        break;
      }
    }
    assertTrue("Expected an intervening-month conflict for 2/10", found);
  }

  @Test
  public void testScheduleVacationBookingCreatesInterveningDays() throws Exception {
    Person p = new Person("Jack");
    // No existing meetings; book from month 1 day 30 to month 3 day 2
    List<String> conflicts = PlannerService.scheduleVacation(p, 1, 30, 3, 2);
    assertNotNull(conflicts);
    assertTrue(conflicts.isEmpty());
    // Verify an intervening month day (month 2, day 1) was booked
    Meeting m = p.getMeeting(2, 1, 0);
    assertNotNull("Expected a vacation meeting booked for intervening month day", m);
    assertEquals("vacation", m.getDescription());
  }
}
