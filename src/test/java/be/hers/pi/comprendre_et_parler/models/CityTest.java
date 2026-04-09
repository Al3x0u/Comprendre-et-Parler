package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CityTest {
    private static City c1;

    @BeforeAll
    public void init() {
        City c1 = new City(1, "Libramont", 6800);
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
        City c2 = new City(c1);
        City c3 = new City(10, "Libramont", 6800);

        Assertions.assertFalse(c1.equals(null));
        Assertions.assertTrue(c1.equals(c1));
        Assertions.assertTrue(c1.equals(c2));
        Assertions.assertFalse(c2.equals(c3));
    }
}