package be.hers.pi.comprendre_et_parler.models;

import be.hers.pi.comprendre_et_parler.DAOs.DAOCity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class BeneficiaryTest {
    private static Beneficiary b1;

    @BeforeAll
    public static void init() {
        City c1 = new City("Bruxelles", 1000);
        Location l1 = new Location("Ici", c1, "Test", "a3", 1);
        Interpreter i1 = new Interpreter(1, "1", "Test", "Test", LocalDate.now(),
                "1234", "test@gmail.com", "123/45.67.89", 10, 120,
                "Auto", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        i1.setUnavailability(new HashSet<>());
        b1 = new Beneficiary(1, "1", "Test", "Test", LocalDate.now(),
                "1234", "test@gmail.com", "123/45.67.89",
                new Status(1, "Test", 10), i1);
    }

    @Test
    public void testHashCode() {
        int hash1 = b1.hashCode();
        int hash2 = b1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        Beneficiary b2 = new Beneficiary(b1);
        int hash3 = b2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        b2.setStatus(new Status(2, "The last test", 90));
        int hash4 = b2.hashCode();
        assertNotEquals(hash3, hash4, "One attribute other than the ID has changed.");
    }

    @Test
    void testEquals() {
        assertNotEquals(null, b1, "The second object is null.");
        assertEquals(b1, b1, "The second object is the same as the first one.");

        Beneficiary b2 = new Beneficiary(b1);
        assertEquals(b1, b2, "The second object is a copy of the first one.");

        b2.setStatus(new Status(3, "The last test", 40));
        assertNotEquals(b2, b1, "The second object has one of its attributes other than its id changed.");
    }
}