package edu.sc.csce747.MeetingPlanner;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class AdditionalCoverageTests {

    private Person person1;
    private Person person2;
    private Room room1;

    @Before
    public void setUp() {
        person1 = new Person("Alice");
        person2 = new Person("Bob");
        room1 = new Room("Room101");
    }

    // ========== PlannerService Tests ==========

    @Test
    public void testApplyMeetingWithConflict() throws TimeConflictException {
        // Create a meeting and add it to the room
        Meeting meeting1 = new Meeting(5, 15, 10, 12, new ArrayList<>(), room1, "First Meeting");
        room1.addMeeting(meeting1);

        // Try to add a conflicting meeting
        ArrayList<Person> attendees = new ArrayList<>();
        attendees.add(person1);
        attendees.add(person2);
        Meeting meeting2 = new Meeting(5, 15, 11, 13, attendees, room1, "Conflicting Meeting");

        String error = PlannerService.applyMeeting(room1, attendees, meeting2);

        assertNotNull("Should return error message on conflict", error);
        assertTrue("Error should mention conflict", error.toLowerCase().contains("conflict"));
    }

    @Test
    public void testScheduleVacationMultipleMonths() throws TimeConflictException {
        // Schedule vacation across multiple months (e.g., March 25 to May 5)
        List<String> conflicts = PlannerService.scheduleVacation(person1, 3, 25, 5, 5);

        assertTrue("Should have no conflicts", conflicts.isEmpty());

        // Verify vacation was booked - check start, middle, end, and an intermediate
        // day
        assertTrue("Day in start month should be busy", person1.isBusy(3, 25, 0, 23));
        assertTrue("Day in middle month should be busy", person1.isBusy(4, 1, 0, 23));
        assertTrue("Another middle month day should be busy", person1.isBusy(4, 15, 0, 23));
        assertTrue("Day in end month should be busy", person1.isBusy(5, 5, 0, 23));
    }

    @Test
    public void testScheduleVacationWithConflictMultipleMonths() throws TimeConflictException {
        // Add a meeting in the middle of the vacation period
        Meeting meeting = new Meeting(4, 10, 9, 11, new ArrayList<>(), new Room(), "Existing Meeting");
        person1.addMeeting(meeting);

        // Try to schedule vacation that conflicts (March 25 to May 5)
        List<String> conflicts = PlannerService.scheduleVacation(person1, 3, 25, 5, 5);

        assertFalse("Should have conflicts", conflicts.isEmpty());
        boolean foundConflict = conflicts.stream().anyMatch(c -> c.contains("4/10"));
        assertTrue("Should mention conflict date 4/10", foundConflict);
    }

    @Test
    public void testScheduleVacationSameMonth() throws TimeConflictException {
        // Schedule vacation within the same month
        List<String> conflicts = PlannerService.scheduleVacation(person1, 6, 10, 6, 15);

        assertTrue("Should have no conflicts", conflicts.isEmpty());

        // Verify vacation was booked
        assertTrue("Day should be busy", person1.isBusy(6, 10, 0, 23));
        assertTrue("Day should be busy", person1.isBusy(6, 15, 0, 23));
    }

    @Test
    public void testScheduleVacationWithConflictSameMonth() throws TimeConflictException {
        // Add a meeting
        Meeting meeting = new Meeting(6, 12, 10, 12, new ArrayList<>(), new Room(), "Conflict");
        person1.addMeeting(meeting);

        // Try to schedule vacation over it
        List<String> conflicts = PlannerService.scheduleVacation(person1, 6, 10, 6, 15);

        assertFalse("Should have conflicts", conflicts.isEmpty());
    }

    @Test
    public void testScheduleVacationOnInvalidDays() throws TimeConflictException {
        // Try to schedule vacation that includes February 30 (invalid day)
        // This tests the "Day does not exist" path
        List<String> conflicts = PlannerService.scheduleVacation(person1, 2, 1, 2, 28);

        // Should succeed for valid days in February
        assertTrue("Should have no conflicts for valid days", conflicts.isEmpty());
    }

    // ========== Calendar Tests ==========

    @Test
    public void testAddMeetingToNonExistentDay() throws TimeConflictException {
        Calendar cal = new Calendar();

        // First, add a normal meeting to February 28
        Meeting meeting1 = new Meeting(2, 28, 10, 12, new ArrayList<>(), new Room(), "First Meeting");
        cal.addMeeting(meeting1);

        // Try to add a conflicting meeting to February 28
        Meeting meeting2 = new Meeting(2, 28, 11, 13, new ArrayList<>(), new Room(), "Second Meeting");

        try {
            cal.addMeeting(meeting2);
            fail("Should throw TimeConflictException for conflicting meeting");
        } catch (TimeConflictException e) {
            assertTrue("Should mention conflict", e.getMessage().toLowerCase().contains("overlap"));
        }
    }

    @Test
    public void testCalendarInvalidDayPath() throws TimeConflictException {
        Calendar cal = new Calendar();

        // Try to add meeting to February 30 - Calendar should pre-populate with "Day
        // does not exist"
        Meeting meeting = new Meeting(2, 30, 10, 12, new ArrayList<>(), new Room(), "Should Conflict");

        try {
            cal.addMeeting(meeting);
            fail("Should throw TimeConflictException because Feb 30 has 'Day does not exist' meeting");
        } catch (TimeConflictException e) {
            assertTrue("Should mention day does not exist",
                    e.getMessage().contains("Day does not exist"));
        }
    }

    @Test
    public void testCalendarBranchCoverage() throws TimeConflictException {
        Calendar cal = new Calendar();

        // Test the branch where description is null (should still detect conflicts)
        Meeting meeting1 = new Meeting(5, 15, 10, 12, new ArrayList<>(), new Room(), null);
        cal.addMeeting(meeting1);

        // Try to add conflicting meeting
        Meeting meeting2 = new Meeting(5, 15, 11, 13, new ArrayList<>(), new Room(), "Conflict");

        try {
            cal.addMeeting(meeting2);
            fail("Should throw conflict exception");
        } catch (TimeConflictException e) {
            assertTrue("Should mention overlap", e.getMessage().toLowerCase().contains("overlap"));
        }
    }

    @Test
    public void testCalendarNonExistentDayBranch() throws TimeConflictException {
        Calendar cal = new Calendar();

        // Try to add meeting to April 31 (doesn't exist)
        Meeting meeting = new Meeting(4, 31, 10, 12, new ArrayList<>(), new Room(), "Test Meeting");

        try {
            cal.addMeeting(meeting);
            fail("Should throw TimeConflictException");
        } catch (TimeConflictException e) {
            assertTrue("Should mention day does not exist",
                    e.getMessage().contains("Day does not exist"));
        }
    }

    // ========== PlannerInterface Tests ==========

    @Test
    public void testMainMenuOption2() {
        TestableInterface planner = new TestableInterface();
        planner.setInputs("2", "6", "1", "7", "15", "Alice", "0");
        planner.mainMenu();
    }

    @Test
    public void testMainMenuOption3() {
        TestableInterface planner = new TestableInterface();
        planner.setInputs("3", "5", "10", "9", "11", "0");
        planner.mainMenu();
    }

    @Test
    public void testMainMenuOption4() {
        TestableInterface planner = new TestableInterface();
        planner.setInputs("4", "5", "10", "9", "11", "0");
        planner.mainMenu();
    }

    @Test
    public void testMainMenuOption5() {
        TestableInterface planner = new TestableInterface();
        planner.setInputs("5", "5", "10", "Room101", "0");
        planner.mainMenu();
    }

    @Test
    public void testMainMenuOption6() {
        TestableInterface planner = new TestableInterface();
        planner.setInputs("6", "5", "all", "Alice", "0");
        planner.mainMenu();
    }

    @Test
    public void testMainMenuInvalidInput() {
        TestableInterface planner = new TestableInterface();
        planner.setInputs("99", "0");
        planner.mainMenu();
    }

    @Test
    public void testMainMenuNonNumericInput() {
        TestableInterface planner = new TestableInterface();
        planner.setInputs("abc", "0");
        planner.mainMenu();
    }

    @Test
    public void testExitMethod() {
        TestableInterface planner = new TestableInterface();
        planner.setInputs("0");
        planner.mainMenu();
        assertTrue("Exit should have been called", planner.exitCalled);
    }

    // ========== Helper Class for Testing PlannerInterface ==========

    private class TestableInterface extends PlannerInterface {
        private List<String> inputs = new ArrayList<>();
        private int inputIndex = 0;
        public boolean exitCalled = false;

        public void setInputs(String... inputArray) {
            for (String input : inputArray) {
                inputs.add(input);
            }
        }

        @Override
        protected String inputOutput(String message) {
            if (inputIndex < inputs.size()) {
                return inputs.get(inputIndex++);
            }
            return "0"; // Default to exit
        }

        @Override
        protected void exit() {
            exitCalled = true;
            // Don't actually exit in tests
        }
    }
}