package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransportationTest {
    private static Transportation t1;

    @BeforeAll
    public static void init() {
        t1 = new Transportation(1, "test");
    }

    @Test
    public void testSetId() {
        t1.setId(-1);
        assertEquals(1, t1.getId());
        t1.setId(2);
        assertEquals(2, t1.getId());
    }

    @Test
    public void testEquals() {
        assertFalse(t1.equals(null));
        assertTrue(t1.equals(t1));

        Transportation t2 = new Transportation(t1);
        assertTrue(t1.equals(t2));

        t1.setId(20);
        assertTrue(t2.equals(t1));

        t2.setDesignation("Dernier test");
        assertFalse(t2.equals(t1));
    }
}