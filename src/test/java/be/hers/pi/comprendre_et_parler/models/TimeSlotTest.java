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
        assertFalse(t1.overlaps(null), "The second object is null.");

        TimeSlot t2 = t1.clone();
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
        assertTrue(t1.overlapsCompletely(t1), "The second object is the same as the first one.");
        assertFalse(t1.overlapsCompletely(null), "The second object is null.");

        TimeSlot t2 = t1.clone();
        assertTrue(t2.overlapsCompletely(t1), "The second object is a copy of the first one.");

        t1.setEndTime(sixteen);
        t2.setStartTime(eight);
        assertFalse(t2.overlapsCompletely(t1), "The first object is partially in the second one.");
        assertFalse(t1.overlapsCompletely(t2), "The second object is partially in the first one.");

        t1.setStartTime(thirteen);
        t2.setEndTime(eleven);
        assertFalse(t2.overlapsCompletely(t1), "The two objects are totally outside each other.");

        t2.setEndTime(thirteen);
        t2.setStartTime(eleven);
        t1.setStartTime(eight);
        assertTrue(t2.overlapsCompletely(t1), "The first object is totally in the second one.");
        assertTrue(t1.overlapsCompletely(t2), "The second object is totally in the first one.");

        t1.setStartTime(thirteen);
        assertFalse(t2.overlapsCompletely(t1), "The second object is just after the first one.");
        assertFalse(t1.overlapsCompletely(t2), "The first object is just after the second one.");
    }

    @Test
    public void testClone() {
        TimeSlot t2 = t1.clone();
        assertTrue(t1.equals(t2), "The second object is a copy of the first one.");
        t2.setId(50);
        assertNotEquals(t1.getId(), t2.getId(), "Changes made to the copy can't impact the original.");
    }

    @Test
    public void testHashCode() {
        int hash1 = t1.hashCode();
        int hash2 = t1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        TimeSlot t2 = t1.clone();
        int hash3 = t2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        t2.setId(50);
        int hash4 = t2.hashCode();
        assertEquals(hash1, hash4, "IDs are different but must not impact the hash.");

        t2.setEndTime(sixteen.plusHours(2));
        int hash5 = t2.hashCode();
        assertNotEquals(hash4, hash5, "One attribute other than the ID has changed.");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, t1, "The second object is null.");
        assertEquals(t1, t1, "The second object is the same as the first one.");

        TimeSlot t2 = t1.clone();
        assertEquals(t1, t2, "The second object is a copy of the first one.");

        t1.setId(20);
        assertEquals(t2, t1, "The second object has its id changed.");

        t2.setEndTime(LocalTime.MIDNIGHT.minusHours(1));
        assertNotEquals(t2, t1, "The second object has one of its attributes other than its id changed.");
    }
}