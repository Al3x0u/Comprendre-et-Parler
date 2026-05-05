package be.hers.pi.comprendre_et_parler.models;

import be.hers.pi.comprendre_et_parler.DAOs.DatabaseConnector;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BaseTimeSlotTest {
    private BaseTimeSlot b1;
    private static final LocalTime eight = LocalTime.NOON.minusHours(4);
    private static final LocalTime eleven = LocalTime.NOON.minusHours(1);
    private static final LocalTime thirteen = LocalTime.NOON.plusHours(1);
    private static final LocalTime sixteen = LocalTime.NOON.plusHours(4);
    private static final LocalDate today = LocalDate.now();

    @BeforeEach
    public void init() {
        b1 = new BaseTimeSlot(1, today, today.plusDays(1), eleven, thirteen, DayOfWeek.SUNDAY);
    }

    @Test
    public void testSetStartTime() {
        b1.setStartTime(sixteen);
        assertEquals(eleven, b1.getStartTime(), "startTime cannot be after endTime.");
        b1.setStartTime(eight);
        assertEquals(eight, b1.getStartTime(), "startTime has to change.");
    }

    @Test
    public void testSetEndTime() {
        b1.setEndTime(eight);
        assertEquals(thirteen, b1.getEndTime(), "endTime cannot be before startTime.");
        b1.setEndTime(sixteen);
        assertEquals(sixteen, b1.getEndTime(), "endTime has to change.");
    }

    @Test
    public void testSetStartDate() {
        b1.setStartDate(today.plusDays(10));
        assertEquals(today, b1.getStartDate(), "startDate cannot be after endDate.");
        b1.setStartDate(today.minusDays(2));
        assertEquals(today.minusDays(2), b1.getStartDate(), "startDate has to change.");
        b1.setStartDate(b1.getEndDate());
        assertEquals(b1.getEndDate(), b1.getStartDate(), "startDate can be the same day as endDate.");
    }

    @Test
    public void testSetEndDate() {
        b1.setEndDate(today.minusWeeks(1));
        assertEquals(today.plusDays(1), b1.getEndDate(), "endDate cannot be before startDate.");
        b1.setEndDate(today.plusMonths(1));
        assertEquals(today.plusMonths(1), b1.getEndDate(), "endDate has to change.");
        b1.setEndDate(b1.getStartDate());
        assertEquals(b1.getStartDate(), b1.getEndDate(), "endDate can be the same day as startDate.");
    }

    @Test
    public void testOverlaps() {
        assertTrue(b1.overlaps(b1), "The second object is the same as the first one.");
        assertFalse(b1.overlaps(null), "The second object is null.");

        BaseTimeSlot b2 = new BaseTimeSlot(2, today, today.plusDays(1), eight, sixteen, DayOfWeek.WEDNESDAY);
        assertFalse(b1.overlaps(b2), "The second object completely overlaps the first one, but they are not on the same day.");

        b1.setDay(b2.getDay());
        assertTrue(b1.overlaps(b2), "The second object completely overlaps the first one, and they are on the same day.");
        assertTrue(b2.overlaps(b1), "The first object completely overlaps the second one, and they are on the same day.");

        b1.setStartDate(today.minusYears(1));
        b1.setEndDate(today.minusMonths(1));
        assertTrue(b1.overlaps(b2), "startDate and endDate have no influence on the result.");

        b2.setStartTime(LocalTime.MIDNIGHT.plusHours(1));
        b2.setEndTime(LocalTime.MIDNIGHT.plusHours(2));
        assertFalse(b1.overlaps(b2), "The two objects are on the same day but don't overlap.");

        b2.setEndTime(sixteen);
        assertTrue(b1.overlaps(b2), "The two objects are on the same day and overlap partially.");
        assertTrue(b2.overlaps(b1), "The two objects are on the same day and overlap partially.");

        b2.setStartTime(b1.getEndTime());
        assertFalse(b1.overlaps(b2), "The first object ends when the second one begins.");
        assertFalse(b2.overlaps(b1), "The second object ends when the first one begins.");
    }

    @Test
    public void testOverlapsCompletely() {
        assertTrue(b1.overlapsCompletely(b1), "The second object is the same as the first one.");
        assertFalse(b1.overlapsCompletely(null), "The second object is null.");

        BaseTimeSlot b2 = new BaseTimeSlot(3, today, today.plusDays(1), eight, sixteen, DayOfWeek.WEDNESDAY);
        assertFalse(b1.overlapsCompletely(b2), "The second object completely overlaps the first one, but they are not on the same day.");

        b1.setDay(b2.getDay());
        assertTrue(b1.overlapsCompletely(b2), "The second object completely overlaps the first one, and they are on the same day.");
        assertTrue(b2.overlapsCompletely(b1), "The first object completely overlaps the second one, and they are on the same day.");

        b1.setStartDate(today.minusYears(1));
        b1.setEndDate(today.minusMonths(1));
        assertTrue(b1.overlapsCompletely(b2), "startDate and endDate have no influence on the result.");

        b2.setStartTime(LocalTime.MIDNIGHT.plusHours(1));
        b2.setEndTime(LocalTime.MIDNIGHT.plusHours(2));
        assertFalse(b1.overlapsCompletely(b2), "The two objects are on the same day but don't overlap.");

        b2.setEndTime(LocalTime.NOON);
        assertFalse(b1.overlapsCompletely(b2), "The two objects are on the same day but overlap partially.");
        assertFalse(b2.overlapsCompletely(b1), "The two objects are on the same day but overlap partially.");

        b2.setEndTime(sixteen);
        b2.setStartTime(b1.getEndTime());
        assertFalse(b1.overlaps(b2), "The first object ends when the second one begins.");
        assertFalse(b2.overlaps(b1), "The second object ends when the first one begins.");
    }

    @Test
    public void testClone() {
        BaseTimeSlot b2 = b1.clone();
        assertEquals(b1, b2, "The second object is a copy of the first one.");
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

        b2.setId(20);
        assertEquals(b1, b2, "The second object has its id changed.");

        b2.setDay(DayOfWeek.SATURDAY);
        assertNotEquals(b2, b1, "The second object has one of its attributes other than its id changed.");
    }
}