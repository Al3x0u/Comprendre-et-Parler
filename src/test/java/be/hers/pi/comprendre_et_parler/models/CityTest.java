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
    public void testHashCode() {
        int hash1 = c1.hashCode();
        int hash2 = c1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        City c2 = new City(c1);
        int hash3 = c2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        c2.setId(50);
        int hash4 = c2.hashCode();
        assertEquals(hash1, hash4, "IDs are different but must not impact the hash.");

        c2.setDesignation("The last test");
        int hash5 = c2.hashCode();
        assertNotEquals(hash4, hash5, "One attribute other than the ID has changed.");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, c1, "The second object is null.");
        assertEquals(c1, c1, "The second object is the same as the first one.");

        City c2 = new City(c1);
        assertEquals(c1, c2, "The second object is a copy of the first one.");

        c1.setId(20);
        assertEquals(c2, c1, "The second object has its id changed.");

        c2.setDesignation("The last test");
        assertNotEquals(c2, c1, "The second object has one of its attributes other than its id changed.");
    }
}