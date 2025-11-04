package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;

public class CalendarExtraTest {
  @Test
  public void testClearSchedule_and_getRemoveMeeting() throws TimeConflictException {
    Calendar cal = new Calendar();
    Meeting m = new Meeting(7, 7, 9, 10);
    cal.addMeeting(m);
    assertTrue(cal.isBusy(7, 7, 9, 10));
    cal.clearSchedule(7, 7);
    assertFalse(cal.isBusy(7, 7, 9, 10));

    // add back and test get/remove
    cal.addMeeting(m);
    Meeting fetched = cal.getMeeting(7, 7, 0);
    assertEquals(m, fetched);
    cal.removeMeeting(7, 7, 0);
    assertFalse(cal.isBusy(7, 7, 9, 10));
  }

  @Test
  public void testCheckTimes_invalidStartHour_shouldThrow() {
    try {
      Calendar.checkTimes(3, 10, -1, 10);
      fail("Invalid start hour should throw");
    } catch (TimeConflictException e) {
      // expected
    }
  }

  @Test
  public void testCheckTimes_invalidEndHour_shouldThrow() {
    try {
      Calendar.checkTimes(3, 10, 9, 24);
      fail("Invalid end hour should throw");
    } catch (TimeConflictException e) {
      // expected
    }
  }

  @Test
  public void testAddMeeting_onMarkedNonexistentDay_allowsMeeting() throws TimeConflictException {
    Calendar cal = new Calendar();
    // constructor marks 2/29 as "Day does not exist" with a placeholder meeting
    Meeting m = new Meeting(2, 29, 9, 10);
    cal.addMeeting(m); // should be allowed because placeholder is skipped
    assertTrue(cal.isBusy(2, 29, 9, 10));
  }
}
