package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;

public class MeetingTest {
    @Test
    public void testDayConstructor_allDayMeeting() {
        Meeting m = new Meeting(2, 10);
        assertEquals(2, m.getMonth());
        assertEquals(10, m.getDay());
        assertEquals(0, m.getStartTime());
        assertEquals(23, m.getEndTime());
    }

    @Test
    public void testDetailedConstructor_andGetters() {
        ArrayList<Person> attendees = new ArrayList<Person>();
        Room room = new Room("2A01");
        Meeting m = new Meeting(8, 3, 9, 11, attendees, room, "Standup");
        assertEquals(8, m.getMonth());
        assertEquals(3, m.getDay());
        assertEquals(9, m.getStartTime());
        assertEquals(11, m.getEndTime());
        assertSame(attendees, m.getAttendees());
        assertSame(room, m.getRoom());
        assertEquals("Standup", m.getDescription());
    }

    @Test
    public void testAddRemoveAttendee() {
        ArrayList<Person> attendees = new ArrayList<Person>();
        Meeting m = new Meeting(1, 1, 10, 11, attendees, new Room("2A02"), "1:1");
        Person alice = new Person("Alice");
        Person bob = new Person("Bob");
        m.addAttendee(alice);
        m.addAttendee(bob);
        assertEquals(2, m.getAttendees().size());
        m.removeAttendee(alice);
        assertEquals(1, m.getAttendees().size());
    }

    @Test
    public void testToString_includesKeyDetails() {
        ArrayList<Person> attendees = new ArrayList<Person>();
        attendees.add(new Person("Alice"));
        attendees.add(new Person("Bob"));
        Room room = new Room("R1");
        Meeting m = new Meeting(8, 3, 9, 11, attendees, room, "Standup");
        String s = m.toString();
        assertTrue(s.contains("8/3, 9 - 11,R1: Standup"));
        assertTrue(s.contains("Attending:"));
        assertTrue(s.contains("Alice"));
        assertTrue(s.contains("Bob"));
    }
}