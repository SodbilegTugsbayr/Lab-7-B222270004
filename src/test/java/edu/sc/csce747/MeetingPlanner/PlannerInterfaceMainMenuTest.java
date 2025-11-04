package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;

public class PlannerInterfaceMainMenuTest {

  private static class TestPI extends PlannerInterface {
    Queue<String> inputs;
    boolean scheduleCalled = false;
    boolean vacationCalled = false;
    boolean checkRoomCalled = false;
    boolean checkEmployeeCalled = false;
    boolean checkAgendaRoomCalled = false;
    boolean checkAgendaPersonCalled = false;

    TestPI(List<String> inputs) {
      super();
      this.inputs = new LinkedList<>(inputs);
    }

    @Override
    protected String inputOutput(String message) {
      String s = inputs.poll();
      return s == null ? "" : s;
    }

    @Override
    public void scheduleMeeting() {
      scheduleCalled = true;
    }

    @Override
    public void scheduleVacation() {
      vacationCalled = true;
    }

    @Override
    public void checkRoomAvailability() {
      checkRoomCalled = true;
    }

    @Override
    public void checkEmployeeAvailability() {
      checkEmployeeCalled = true;
    }

    @Override
    public void checkAgendaRoom() {
      checkAgendaRoomCalled = true;
    }

    @Override
    public void checkAgendaPerson() {
      checkAgendaPersonCalled = true;
    }

    // prevent calling super.mainMenu from schedule* to avoid recursion
  }

  @Test
  public void testMainMenu_scheduleMeeting() {
    TestPI pi = new TestPI(Arrays.asList("1"));
    pi.mainMenu();
    assertTrue(pi.scheduleCalled);
  }

  @Test
  public void testMainMenu_invalidThenSchedule() {
    TestPI pi = new TestPI(Arrays.asList("9", "1"));
    pi.mainMenu();
    assertTrue(pi.scheduleCalled);
  }

  @Test
  public void testMainMenu_numberFormatThenSchedule() {
    TestPI pi = new TestPI(Arrays.asList("bad", "2"));
    pi.mainMenu();
    assertTrue(pi.vacationCalled);
  }

  @Test
  public void testMainMenu_otherOptions() {
    TestPI pi = new TestPI(Arrays.asList("3"));
    pi.mainMenu();
    assertTrue(pi.checkRoomCalled);

    TestPI pi2 = new TestPI(Arrays.asList("4"));
    pi2.mainMenu();
    assertTrue(pi2.checkEmployeeCalled);

    TestPI pi3 = new TestPI(Arrays.asList("5"));
    pi3.mainMenu();
    assertTrue(pi3.checkAgendaRoomCalled);

    TestPI pi4 = new TestPI(Arrays.asList("6"));
    pi4.mainMenu();
    assertTrue(pi4.checkAgendaPersonCalled);
  }
}
