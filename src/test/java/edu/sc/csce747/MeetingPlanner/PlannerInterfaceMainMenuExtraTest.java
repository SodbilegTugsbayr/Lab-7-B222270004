package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

public class PlannerInterfaceMainMenuExtraTest {

  private static class MenuTester extends PlannerInterface {
    public boolean saw1 = false, saw2 = false, saw3 = false, saw4 = false, saw5 = false, saw6 = false;
    private Queue<String> inputs;
    public boolean exitCalled = false;
    private int callCount = 0;
    private final int maxCalls;

    public MenuTester(List<String> inputs) {
      super();
      this.inputs = new LinkedList<>(inputs);
      this.maxCalls = inputs.size() + 1; // Prevent infinite loops
    }

    @Override
    protected String inputOutput(String message) {
      String s = inputs.poll();
      return s == null ? "" : s;
    }

    @Override
    public void scheduleMeeting() {
      saw1 = true;
    }

    @Override
    public void scheduleVacation() {
      saw2 = true;
    }

    @Override
    public void checkRoomAvailability() {
      saw3 = true;
    }

    @Override
    public void checkEmployeeAvailability() {
      saw4 = true;
    }

    @Override
    public void checkAgendaRoom() {
      saw5 = true;
    }

    @Override
    public void checkAgendaPerson() {
      saw6 = true;
    }

    // Prevent recursion from mainMenu when calls to mainMenu happen elsewhere
    @Override
    public void mainMenu() {
      callCount++;
      if (callCount < maxCalls) {
        super.mainMenu();
      }
      // Otherwise stop to prevent infinite recursion
    }

    @Override
    protected void exit() {
      exitCalled = true;
    }
  }

  // Lightweight TestPI copied from existing tests to simulate simple inputs and
  // prevent recursion by overriding mainMenu.
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
      // Prevent recursion during tests
    }
  }

  @Test
  public void testMainMenu_dispatch_eachOption() {
    // For each option 1..6, create an instance returning that input and verify it
    // calls the right method
    for (int i = 1; i <= 6; i++) {
      MenuTester mt = new MenuTester(Arrays.asList(String.valueOf(i)));
      mt.mainMenu();
      switch (i) {
        case 1:
          assertTrue(mt.saw1);
          break;
        case 2:
          assertTrue(mt.saw2);
          break;
        case 3:
          assertTrue(mt.saw3);
          break;
        case 4:
          assertTrue(mt.saw4);
          break;
        case 5:
          assertTrue(mt.saw5);
          break;
        case 6:
          assertTrue(mt.saw6);
          break;
      }
    }
  }

  @Test
  public void testMainMenu_invalidNumber_then_valid() {
    // First returns invalid number, then valid 1
    MenuTester mt = new MenuTester(Arrays.asList("9", "1"));
    mt.mainMenu();
    assertTrue(mt.saw1);
  }

  @Test
  public void testMainMenu_nonNumber_then_valid() {
    // First returns non-number, then valid 2
    MenuTester mt = new MenuTester(Arrays.asList("notanumber", "2"));
    mt.mainMenu();
    assertTrue(mt.saw2);
  }

  @Test
  public void testMainMenu_exitOption_triggersExitHook() {
    MenuTester mt = new MenuTester(Arrays.asList("0"));
    mt.mainMenu();
    assertTrue("exit() should have been called for option 0", mt.exitCalled);
  }

  @Test
  public void testScheduleMeeting_cancelRoom() {
    // month, day, start, end, room id="cancel", then attendees done and description
    TestPI pi = new TestPI(Arrays.asList("5", "5", "8", "9", "cancel", "done", "Canceled meeting"));
    // Should return to mainMenu (which TestPI overrides to do nothing)
    pi.scheduleMeeting();
  }

  @Test
  public void testScheduleVacation_cancelName() {
    // sMonth, sDay, eMonth, eDay, name="cancel"
    TestPI pi = new TestPI(Arrays.asList("6", "1", "6", "3", "cancel"));
    pi.scheduleVacation();
  }

  @Test
  public void testCheckAgendaRoom_invalidRoom() {
    // month, day, invalid room id
    TestPI pi = new TestPI(Arrays.asList("1", "10", "NOPE"));
    pi.checkAgendaRoom();
  }

  @Test
  public void testCheckAgendaPerson_invalidPerson() {
    // month, day, invalid person name
    TestPI pi = new TestPI(Arrays.asList("1", "10", "Nobody"));
    pi.checkAgendaPerson();
  }

  @Test
  public void testCheckRoomAvailability_invalidTime_shouldCatch() {
    // month, day, start time invalid (e.g., 999), end time
    TestPI pi = new TestPI(Arrays.asList("1", "1", "999", "1000"));
    pi.checkRoomAvailability();
  }

  @Test
  public void testCheckAgendaRoom_cancel() {
    // month, day, room id = cancel
    TestPI pi = new TestPI(Arrays.asList("1", "15", "cancel"));
    pi.checkAgendaRoom();
  }

  @Test
  public void testCheckAgendaPerson_cancel() {
    // month, day, name = cancel
    TestPI pi = new TestPI(Arrays.asList("1", "15", "cancel"));
    pi.checkAgendaPerson();
  }
}
