package edu.sc.csce747.MeetingPlanner;

import static org.junit.Assert.*;
import org.junit.Test;

public class OrganizationTest {
    @Test
    public void testGetRoom_existing() throws Exception {
        Organization org = new Organization();
        Room r = org.getRoom("2A01");
        assertEquals("2A01", r.getID());
    }

    @Test
    public void testGetRoom_nonExisting_shouldThrow() {
        Organization org = new Organization();
        try {
            org.getRoom("NOPE");
            fail("Expected exception for missing room");
        } catch (Exception e) {
            assertTrue(e.getMessage().toLowerCase().contains("room"));
        }
    }

    @Test
    public void testGetEmployee_existing() throws Exception {
        Organization org = new Organization();
        Person p = org.getEmployee("Greg Gay");
        assertEquals("Greg Gay", p.getName());
    }

    @Test
    public void testGetEmployee_nonExisting_shouldThrow() {
        Organization org = new Organization();
        try {
            org.getEmployee("Unknown Person");
            fail("Expected exception for missing employee");
        } catch (Exception e) {
            assertTrue(e.getMessage().toLowerCase().contains("employee"));
        }
    }
}