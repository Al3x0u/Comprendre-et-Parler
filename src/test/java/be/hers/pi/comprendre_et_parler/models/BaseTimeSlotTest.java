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

        b1.setDay(DayOfWeek.SUNDAY);
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

        b1.setDay(DayOfWeek.SUNDAY);
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
        assertFalse(b1.getDay() == b2.getDay(), "Changes made to the copy can't impact the original.");
    }

    @Test
    public void testEquals() {
        assertFalse(b1.equals(null), "The second object is null.");
        assertTrue(b1.equals(b1), "The second object is the same as the first one.");

        BaseTimeSlot b2 = new BaseTimeSlot(b1);
        assertTrue(b1.equals(b2), "The second object is a copy of the first one.");

        b2.setDay(DayOfWeek.THURSDAY);
        assertTrue(b2.equals(b1), "The second object has its id changed.");

        b2.setEndTime(LocalTime.MIDNIGHT.minusHours(1));
        assertFalse(b2.equals(b1), "The second object has one of its attributes other than its id changed.");
    }
}