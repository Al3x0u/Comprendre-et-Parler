package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class BaseTimeSlotTest {
    private static BaseTimeSlot b1;

    @BeforeAll
    public static void initTime() {
        b1 = new BaseTimeSlot(1, LocalTime.NOON, LocalTime.NOON.plusHours(1), DayOfWeek.SUNDAY);
    }

    @Test
    public void testOverlaps() {
        assertTrue(b1.overlaps(b1), "The second object is the same as the first one.");
        assertFalse(b1.overlaps(null), "The second object is null.");

        BaseTimeSlot b2 = new BaseTimeSlot(2, LocalTime.NOON.minusHours(1), LocalTime.NOON.plusHours(1), DayOfWeek.SUNDAY);
        assertFalse(b1.overlaps(b2), "The two objects are not on the same day.");

        b1.setDay(DayOfWeek.MONDAY);
        assertTrue(b1.overlaps(b2), "The two objects are on the same day and overlap.");

        b2.setStartTime(LocalTime.MIDNIGHT.plusHours(1));
        b2.setEndTime(LocalTime.MIDNIGHT.plusHours(2));
        assertFalse(b1.overlaps(b2), "The two objects are on the same day but don't overlap.");
    }

    @Test
    public void testOverlapsCompletely() {
        assertTrue(b1.overlapsCompletely(b1), "The second object is the same as the first one.");
        assertFalse(b1.overlapsCompletely(null), "The second object is null.");

        BaseTimeSlot b2 = new BaseTimeSlot(2, LocalTime.NOON.minusHours(1), LocalTime.NOON.plusHours(1), DayOfWeek.SUNDAY);
        assertFalse(b1.overlapsCompletely(b2), "The two objects are not on the same day.");

        b1.setDay(DayOfWeek.TUESDAY);
        assertTrue(b1.overlapsCompletely(b2), "The two objects are on the same day and overlap.");

        b2.setStartTime(LocalTime.MIDNIGHT.plusHours(1));
        b2.setEndTime(LocalTime.MIDNIGHT.plusHours(2));
        assertFalse(b1.overlapsCompletely(b2), "The two objects are on the same day but don't overlap.");
    }

    @Test
    public void testClone() {
        BaseTimeSlot b2 = b1.clone();
        assertTrue(b1.equals(b2), "The second object is a copy of the first one.");
        b2.setDay(DayOfWeek.FRIDAY);
        assertNotSame(b1.getDay(), b2.getDay(), "Changes made to the copy can't impact the original.");
    }

    @Test
    public void testHashCode() {
        int hash1 = b1.hashCode();
        int hash2 = b1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        BaseTimeSlot b2 = new BaseTimeSlot(b1);
        int hash3 = b2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        b2.setId(50);
        int hash4 = b2.hashCode();
        assertEquals(hash1, hash4, "IDs are different but must not impact the hash.");

        b2.setDay(DayOfWeek.THURSDAY);
        int hash5 = b2.hashCode();
        assertNotEquals(hash4, hash5, "One attribute other than the ID has changed.");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, b1, "The second object is null.");
        assertEquals(b1, b1, "The second object is the same as the first one.");

        BaseTimeSlot b2 = new BaseTimeSlot(b1);
        assertEquals(b1, b2, "The second object is a copy of the first one.");

        b2.setDay(DayOfWeek.SATURDAY);
        assertNotEquals(b2, b1, "The second object has one of its attributes other than its id changed.");
    }
}