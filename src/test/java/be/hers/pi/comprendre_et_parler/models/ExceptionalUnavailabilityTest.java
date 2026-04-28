package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionalUnavailabilityTest {
    private static ExceptionalUnavailability e1;
    private static PunctualTimeSlot p1;
    private static Interpreter i1;

    @BeforeAll
    public static void init() {
        p1 = new PunctualTimeSlot(1, LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        i1 = new Interpreter(20, 30, 1, "1", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Transportation(1, "test"));
        e1 = new ExceptionalUnavailability(1, "test", p1, i1);
    }

    @Test
    public void testSetId() {
        e1.setId(-1);
        assertEquals(1, e1.getId(), "id cannot be negative.");
        e1.setId(2);
        assertEquals(2, e1.getId(), "id has to change.");
    }

    @Test
    public void testClone() {
        ExceptionalUnavailability e2 = e1.clone();
        assertEquals(e1, e2, "The second object is a copy of the first one.");
        e2.setId(30);
        assertNotEquals(e1.getId(), e2.getId(), "Changes made to the copy can't impact the original.");
    }

    @Test
    public void testHashCode() {
        int hash1 = e1.hashCode();
        int hash2 = e1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        ExceptionalUnavailability e2 = new ExceptionalUnavailability(e1);
        int hash3 = e2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        e2.setId(50);
        int hash4 = e2.hashCode();
        assertEquals(hash1, hash4, "IDs are different but must not impact the hash.");

        e2.setReason("The last test");
        int hash5 = e2.hashCode();
        assertNotEquals(hash4, hash5, "One attribute other than the ID has changed.");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, e1, "The second object is null.");
        assertEquals(e1, e1, "The second object is the same as the first one.");

        ExceptionalUnavailability e2 = new ExceptionalUnavailability(e1);
        assertEquals(e1, e2, "The second object is a copy of the first one.");

        e1.setId(20);
        assertEquals(e2, e1, "The second object has its id changed.");

        e2.setReason("The last test");
        assertNotEquals(e2, e1, "The second object has one of its attributes other than its id changed.");
    }
}