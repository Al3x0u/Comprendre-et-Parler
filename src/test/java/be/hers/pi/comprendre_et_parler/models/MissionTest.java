package be.hers.pi.comprendre_et_parler.models;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MissionTest {
    private static Map<Beneficiary, Integer> beneficiaries;
    private static List<Interpreter> interpreters;
    private static Beneficiary b1;
    private static Interpreter i1;
    private static Mission m1;

    @BeforeAll
    public static void init() {
        i1 = new Interpreter(20, 30, 1, "1", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Transportation(1, "test"));
        b1 = new Beneficiary(2, "2", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), i1);
        beneficiaries.put(b1, 4);
        interpreters.add(i1);
        m1 = new Mission(1,
                "test",
                MissionState.PENDING,
                beneficiaries,
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
    public void testSetBeneficiaries() {
        assertThrows(NullPointerException.class, () -> {
            m1.setBeneficiaries(null);
        });

        Map<Beneficiary, Integer> b2 = new HashMap<Beneficiary, Integer>();
        b2.put(new Beneficiary(3, "3", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), null), 1);
        m1.setBeneficiaries(b2);
        assertEquals(m1.getBeneficiaries(), b2);

        Beneficiary b3 = new Beneficiary(4, "4", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), null);
        b2.put(b3, 0);
        assertNotEquals(m1.getBeneficiaries(), b2);

        b3.setLogin("8");
        b2.put(b3, 0);
        assertThrows(AlreadyExistsException.class, () -> {
            m1.setBeneficiaries(b2);
        });

        b2.remove(b3);
        b3.setLogin("4");
        b3.setId(3);
        b2.put(b3, 2);
        assertThrows(AlreadyExistsException.class, () -> {
            m1.setBeneficiaries(b2);
        });
    }

    @Test
    public void testGetBeneficiaries() {
        List<Beneficiary> b3 = m1.getBeneficiaries();
        b3.add(new Beneficiary(5, "5", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), null));
        assertNotEquals(b3, m1.getBeneficiaries(), "The original object has to remain itself.");
    }

    @Test
    public void testSetInterpreters() {
        assertThrows(NullPointerException.class, () -> {
            m1.setInterpreters(null);
        });

        List<Interpreter> i2 = new ArrayList<Interpreter>();
        i2.add(new Interpreter(74, 105, 7, "7", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null));
        m1.setInterpreters(i2);
        assertEquals(m1.getInterpreters(), i2);

        Interpreter i3 = new Interpreter(74, 105, 8, "8", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null);
        i2.add(i3);
        assertNotEquals(m1.getInterpreters(), i2);

        i3.setLogin("9");
        i2.add(i3);
        assertThrows(AlreadyExistsException.class, () -> {
            m1.setInterpreters(i2);
        });

        i2.remove(i3);
        i3.setLogin("8");
        i3.setId(20);
        i2.add(i3);
        assertThrows(AlreadyExistsException.class, () -> {
            m1.setInterpreters(i2);
        });
    }

    @Test
    public void testGetInterpreters() {
        List<Interpreter> i3 = m1.getInterpreters();
        i3.add(new Interpreter(74, 105, 9, "9", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null));
        assertNotEquals(m1.getInterpreters(), i3, "The original object has to remain itself.");
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