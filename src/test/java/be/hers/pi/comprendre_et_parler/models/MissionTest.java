package be.hers.pi.comprendre_et_parler.models;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MissionTest {
    private static List<Interpreter> interpreters;
    private static Beneficiary b1;
    private static Interpreter i1;
    private static Mission m1;

    @BeforeAll
    public static void init() {
        i1 = new Interpreter(20, 30, 1, "1", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Transportation(1, "test"));
        b1 = new Beneficiary(2, "2", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), i1);
        interpreters.add(i1);
        m1 = new Mission(1,
                "test",
                MissionState.PENDING,
                b1,
                interpreters,
                new Location(1, "test", new City(1, "Libramont", 6800), "test", "test", 15),
                new JobSkill(2, "test"),
                new AcademicSkill(1, "test"),
                "test",
                "test");
    }

    @Test
    public void testSetId() {
        m1.setId(-1);
        assertEquals(1, m1.getId(), "id cannot be negative.");
        m1.setId(2);
        assertEquals(2, m1.getId(), "id has to change.");
    }

    @Test
    public void testSetBeneficiary() {
        Beneficiary b2 = new Beneficiary(3, "3", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), null);
        m1.setBeneficiary(b2);
        assertEquals(m1.getBeneficiary(), b2, "Must have copied the object.");

        b2.setFirstName("Autre test");
        assertNotEquals(m1.getBeneficiary(), b2, "Modifications effected on the obtained object cannot change the original object.");
    }

    @Test
    public void testGetBeneficiary() {
        Beneficiary b3 = m1.getBeneficiary();

        b3.setFirstName("Autre test");
        assertNotEquals(b3, m1.getBeneficiary(), "The original object has to remain itself.");
    }

    @Test
    public void testSetInterpreters() {
        List<Interpreter> i2 = new ArrayList<Interpreter>();
        i2.add(new Interpreter(74, 105, 7, "7", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null));
        m1.setInterpreters(i2);
        assertEquals(m1.getInterpreters(), i2, "Must have copied the object.");

        Interpreter i3 = new Interpreter(74, 105, 8, "8", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null);
        i2.add(i3);
        assertNotEquals(m1.getInterpreters(), i2, "Modifications effected on the obtained object cannot change the original object.");

        i3.setLogin("9");
        i2.add(i3);
        assertThrows(AlreadyExistsException.class, () -> {
            m1.setInterpreters(i2);
        }, "The List cannot have two objects with the same id.");

        i2.remove(i3);
        i3.setLogin("8");
        i3.setId(20);
        i2.add(i3);
        assertThrows(AlreadyExistsException.class, () -> {
            m1.setInterpreters(i2);
        }, "The List cannot have two equal objects.");
    }

    @Test
    public void testGetInterpreters() {
        List<Interpreter> i3 = m1.getInterpreters();
        assertEquals(i3, m1.getInterpreters(), "Must obtain an exact copy of the object.");

        i3.add(new Interpreter(74, 105, 9, "9", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null));
        assertNotEquals(m1.getInterpreters(), i3, "The original object has to remain itself.");
    }

    @Test
    public void testAddInterpreter() {
        assertThrows(NullPointerException.class, () -> {
            m1.addInterpreter(null);
        }, "An exception must occur when the object is null.");

        Interpreter i4 = new Interpreter(74, 105, 10, "10", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null);
        m1.addInterpreter(i4);
        assertTrue(m1.getInterpreters().contains(i4), "The object is added to the list.");

        assertThrows(AlreadyExistsException.class, () -> {
            m1.addInterpreter(i4);
        }, "The object has already been added to the list.");
    }

    @Test
    public void testDeleteInterpreter() {
        assertDoesNotThrow(() -> {
            m1.deleteInterpreter(null);
        }, "No exception should occur when the object is null.");

        Interpreter i5 = new Interpreter(74, 105, 11, "11", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null);
        m1.addInterpreter(i5);
        assertDoesNotThrow(() -> {
            m1.deleteInterpreter(11);
        }, "Delete an object from the list.");
        assertFalse(m1.getInterpreters().contains(i5), "The object has been deleted from the list.");

        assertThrows(NoSuchElementException.class, () -> {
            m1.deleteInterpreter(11);
        }, "The object is not present in the list.");
    }

    @Test
    public void testEquals() {
        assertFalse(m1.equals(null), "The second object is null.");
        assertTrue(m1.equals(m1), "The second object is the same as the first one.");

        Mission m2 = new Mission(m1);
        assertTrue(m1.equals(m2), "The second object is a copy of the first one.");

        m1.setId(20);
        assertTrue(m2.equals(m1), "The second object has its id changed.");

        m2.setCommentary("Dernier test");
        assertFalse(m2.equals(m1), "The second object has one of its attributes other than its id changed.");
    }
}