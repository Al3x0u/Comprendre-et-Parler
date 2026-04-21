package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionalUnavailabilityTest {
    private static ExceptionalUnavailability e1;
    private static PunctualTimeSlot p1;
    private static Interpreter i1;

    @BeforeAll
    public static void init() {
        p1 = new PunctualTimeSlot(1, LocalTime.NOON, LocalTime.NOON.plusHours(1), LocalDate.now(), LocalDate.now());
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
    public void testGetTimeSlot() {
        PunctualTimeSlot p2 = e1.getTimeSlot();

        p2.setEndTime(LocalTime.NOON.plusHours(5));
        assertNotEquals(e1.getTimeSlot(), p2, "The original object has to remain itself.");
    }

    @Test
    public void testSetTimeSlot() {
        PunctualTimeSlot p2 = new PunctualTimeSlot(4, LocalTime.NOON.minusHours(1), LocalTime.NOON, LocalDate.now().minusDays(7), LocalTime.now());
        e1.setTimeSlot(p2);
        assertEquals(e1.getTimeSlot(), p2, "Must have copied the object.");

        p2.setStartTime(LocalTime.NOON.minusHours(4));
        assertNotEquals(e1.getTimeSlot(), p2, "Modifications effected on the obtained object cannot change the original object.");
    }

    @Test
    public void testGetInterpreter() {
        Interpreter i3 = e1.getInterpreter();

        i3.setFirstName("Autre test");
        assertNotEquals(i3, e1.getInterpreter(), "The original object has to remain itself.");
    }

    @Test
    public void testSetInterpreter() {
        Interpreter i2 = new Interpreter(10, 80, 2, "2", "test", "test", LocalDate.now(), "65874", "test@gmail.com", "123/45.67.89", new Transportation(2, "test"));
        e1.setInterpreter(i2);
        assertEquals(e1.getInterpreter(), i2, "Must have copied the object.");

        i2.setFirstName("Autre test");
        assertNotEquals(e1.getInterpreter(), i2, "Modifications effected on the obtained object cannot change the original object.");
    }

    @Test
    public void testClone() {
        ExceptionalUnavailability e2 = e1.clone();
        assertTrue(e1.equals(e2), "The second object is a copy of the first one.");
        e2.setId(30);
        assertFalse(e1.getId() == e2.getId(), "Changes made to the copy can't impact the original.");
    }

    @Test
    public void testEquals() {
        assertFalse(e1.equals(null), "The second object is null.");
        assertTrue(e1.equals(e1), "The second object is the same as the first one.");

        ExceptionalUnavailability e2 = new ExceptionalUnavailability(e1);
        assertTrue(e1.equals(e2), "The second object is a copy of the first one.");

        e1.setId(20);
        assertTrue(e2.equals(e1), "The second object has its id changed.");

        e2.setReason("Dernier test");
        assertFalse(e2.equals(e1), "The second object has one of its attributes other than its id changed.");
    }
}