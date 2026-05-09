package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionalUnavailabilityTest {
    private static ExceptionalUnavailability e1;

    @BeforeAll
    public static void init() {
        PunctualTimeSlot p1 = new PunctualTimeSlot(1, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        e1 = new ExceptionalUnavailability("test", p1);
    }

    @Test
    public void testHashCode() {
        int hash1 = e1.hashCode();
        int hash2 = e1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        ExceptionalUnavailability e2 = new ExceptionalUnavailability(e1);
        int hash3 = e2.hashCode();
        assertEquals(hash2, hash3, "A copied object must have the same hash.");

        e2.setReason("The last test");
        int hash4 = e2.hashCode();
        assertNotEquals(hash3, hash4, "One attribute other than the ID has changed.");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, e1, "The second object is null.");
        assertEquals(e1, e1, "The second object is the same as the first one.");

        ExceptionalUnavailability e2 = new ExceptionalUnavailability(e1);
        assertEquals(e1, e2, "The second object is a copy of the first one.");

        e2.setReason("The last test");
        assertNotEquals(e2, e1, "The second object has one of its attributes other than its id changed.");
    }
}