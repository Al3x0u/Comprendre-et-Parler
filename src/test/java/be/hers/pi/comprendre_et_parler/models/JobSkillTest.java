package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobSkillTest {
    private static JobSkill j1;

    @BeforeAll
    public static void init() {
        j1 = new JobSkill(1, "test");
    }

    @Test
    public void testSetId() {
        j1.setId(-1);
        assertEquals(1, j1.getId(), "id cannot be negative.");
        j1.setId(2);
        assertEquals(2, j1.getId(), "id has to change.");
    }

    @Test
    public void testEquals() {
        assertFalse(j1.equals(null), "The second object is null.");
        assertTrue(j1.equals(j1), "The second object is the same as the first one.");

        JobSkill j2 = new JobSkill(j1);
        assertTrue(j1.equals(j2), "The second object is a copy of the first one.");

        j1.setId(20);
        assertTrue(j2.equals(j1), "The second object has its id changed.");

        j2.setDesignation("Dernier test");
        assertFalse(j2.equals(j1), "The second object has one of its attributes other than its id changed.");
    }
}