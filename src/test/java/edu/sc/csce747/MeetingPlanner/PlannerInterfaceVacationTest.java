package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.*;

public class PlannerInterfaceVacationTest {

  private static class TestPI extends PlannerInterface {
    private Queue<String> inputs;

    public TestPI(List<String> inputs) {
      super();
      this.inputs = new LinkedList<String>(inputs);
    }

    @Override
    protected String inputOutput(String message) {
      String s = inputs.poll();
      return s == null ? "" : s;
    }

    @Override
    public void mainMenu() {

    }
  }

  // Helper: get the Organization instance from a PlannerInterface via reflection
  private Organization getOrg(PlannerInterface pi) throws Exception {
    Field f = PlannerInterface.class.getDeclaredField("org");
    f.setAccessible(true);
    return (Organization) f.get(pi);
  }

  @Test
  public void testScheduleVacation_conflictStopsBooking() throws Exception {
    // Prepare inputs: sMonth, sDay, eMonth, eDay, name
    TestPI pi = new TestPI(Arrays.asList("1", "1", "1", "3", "Greg Gay"));
    Organization org = getOrg(pi);
    Person who = org.getEmployee("Greg Gay");

    // Add an existing meeting on 1/2 that will create a conflict
    Meeting existing = new Meeting(1, 2, 10, 11, new ArrayList<Person>(), new Room(), "Existing");
    who.addMeeting(existing);

    // Now run scheduleVacation; it should detect conflict and NOT book vacation
    // days
    pi.scheduleVacation();

    // Day 1 should not be booked because conflict occurs and booking is skipped
    assertFalse("Day 1 should not be booked when conflict exists", who.isBusy(1, 1, 0, 23));
  }

  @Test
  public void testScheduleVacation_noConflictBooksDays() throws Exception {
    TestPI pi = new TestPI(Arrays.asList("2", "1", "2", "3", "Manton Matthews"));
    Organization org = getOrg(pi);
    Person who = org.getEmployee("Manton Matthews");

    // Ensure no preexisting meetings
    assertFalse(who.isBusy(2, 1, 0, 23));

    // Run scheduleVacation, should book days 1..3
    pi.scheduleVacation();

    assertTrue("Day 1 should be booked as vacation", who.isBusy(2, 1, 0, 23));
    assertTrue("Day 2 should be booked as vacation", who.isBusy(2, 2, 0, 23));
    assertTrue("Day 3 should be booked as vacation", who.isBusy(2, 3, 0, 23));

    // Verify description is "vacation" for one of the days
    Meeting booked = who.getMeeting(2, 1, 0);
    assertEquals("vacation", booked.getDescription());
  }

  @Test
  public void testScheduleVacation_placeholderDay_skipsConflict() throws Exception {
    // sMonth and eMonth both 2, day 29 is marked as 'Day does not exist'
    // placeholder
    TestPI pi = new TestPI(Arrays.asList("2", "29", "2", "29", "Greg Gay"));
    Organization org = getOrg(pi);
    Person who = org.getEmployee("Greg Gay");

    // The placeholder exists and isBusy should be true for 2/29; scheduleVacation
    // should skip marking conflict
    pi.scheduleVacation();

    // Because placeholder is skipped, no conflict should be reported and booking
    // should proceed => day 29 should be booked
    assertTrue(who.isBusy(2, 29, 0, 23));
  }
}
