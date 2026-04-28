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
    public void testHashCode() {
        int hash1 = l1.hashCode();
        int hash2 = l1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        Location l2 = new Location(l1);
        int hash3 = l2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        l2.setId(50);
        int hash4 = l2.hashCode();
        assertEquals(hash1, hash4, "IDs are different but must not impact the hash.");

        l2.setStreetNumber("The last test");
        int hash5 = l2.hashCode();
        assertNotEquals(hash4, hash5, "One attribute other than the ID has changed.");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, l1, "The second object is null.");
        assertEquals(l1, l1, "The second object is the same as the first one.");

        Location l2 = new Location(l1);
        assertEquals(l1, l2, "The second object is a copy of the first one.");

        l1.setId(20);
        assertEquals(l2, l1, "The second object has its id changed.");

        l2.setDesignation("The last test");
        assertNotEquals(l2, l1, "The second object has one of its attributes other than its id changed.");
    }
}