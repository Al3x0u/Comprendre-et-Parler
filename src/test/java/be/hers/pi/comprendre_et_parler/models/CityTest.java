package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityTest {
    private static City c1;

    @BeforeAll
    public static void init() {
        c1 = new City(1, "Libramont", 6800);
    }

    @Test
    public void testSetId() {
        c1.setId(-1);
        assertEquals(1, c1.getId());
        c1.setId(2);
        assertEquals(2, c1.getId());
    }

    @Test
    public void testSetPostalCode() {
        c1.setPostalCode(-1);
        assertEquals(5200, c1.getPostalCode());
        c1.setPostalCode(6800);
        assertEquals(6800, c1.getPostalCode());
        c1.setPostalCode(10000);
        assertEquals(6800, c1.getPostalCode());
    }

    @Test
    public void testEquals() {
        assertFalse(c1.equals(null));
        assertTrue(c1.equals(c1));

        City c2 = new City(c1);
        assertTrue(c1.equals(c2));

        c2.setDesignation("Dernier test");
        assertFalse(c2.equals(c1));
    }
}