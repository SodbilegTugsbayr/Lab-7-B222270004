package edu.sc.csce747.MeetingPlanner;

import org.junit.Test;
import java.util.*;

public class PlannerInterfaceTest {

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

  @Test
  public void testScheduleMeeting_basicFlow() {
    // month, day, start, end, room id, attendees(done), description
    List<String> inputs = Arrays.asList("1", "1", "9", "10", "2A01", "done", "Test meeting");
    TestPI pi = new TestPI(inputs);
    // Should not throw
    pi.scheduleMeeting();
  }

  @Test
  public void testScheduleVacation_basicFlow() {
    // sMonth, sDay, eMonth, eDay, name (choose existing),
    List<String> inputs = Arrays.asList("3", "1", "3", "2", "Greg Gay");
    TestPI pi = new TestPI(inputs);
    pi.scheduleVacation();
  }

  @Test
  public void testCheckRoomAndEmployeeAvailability_andAgendas() {
    // For checkRoomAvailability: month,day,start,end
    TestPI pi1 = new TestPI(Arrays.asList("1", "1", "9", "10"));
    pi1.checkRoomAvailability();

    // For checkEmployeeAvailability: month,day,start,end
    TestPI pi2 = new TestPI(Arrays.asList("1", "1", "9", "10"));
    pi2.checkEmployeeAvailability();

    // For checkAgendaRoom: month, day (or all), room id
    TestPI pi3 = new TestPI(Arrays.asList("1", "all", "2A01"));
    pi3.checkAgendaRoom();

    // For checkAgendaPerson: month, day (or all), person name
    TestPI pi4 = new TestPI(Arrays.asList("1", "all", "Greg Gay"));
    pi4.checkAgendaPerson();
  }

  @Test
  public void testScheduleMeeting_invalidRoom_thenValid() {
    // month, day, start, end, invalid room id, valid room id, done, description
    List<String> inputs = Arrays.asList("2", "2", "10", "11", "NOPE", "2A02", "done", "Team");
    TestPI pi = new TestPI(inputs);
    pi.scheduleMeeting();
  }

  @Test
  public void testScheduleMeeting_attendeeNotFound() {
    // month, day, start, end, room id, attendee(not found), done, description
    List<String> inputs = Arrays.asList("4", "4", "13", "14", "2A03", "Nobody", "done", "Sync");
    TestPI pi = new TestPI(inputs);
    pi.scheduleMeeting();
  }

  @Test
  public void testScheduleVacation_multiMonthBooking() {
    // sMonth, sDay, eMonth, eDay, name
    List<String> inputs = Arrays.asList("1", "30", "3", "2", "Greg Gay");
    TestPI pi = new TestPI(inputs);
    pi.scheduleVacation();
  }
}
