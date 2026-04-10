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
        assertEquals(1, a1.getId());
        a1.setId(2);
        assertEquals(2, a1.getId());
    }

    @Test
    public void testEquals() {
        AcademicSkill a2 = new AcademicSkill(a1);
        AcademicSkill a3 = new AcademicSkill(10, "test");

        assertFalse(a1.equals(null));
        assertTrue(a1.equals(a1));
        assertTrue(a1.equals(a2));
        assertFalse(a2.equals(a3));
    }
}