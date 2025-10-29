package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;

public class PersonTest {
    @Test
    public void testAddMeeting_conflictMessageWrapped() {
        Person alice = new Person("Alice");
        try {
            alice.addMeeting(new Meeting(4, 5, 10, 12));
        } catch (TimeConflictException e) {
            fail("First meeting should add fine: " + e.getMessage());
        }

        try {
            alice.addMeeting(new Meeting(4, 5, 11, 13));
            fail("Should throw conflict wrapped with attendee name");
        } catch (TimeConflictException e) {
            assertTrue("Message should contain attendee name", e.getMessage().contains("Alice"));
        }
    }

    @Test
    public void testPrintAgenda_returnsString() {
        Person bob = new Person("Bob");
        String agenda = bob.printAgenda(3);
        assertNotNull(agenda);
    }
}