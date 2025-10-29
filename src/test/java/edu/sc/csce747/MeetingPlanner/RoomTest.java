package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;

public class RoomTest {
    @Test
    public void testAddMeeting_conflictMessageWrapped() {
        Room r201 = new Room("2A01");
        try {
            r201.addMeeting(new Meeting(9, 9, 14, 16));
        } catch (TimeConflictException e) {
            fail("First meeting should add fine: " + e.getMessage());
        }

        try {
            r201.addMeeting(new Meeting(9, 9, 15, 17));
            fail("Should throw conflict wrapped with room id");
        } catch (TimeConflictException e) {
            assertTrue("Message should contain room id", e.getMessage().contains("2A01"));
        }
    }

    @Test
    public void testPrintAgenda_returnsString() {
        Room r202 = new Room("2A02");
        String agenda = r202.printAgenda(1);
        assertNotNull(agenda);
    }
}