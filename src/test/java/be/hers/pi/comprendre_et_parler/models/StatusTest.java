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
    public void testHashCode() {
        int hash1 = s1.hashCode();
        int hash2 = s1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        Status s2 = new Status(s1);
        int hash3 = s2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        s2.setId(50);
        int hash4 = s2.hashCode();
        assertEquals(hash1, hash4, "IDs are different but must not impact the hash.");

        s2.setDesignation("The last test");
        int hash5 = s2.hashCode();
        assertNotEquals(hash4, hash5, "One attribute other than the ID has changed.");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, s1, "The second object is null.");
        assertEquals(s1, s1, "The second object is the same as the first one.");

        Status s2 = new Status(s1);
        assertEquals(s1, s2, "The second object is a copy of the first one.");

        s1.setId(20);
        assertEquals(s2, s1, "The second object has its id changed.");

        s2.setDesignation("The last test");
        assertNotEquals(s2, s1, "The second object has one of its attributes other than its id changed.");
    }
}