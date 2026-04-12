package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityTest {
    private static City c1;

    @BeforeAll
    public static void init() {
        c1 = new City(1, "Libramont", 5200);
    }

    @Test
    public void testSetId() {
        c1.setId(-1);
        assertEquals(1, c1.getId(), "id cannot be negative.");
        c1.setId(2);
        assertEquals(2, c1.getId(), "id has to change.");
    }

    @Test
    public void testSetPostalCode() {
        c1.setPostalCode(-1);
        assertEquals(5200, c1.getPostalCode(), "postalCode cannot be negative.");
        c1.setPostalCode(6800);
        assertEquals(6800, c1.getPostalCode(), "postalCode has to change.");
        c1.setPostalCode(10000);
        assertEquals(6800, c1.getPostalCode(), "postalCode cannot be greater than 9999.");
    }

    @Test
    public void testEquals() {
        assertFalse(c1.equals(null), "The second object is null.");
        assertTrue(c1.equals(c1), "The second object is the same as the first one.");

        City c2 = new City(c1);
        assertTrue(c1.equals(c2), "The second object is a copy of the first one.");

        c1.setId(20);
        assertTrue(c2.equals(c1), "The second object has its id changed.");

        c2.setDesignation("Dernier test");
        assertFalse(c2.equals(c1), "The second object has one of its attributes other than its id changed.");
    }
}