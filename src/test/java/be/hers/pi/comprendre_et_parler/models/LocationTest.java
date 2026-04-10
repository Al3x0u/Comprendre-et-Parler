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
        assertEquals(1, l1.getId());
        l1.setId(2);
        assertEquals(2, l1.getId());
    }

    @Test
    public void testSetBox() {
        l1.setBox(-1);
        assertEquals(1, l1.getBox());
        l1.setBox(2);
        assertEquals(2, l1.getBox());
    }

    @Test
    public void testSetCity() {
        c1.setPostalCode(6900);
        assertFalse(c1.equals(l1.getCity()));
    }

    @Test
    public void testGetCity() {
        City c2 = l1.getCity();
        c2.setPostalCode(7800);
        assertTrue(c1.equals(l1.getCity()));
    }

    @Test
    public void testEquals() {
        Location l2 = new Location(l1);
        Location l3 = new Location(10, "test", c1, "test", "test", 1);

        assertFalse(l1.equals(null));
        assertTrue(l1.equals(l1));
        assertTrue(l1.equals(l2));
        assertFalse(l2.equals(l3));
    }
}