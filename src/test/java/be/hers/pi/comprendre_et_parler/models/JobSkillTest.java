package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobSkillTest {
    private static JobSkill j1;

    @BeforeAll
    public static void init() {
        j1 = new JobSkill(1, "Test");
    }

    @Test
    public void testSetId() {
        j1.setId(-1);
        assertEquals(1, j1.getId(), "id cannot be negative.");
        j1.setId(2);
        assertEquals(2, j1.getId(), "id has to change.");
    }

    @Test
    public void testHashCode() {
        int hash1 = j1.hashCode();
        int hash2 = j1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        JobSkill j2 = new JobSkill(j1);
        int hash3 = j2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        j2.setId(50);
        int hash4 = j2.hashCode();
        assertEquals(hash1, hash4, "IDs are different but must not impact the hash.");

        j2.setDesignation("The last test");
        int hash5 = j2.hashCode();
        assertNotEquals(hash4, hash5, "One attribute other than the ID has changed.");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, j1, "The second object is null.");
        assertEquals(j1, j1, "The second object is the same as the first one.");

        JobSkill j2 = new JobSkill(j1);
        assertEquals(j1, j2, "The second object is a copy of the first one.");

        j1.setId(20);
        assertEquals(j2, j1, "The second object has its id changed.");

        j2.setDesignation("The last test");
        assertNotEquals(j2, j1, "The second object has one of its attributes other than its id changed.");
    }

    @Test
    public void testCompareTo() {
        assertThrows(NullPointerException.class, () -> {
            j1.compareTo(null);
        }, "The second object is null.");

        assertEquals(0, j1.compareTo(j1), "The second object is the same as the first one.");

        JobSkill j2 = new JobSkill(j1);
        assertEquals(0, j1.compareTo(j2), "The second object is a copy of the first one.");

        j2.setDesignation("The last test");
        assertTrue(j1.compareTo(j2) < 0, "The first object lexicographically precedes the second one.");
        assertTrue(j2.compareTo(j1) > 0, "The first object lexicographically follows the second one.");
    }
}

