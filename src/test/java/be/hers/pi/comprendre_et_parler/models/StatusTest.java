package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {
    private static Status s1;

    @BeforeAll
    public static void init() {
        s1 = new Status(1, "test", 20);
    }

    @Test
    public void testSetId() {
        s1.setId(-1);
        assertEquals(1, s1.getId(), "id cannot be negative.");
        s1.setId(2);
        assertEquals(2, s1.getId(), "id has to change.");
    }

    @Test
    public void testSetHourQuota() {
        s1.setHourQuota(-1);
        assertEquals(20, s1.getHourQuota(), "hourQuota cannot be negative.");
        s1.setHourQuota(25);
        assertEquals(25, s1.getHourQuota(), "hourQuota has to change.");
    }

    @Test
    public void testEquals() {
        assertFalse(s1.equals(null), "The second object is null.");
        assertTrue(s1.equals(s1), "The second object is the same as the first one.");

        Status s2 = new Status(s1);
        assertTrue(s1.equals(s2), "The second object is a copy of the first one.");

        s1.setId(20);
        assertTrue(s2.equals(s1), "The second object has its id changed.");

        s2.setDesignation("Dernier test");
        assertFalse(s2.equals(s1), "The second object has one of its attributes other than its id changed.");
    }
}