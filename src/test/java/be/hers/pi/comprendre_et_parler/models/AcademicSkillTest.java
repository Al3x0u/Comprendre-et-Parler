package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AcademicSkillTest {
    private static AcademicSkill a1;

    @BeforeAll
    public static void init() {
        a1 = new AcademicSkill(1, "test");
    }

    @Test
    public void testSetId() {
        a1.setId(-1);
        assertEquals(1, a1.getId(), "id cannot be negative.");
        a1.setId(2);
        assertEquals(2, a1.getId(), "id has to change.");
    }

    @Test
    public void testEquals() {
        assertFalse(a1.equals(null), "The second object is null.");
        assertTrue(a1.equals(a1), "The second object is the same as the first one.");

        AcademicSkill a2 = new AcademicSkill(a1);
        assertTrue(a1.equals(a2), "The second object is a copy of the first one.");

        a1.setId(20);
        assertTrue(a2.equals(a1), "The second object has its id changed.");

        a2.setDesignation("Dernier test");
        assertFalse(a2.equals(a1), "The second object has one of its attributes other than its id changed.");
    }
}