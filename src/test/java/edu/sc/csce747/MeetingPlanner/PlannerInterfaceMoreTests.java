package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.*;

public class PlannerInterfaceMoreTests {

  private class MockPlanner extends PlannerInterface {
    @Override
    protected String inputOutput(String message) {
      return "dummy";
    }
  }

  @Test
  public void testAllMethods() {
    MockPlanner planner = new MockPlanner();
    planner.mainMenu();
    planner.scheduleMeeting();
    planner.scheduleVacation();
    planner.checkAgendaPerson();
    planner.checkAgendaRoom();
    planner.checkEmployeeAvailability();
    planner.checkRoomAvailability();
  }

  private static class TestPI extends PlannerInterface {
    private Queue<String> inputs;

    public TestPI(List<String> inputs) {
      super();
      this.inputs = new LinkedList<>(inputs);
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

  private Organization getOrg(PlannerInterface pi) throws Exception {
    Field f = PlannerInterface.class.getDeclaredField("org");
    f.setAccessible(true);
    return (Organization) f.get(pi);
  }

  @Test
  public void testScheduleMeeting_invalidTime_triggersCatch() {
    // month, day, start invalid, end
    TestPI pi = new TestPI(Arrays.asList("1", "1", "-5", "10", "2A01", "done", "desc"));
    // should not throw, will hit TimeConflictException inside isBusy and be handled
    pi.scheduleMeeting();
  }

  @Test
  public void testCheckEmployeeAvailability_invalidTime_triggersCatch() {
    TestPI pi = new TestPI(Arrays.asList("1", "1", "-1", "0"));
    pi.checkEmployeeAvailability();
  }

  @Test
  public void testScheduleVacation_interveningMonth_conflictDetected() throws Exception {
    // Create a person meeting in month 3 which lies between sMonth=2 and eMonth=4
    TestPI piPrep = new TestPI(Arrays.asList());
    Organization org = getOrg(piPrep);
    Person who = org.getEmployee("John Rose");
    // Add a meeting on 3/5
    Meeting m = new Meeting(3, 5, 10, 11, new ArrayList<Person>(), new Room(), "BusyInIntervening");
    who.addMeeting(m);

    // Now run scheduleVacation from 2/28 to 4/2 for John Rose
    TestPI pi = new TestPI(Arrays.asList("2", "28", "4", "2", "John Rose"));
    pi.scheduleVacation();

    // Because of conflict in intervening month, no booking for 2/28 should have
    // occurred
    assertFalse(who.isBusy(2, 28, 0, 23));
  }

  @Test
  public void testScheduleVacation_interveningMonth_noConflict_books() throws Exception {
    TestPI pi = new TestPI(Arrays.asList("7", "1", "9", "2", "Ryan Austin"));
    Organization org = getOrg(pi);
    Person who = org.getEmployee("Ryan Austin");
    // ensure no meetings exist in intervening months
    pi.scheduleVacation();
    // verify a few days were booked
    assertTrue(who.isBusy(7, 1, 0, 23));
    assertTrue(who.isBusy(9, 2, 0, 23));
  }
}
