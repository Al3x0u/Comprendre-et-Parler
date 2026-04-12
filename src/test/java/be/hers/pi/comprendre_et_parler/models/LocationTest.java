package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {
    private static City c1;
    private static Location l1;

    @BeforeAll
    public static void init() {
        c1 = new City(1, "Libramont", 6800);
        l1 = new Location(1, "test", c1, "test", "test", 1);
    }


    @Test
    public void testSetId() {
        l1.setId(-1);
        assertEquals(1, l1.getId(), "id cannot be negative.");
        l1.setId(2);
        assertEquals(2, l1.getId(), "id has to change.");
    }

    @Test
    public void testSetBox() {
        l1.setBox(-1);
        assertEquals(1, l1.getBox(), "box cannot be negative.");
        l1.setBox(2);
        assertEquals(2, l1.getBox(), "box has to change.");
    }

    @Test
    public void testSetCity() {
        City c2 = new City(c1);
        l1.setCity(c2);
        c2.setPostalCode(6900);
        assertFalse(c2.equals(l1.getCity()), "Modifications effected on the obtained object cannot change the original object.");
    }

    @Test
    public void testGetCity() {
        City c2 = l1.getCity();
        assertEquals(c2, l1.getCity(), "Must obtain an exact copy of the object.");

        c2.setPostalCode(7800);
        assertFalse(c2.equals(l1.getCity()), "The original object has to remain itself.");
    }

    @Test
    public void testEquals() {
        assertFalse(l1.equals(null), "The second object is null.");
        assertTrue(l1.equals(l1), "The second object is the same as the first one.");

        Location l2 = new Location(l1);
        assertTrue(l1.equals(l2), "The second object is a copy of the first one.");

        l1.setId(20);
        assertTrue(l2.equals(l1), "The second object has its id changed.");

        l2.setDesignation("Dernier test");
        assertFalse(l2.equals(l1), "The second object has one of its attributes other than its id changed.");
    }
}