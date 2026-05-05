package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PunctualTimeSlotTest {
    private PunctualTimeSlot p1;
    private final LocalDateTime today = LocalDateTime.now().withNano(0).withSecond(0);

    @BeforeEach
    public void init() {
        p1 = new PunctualTimeSlot(1, today, today.plusHours(5));
    }

    @Test
    public void testSetStartDate() {
        p1.setStartDate(today.plusDays(1));
        assertEquals(today, p1.getStartDate(),"startDate cannot be after endDate.");
        p1.setStartDate(today.minusDays(10));
        assertEquals(today.minusDays(10), p1.getStartDate(), "startDate has to change.");
    }

    @Test
    public void testSetEndDate() {
        p1.setEndDate(today.minusDays(1));
        assertEquals(today.plusHours(5), p1.getEndDate(), "endDate cannot be before startDate.");
        p1.setEndDate(today.plusDays(5));
        assertEquals(today.plusDays(5), p1.getEndDate(), "endDate has to change.");
    }

    @Test
    public void testOverlaps() {
        assertTrue(p1.overlaps(p1), "The second object is the same as the first one.");
        assertFalse(p1.overlaps(null), "The second object is null.");

        PunctualTimeSlot p2 = new PunctualTimeSlot(2, today.minusDays(2), today.minusDays(1));
        assertFalse(p1.overlaps(p2), "The two objects have different dates.");

        p2.setEndDate(today.plusDays(3));
        assertTrue(p1.overlaps(p2), "The second object completely overlaps the first one.");
        assertTrue(p2.overlaps(p1), "The first object completely overlaps the second one.");

        p2.setEndDate(p1.getEndDate().plusMinutes(25));
        p2.setStartDate(p1.getStartDate().plusHours(6));
        assertTrue(p1.overlaps(p2), "The two dates overlap partially.");
        assertTrue(p2.overlaps(p1), "The two dates overlap partially.");

        p2.setStartDate(p1.getEndDate());
        assertFalse(p1.overlaps(p2), "The first object ends when the second one begins.");
        assertFalse(p2.overlaps(p1), "The second object ends when the first one begins.");
    }

    @Test
    public void testOverlapsCompletely() {
        assertTrue(p1.overlapsCompletely(p1), "The second object is the same as the first one.");
        assertFalse(p1.overlapsCompletely(null), "The second object is null.");

        PunctualTimeSlot p2 = new PunctualTimeSlot(2, today.minusDays(2), today.minusDays(1));
        assertFalse(p1.overlapsCompletely(p2), "The two objects have different dates.");

        p2.setEndDate(today.plusDays(3));
        assertTrue(p1.overlapsCompletely(p2), "The second object completely overlaps the first one.");
        assertTrue(p2.overlapsCompletely(p1), "The first object completely overlaps the second one.");

        p2.setEndDate(p1.getEndDate().plusMinutes(25));
        p2.setStartDate(p1.getStartDate().plusHours(1));
        assertFalse(p1.overlapsCompletely(p2), "The two dates overlap partially.");
        assertFalse(p2.overlapsCompletely(p1), "The two dates overlap partially.");

        p2.setStartDate(p1.getEndDate());
        assertFalse(p1.overlapsCompletely(p2), "The first object ends when the second one begins.");
        assertFalse(p2.overlapsCompletely(p1), "The second object ends when the first one begins.");
    }

    @Test
    public void testClone() {
        PunctualTimeSlot p2 = p1.clone();
        assertEquals(p1, p2, "The second object is a copy of the first one.");
        p2.setStartDate(today.minusMonths(2));
        assertNotSame(p1.getStartDate(), p2.getStartDate(), "Changes made to the copy can't impact the original.");
    }

    @Test
    public void testHashCode() {
        int hash1 = p1.hashCode();
        int hash2 = p1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        PunctualTimeSlot p2 = new PunctualTimeSlot(p1);
        int hash3 = p2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        p2.setId(50);
        int hash4 = p2.hashCode();
        assertEquals(hash1, hash4, "IDs are different but must not impact the hash.");

        p2.setStartDate(today.minusYears(1));
        int hash5 = p2.hashCode();
        assertNotEquals(hash4, hash5, "One attribute other than the ID has changed.");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, p1, "The second object is null.");
        assertEquals(p1, p1, "The second object is the same as the first one.");

        PunctualTimeSlot p2 = new PunctualTimeSlot(p1);
        assertEquals(p1, p2, "The second object is a copy of the first one.");

        p2.setId(20);
        assertEquals(p1, p2, "The second object has its id changed.");

        p2.setEndDate(today.plusYears(1));
        assertNotEquals(p2, p1, "The second object has one of its attributes other than its id changed.");
    }
}