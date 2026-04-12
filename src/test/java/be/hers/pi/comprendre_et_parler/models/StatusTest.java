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
        assertEquals(1, s1.getId());
        s1.setId(2);
        assertEquals(2, s1.getId());
    }

    @Test
    public void testSetHourQuota() {
        s1.setHourQuota(-1);
        assertEquals(20, s1.getHourQuota());
        s1.setHourQuota(25);
        assertEquals(25, s1.getHourQuota());
    }

    @Test
    public void testEquals() {
        assertFalse(s1.equals(null));
        assertTrue(s1.equals(s1));

        Status s2 = new Status(s1);
        assertTrue(s1.equals(s2));

        s2.setDesignation("Dernier test");
        assertFalse(s2.equals(s1));
    }
}