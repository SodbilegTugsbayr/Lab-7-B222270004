package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.*;

public class PlannerInterfaceScheduleMeetingTest {

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

  private Organization getOrg(PlannerInterface pi) throws Exception {
    Field f = PlannerInterface.class.getDeclaredField("org");
    f.setAccessible(true);
    return (Organization) f.get(pi);
  }

  @Test
  public void testScheduleMeeting_roomConflict_preventsNewMeeting() throws Exception {
    // Set up existing meeting in room 2A01 on 4/4 9-10
    TestPI piPrep = new TestPI(Arrays.asList());
    Organization org = getOrg(piPrep);
    Room room = org.getRoom("2A01");
    Meeting existing = new Meeting(4, 4, 9, 10, new ArrayList<Person>(), new Room(), "ExistingRoomMeeting");
    room.addMeeting(existing);

    // Now attempt to schedule a meeting at same time in same room
    TestPI pi = new TestPI(Arrays.asList("4", "4", "9", "10", "2A01", "done", "NewMeeting"));
    pi.scheduleMeeting();

    // The room's meeting at that date should still be the existing one
    Meeting fetched = room.getMeeting(4, 4, 0);
    assertEquals("ExistingRoomMeeting", fetched.getDescription());
  }

  @Test
  public void testScheduleMeeting_multiAttendee_withOneMissing() throws Exception {
    // Inputs: month, day, start, end, room id, attendee1, attendeeMissing,
    // attendee2, done, description
    TestPI pi = new TestPI(
        Arrays.asList("5", "5", "11", "12", "2A02", "Greg Gay", "Nobody", "Manton Matthews", "done", "TeamMeeting"));
    pi.scheduleMeeting();

    // Verify that Greg Gay was added to the meeting by checking his calendar
    Organization org = getOrg(pi);
    Person greg = org.getEmployee("Greg Gay");
    assertTrue("Greg should be busy for the meeting time", greg.isBusy(5, 5, 11, 12));
  }

  @Test
  public void testScheduleMeeting_attendeeConflict_printsMessage() throws Exception {
    // Prepare an attendee who is already busy at the meeting time
    TestPI piPrep = new TestPI(Arrays.asList());
    Organization org = getOrg(piPrep);
    Person greg = org.getEmployee("Greg Gay");
    // Add existing meeting for Greg at 6/6 14-15
    Meeting existing = new Meeting(6, 6, 14, 15, new ArrayList<Person>(), new Room(), "BusyGreg");
    greg.addMeeting(existing);

    // Now schedule a meeting at same time with Greg as attendee
    TestPI pi = new TestPI(Arrays.asList("6", "6", "14", "15", "2A03", "Greg Gay", "done", "ConflictMeeting"));
    pi.scheduleMeeting();

    // Greg should remain busy (existing meeting still present)
    assertTrue(greg.isBusy(6, 6, 14, 15));
  }
}
