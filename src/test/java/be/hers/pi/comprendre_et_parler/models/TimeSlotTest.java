package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeSlotTest {
    private TimeSlot t1;
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
    public void initTimeSLot() {
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
        t1.setStartTime(sixteen);
        assertEquals(eleven, t1.getStartTime(), "startTime cannot be after endTime.");
        t1.setStartTime(eight);
        assertEquals(eight, t1.getStartTime(), "startTime has to change.");
    }

    @Test
    public void testSetEndTime() {
        t1.setEndTime(eight);
        assertEquals(thirteen, t1.getEndTime(), "endTime cannot be before startTime.");
        t1.setEndTime(sixteen);
        assertEquals(sixteen, t1.getEndTime(), "endTime has to change.");
    }

    @Test
    public void testOverlaps() {
        assertTrue(t1.overlaps(t1), "The second object is the same as the first one.");
        assertTrue(t1.overlaps(null), "The second object is null.");

        TimeSlot t2 = new BaseTimeSlot(t1);
        assertTrue(t2.overlaps(t1), "The second object is a copy of the first one.");

        t1.setEndTime(sixteen);
        t2.setStartTime(eight);
        assertTrue(t2.overlaps(t1), "The first object is partially in the second one.");
        assertTrue(t1.overlaps(t2), "The second object is partially in the first one.");

        t1.setStartTime(thirteen);
        t2.setEndTime(eleven);
        assertFalse(t2.overlaps(t1), "The two objects are totally outside each other.");

        t2.setEndTime(thirteen);
        t2.setStartTime(eleven);
        t1.setStartTime(eight);
        assertTrue(t2.overlaps(t1), "The first object is totally in the second one.");
        assertTrue(t1.overlaps(t2), "The second object is totally in the first one.");

        t1.setStartTime(thirteen);
        assertFalse(t2.overlaps(t1), "The second object is just after the first one.");
        assertFalse(t1.overlaps(t2), "The first object is just after the second one.");
    }

    @Test
    public void testOverlapsCompletely() {

    }

    @Test
    public void testClone() {
        TimeSlot t2 = t1.clone();
        assertTrue(t1.equals(t2), "The second object is a copy of the first one.");
        t2.setId(50);
        assertFalse(t1.getId() == t2.getId(), "Changes made to the copy can't impact the original.");
    }

    @Test
    public void testEquals() {
        assertFalse(t1.equals(null), "The second object is null.");
        assertTrue(t1.equals(t1), "The second object is the same as the first one.");

        TimeSlot t2 = new BaseTimeSlot(t1);
        assertTrue(t1.equals(t2), "The second object is a copy of the first one.");

        t1.setId(20);
        assertTrue(t2.equals(t1), "The second object has its id changed.");

        t2.setEndTime(LocalTime.MIDNIGHT.minusHours(1));
        assertFalse(t2.equals(t1), "The second object has one of its attributes other than its id changed.");
    }
}