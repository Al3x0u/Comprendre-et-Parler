package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AppliUserTest {
    private static AppliUser a1;

    @BeforeAll
    public static void init() {
        Interpreter interpreterTest = new Interpreter("i2365", "samou", "juni", LocalDate.of(2000, 6, 1), "qsdfghjklm", "samoujuni@gmail.com","0489134440", 10, 120, "car", Set.of(), Set.of(), new Location(0, "e", new City("e", 6000), "s", "e", 1), Set.of(), Set.of());
        a1 = new Beneficiary(1, "1", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), interpreterTest);
    }

    @Test
    void testSetId() {
        a1.setId(-1);
        assertEquals(1, a1.getId(), "id cannot be negative.");
        a1.setId(2);
        assertEquals(2, a1.getId(), "id has to change.");
    }

    @Test
    public void testHashCode() {
        int hash1 = a1.hashCode();
        int hash2 = a1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        AppliUser a2 = a1.clone();
        int hash3 = a2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        a2.setId(50);
        int hash4 = a2.hashCode();
        assertEquals(hash1, hash4, "IDs are different but must not impact the hash.");

        a2.setLastName("The last test");
        int hash5 = a2.hashCode();
        assertNotEquals(hash4, hash5, "One attribute other than the ID has changed.");
    }

    @Test
    void testEquals() {
        assertNotEquals(null, a1, "The second object is null.");
        assertEquals(a1, a1, "The second object is the same as the first one.");

        AppliUser a2 = a1.clone();
        assertEquals(a1, a2, "The second object is a copy of the first one.");

        a1.setId(20);
        assertEquals(a2, a1, "The second object has its id changed.");

        a2.setFirstName("The last test");
        assertNotEquals(a2, a1, "The second object has one of its attributes other than its id changed.");
    }
}