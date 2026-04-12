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
        Transportation t2 = new Transportation(t1);
        Transportation t3 = new Transportation(10, "test");

        assertFalse(t1.equals(null));
        assertTrue(t1.equals(t1));
        assertTrue(t1.equals(t2));
        assertFalse(t2.equals(t3));
    }
}