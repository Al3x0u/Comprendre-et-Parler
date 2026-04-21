package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class PunctualTimeSlotTest {
    private PunctualTimeSlot p1;
    private LocalDate today = LocalDate.now();
    private static LocalTime eight;
    private static LocalTime eleven;
    private static LocalTime thirteen;
    private static LocalTime sixteen;

    @BeforeAll
    public static void initTime() {
        eight = LocalTime.NOON.minusHours(4);
        eleven = LocalTime.NOON.minusHours(1);
        thirteen = LocalTime.NOON.plusHours(1);
        sixteen = LocalTime.NOON.plusHours(4);
    }

    @BeforeEach
    public void init() {
        p1 = new PunctualTimeSlot(1, eleven, thirteen, today, today);
    }

    @Test
    public void testSetStartTime() {
        p1.setStartTime(sixteen);
        assertEquals(eleven, p1.getStartTime(), "startTime cannot be after endTime when the dates are the sames.");
        p1.setStartTime(eight);
        assertEquals(eight, p1.getStartTime(), "startTime has to change.");

        p1.setEndDate(today.minusWeeks(1));
        p1.setStartTime(sixteen);
        assertEquals(sixteen, p1.getStartTime(), "startTime can be after endTime when the dates are not the sames.");
    }

    @Test
    public void testSetEndTime() {
        p1.setEndTime(eight);
        assertEquals(thirteen, p1.getEndTime(), "endTime cannot be before startTime when the dates are the sames.");
        p1.setEndTime(sixteen);
        assertEquals(sixteen, p1.getEndTime(), "endTime has to change.");

        p1.setEndDate(today.plusWeeks(1));
        p1.setEndTime(eight);
        assertEquals(eight, p1.getEndTime(), "endTime can be before startTime when the dates are not the sames.");
    }

    @Test
    public void testSetStartDate() {
        p1.setStartDate(today.plusDays(1));
        assertEquals(today, p1.getStartDate(), "startDate cannot be after endDate.");
        p1.setStartDate(today.minusDays(1));
        assertNotEquals(today, p1.getStartDate(), "startDate has to change.");
    }

    @Test
    public void testSetEndDate() {
        p1.setEndDate(today.minusDays(1));
        assertEquals(today, p1.getEndDate(), "endDate cannot be before startDate.");
        p1.setEndDate(today.plusDays(1));
        assertNotEquals(today, p1.getEndDate(), "endDate has to change.");
    }

    @Test
    public void testOverlaps() {
        assertTrue(p1.overlaps(p1), "The second object is the same as the first one.");
        assertFalse(p1.overlaps(null), "The second object is null.");

        PunctualTimeSlot p2 = new PunctualTimeSlot(2, LocalTime.NOON.minusHours(1), LocalTime.NOON.plusHours(1), today.minusDays(2), today.minusDays(1));
        assertFalse(p1.overlaps(p2), "The two objects have different dates.");

        p2.setEndDate(today.plusDays(3));
        assertTrue(p1.overlaps(p2), "The second object overlaps the first one, and the time overlaps too.");
        assertTrue(p2.overlaps(p1), "The first object overlaps the second one, and the time overlaps too.");

        p2.setStartTime(LocalTime.MIDNIGHT.plusHours(1));
        p2.setEndTime(LocalTime.MIDNIGHT.plusHours(2));
        assertTrue(p1.overlaps(p2), "The second object overlaps the first one, and the time don't overlaps.");
        assertTrue(p2.overlaps(p1), "The first object overlaps the second one, and the time don't overlaps.");

        p2.setEndDate(today);
        assertFalse(p1.overlaps(p2), "The second object ends when the first one begins, and the times don't overlap.");
        assertFalse(p1.overlaps(p2), "The first object ends when the second one begins, and the times don't overlap.");

        p2.setEndTime(LocalTime.NOON.plusMinutes(30));
        assertTrue(p1.overlaps(p2), "The second object ends when the first one begins, and the times overlaps.");
        assertTrue(p1.overlaps(p2), "The first object ends when the second one begins, and the times overlaps.");
    }

    @Test
    public void testOverlapsCompletely() {
        assertTrue(p1.overlapsCompletely(p1), "The second object is the same as the first one.");
        assertFalse(p1.overlapsCompletely(null), "The second object is null.");

        PunctualTimeSlot p2 = new PunctualTimeSlot(2, LocalTime.NOON.minusHours(1), LocalTime.NOON.plusHours(1), today.minusDays(2), today.minusDays(1));
        assertFalse(p1.overlapsCompletely(p2), "The two objects have different dates.");

        p2.setEndDate(today.plusDays(3));
        assertTrue(p1.overlapsCompletely(p2), "The second object overlaps the first one, and the time overlaps too.");
        assertTrue(p2.overlapsCompletely(p1), "The first object overlaps the second one, and the time overlaps too.");

        p2.setStartTime(LocalTime.MIDNIGHT.plusHours(1));
        p2.setEndTime(LocalTime.MIDNIGHT.plusHours(2));
        assertTrue(p1.overlapsCompletely(p2), "The second object overlaps the first one, and the time don't overlaps completely.");
        assertTrue(p2.overlapsCompletely(p1), "The first object overlaps the second one, and the time don't overlaps completely.");

        p2.setEndDate(today);
        assertFalse(p1.overlapsCompletely(p2), "The second object ends when the first one begins, and the times don't overlap completely.");
        assertFalse(p1.overlapsCompletely(p2), "The first object ends when the second one begins, and the times don't overlap completely.");

        p2.setEndTime(LocalTime.NOON.plusMinutes(30));
        assertFalse(p1.overlapsCompletely(p2), "The second object ends when the first one begins, and the times don't overlaps completely.");
        assertFalse(p1.overlapsCompletely(p2), "The first object ends when the second one begins, and the times don't overlaps completely.");

        p2.setEndTime(LocalTime.NOON.plusHours(4));
        assertTrue(p1.overlapsCompletely(p2), "The second object ends when the first one begins, and the times overlaps completely.");
        assertTrue(p1.overlapsCompletely(p2), "The first object ends when the second one begins, and the times overlaps completely.");
    }

    @Test
    public void testClone() {
        PunctualTimeSlot b2 = p1.clone();
        assertTrue(p1.equals(b2), "The second object is a copy of the first one.");
        b2.setStartDate(today.plusMonths(2));
        assertFalse(p1.getStartDate() == b2.getStartDate(), "Changes made to the copy can't impact the original.");
    }

    @Test
    public void testEquals() {
        assertFalse(p1.equals(null), "The second object is null.");
        assertTrue(p1.equals(p1), "The second object is the same as the first one.");

        PunctualTimeSlot p2 = new PunctualTimeSlot(p1);
        assertTrue(p1.equals(p2), "The second object is a copy of the first one.");

        p2.setEndDate(today.plusYears(1));
        assertFalse(p2.equals(p1), "The second object has one of its attributes other than its id changed.");
    }
}