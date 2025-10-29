package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class CalendarTest {
	// Add test methods here.
	// You are not required to write tests for all classes.

	@Test
	public void testAddMeeting_holiday() {
		// Create Midsommar holiday
		Calendar calendar = new Calendar();
		// Add to calendar object.
		try {
			Meeting midsommar = new Meeting(6, 26, "Midsommar");
			calendar.addMeeting(midsommar);
			// Verify that it was added.
			Boolean added = calendar.isBusy(6, 26, 0, 23);
			assertTrue("Midsommar should be marked as busy on the calendar", added);
		} catch (TimeConflictException e) {
			fail("Should not throw exception: " + e.getMessage());
		}
	}

	@Test
	public void testAddMeeting_noOverlap_shouldSucceed() {
		Calendar calendar = new Calendar();
		try {
			calendar.addMeeting(new Meeting(3, 15, 9, 10));
			calendar.addMeeting(new Meeting(3, 15, 11, 12));
			assertTrue("9-10 should be busy", calendar.isBusy(3, 15, 9, 10));
			assertTrue("11-12 should be busy", calendar.isBusy(3, 15, 11, 12));
		} catch (TimeConflictException e) {
			fail("Non-overlapping meetings should not conflict: " + e.getMessage());
		}
	}

	@Test
	public void testAddMeeting_overlap_startInside_shouldFail() {
		Calendar calendar = new Calendar();
		try {
			calendar.addMeeting(new Meeting(5, 10, 10, 12));
			calendar.addMeeting(new Meeting(5, 10, 11, 13));
			fail("Expected overlap conflict when start is inside existing meeting");
		} catch (TimeConflictException e) {
			// expected
		}
	}

	@Test
	public void testAddMeeting_overlap_endInside_shouldFail() {
		Calendar calendar = new Calendar();
		try {
			calendar.addMeeting(new Meeting(5, 10, 10, 12));
			calendar.addMeeting(new Meeting(5, 10, 9, 11));
			fail("Expected overlap conflict when end is inside existing meeting");
		} catch (TimeConflictException e) {
			// expected
		}
	}

	@Test
	public void testAddMeeting_enclosingExisting_currentBug_expectedToMissConflict() {
		Calendar calendar = new Calendar();
		try {
			calendar.addMeeting(new Meeting(7, 20, 10, 11));
			// This meeting fully encloses the existing one (9-12), current implementation
			// does not check this
			calendar.addMeeting(new Meeting(7, 20, 9, 12));
			fail("Should detect enclosure overlap (known bug) and throw TimeConflictException");
		} catch (TimeConflictException e) {
			// When bug is fixed, this path will be taken. For now, this test may fail,
			// exposing the defect.
		}
	}

	@Test
	public void testCheckTimes_invalidDay_shouldThrow() {
		try {
			Calendar.checkTimes(3, 0, 9, 10);
			fail("Invalid day should throw");
		} catch (TimeConflictException e) {
			// expected
		}
	}

	@Test
	public void testCheckTimes_invalidMonth_shouldThrow() {
		try {
			Calendar.checkTimes(0, 10, 9, 10);
			fail("Invalid month should throw");
		} catch (TimeConflictException e) {
			// expected
		}
	}

	@Test
	public void testCheckTimes_monthTwelve_shouldBeValid() {
		try {
			Calendar.checkTimes(12, 10, 9, 10);
			// pass
		} catch (TimeConflictException e) {
			fail("Month 12 should be valid but threw: " + e.getMessage());
		}
	}

	@Test
	public void testCheckTimes_startAfterEnd_shouldThrow() {
		try {
			Calendar.checkTimes(3, 10, 10, 9);
			fail("Start after end should throw");
		} catch (TimeConflictException e) {
			// expected
		}
	}
}