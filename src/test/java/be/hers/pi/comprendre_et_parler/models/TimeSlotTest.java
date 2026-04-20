package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeSlotTest {
    private static TimeSlot t1;
    private static LocalTime eight;
    private static LocalTime eleven;
    private static LocalTime thirteen;
    private static LocalTime sixteen;

    @BeforeAll
    public static void init() {
        eight = LocalTime.NOON.minusHours(4);
        eleven = LocalTime.NOON.minusHours(1);
        thirteen = LocalTime.NOON.plusHours(1);
        sixteen = LocalTime.NOON.plusHours(4);
        t1 = new BaseTimeSlot(1, eleven, thirteen, DayOfWeek.SUNDAY);
    }

    @Test
    public void testSetId() {
        t1.setId(-1);
        assertEquals(1, t1.getId(), "id cannot be negative.");
        t1.setId(2);
        assertEquals(2, t1.getId(), "id has to change.");
    }

    @Test
    public void testSetStartTime() {
    }

    @Test
    public void testSetEndTime() {
    }

    @Test
    public void testOverlapsCompletely() {

    }

    @Test
    public void testOverlaps() {

    }

    @Test
    public void testClone() {

    }

    @Test
    public void testEquals() {
        assertFalse(t1.equals(null), "The second object is null.");
        assertTrue(t1.equals(t1), "The second object is the same as the first one.");

        BaseTimeSlot t2 = new BaseTimeSlot(t1);
        assertTrue(t1.equals(t2), "The second object is a copy of the first one.");

        t1.setId(20);
        assertTrue(t2.equals(t1), "The second object has its id changed.");

        t2.setEndTime(LocalTime.MIDNIGHT.minusHours(1));
        assertFalse(t2.equals(t1), "The second object has one of its attributes other than its id changed.");
    }
}