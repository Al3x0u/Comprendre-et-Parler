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
        assertEquals(1, j1.getId());
        j1.setId(2);
        assertEquals(2, j1.getId());
    }

    @Test
    public void testEquals() {
        assertFalse(j1.equals(null));
        assertTrue(j1.equals(j1));

        JobSkill j2 = new JobSkill(j1);
        assertTrue(j1.equals(j2));

        j2.setDesignation("Dernier test");
        assertFalse(j2.equals(j1));
    }
}