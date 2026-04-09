package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class MissionTest {
    private static List<Beneficiary> beneficiaries;
    private static List<Interpreter> interpreters;
    private static Beneficiary b1;
    private static Interpreter i1;
    private static List<Beneficiary> b2;
    private static List<Interpreter> i2;
    private static Mission m1;

    @BeforeAll
    static void init() {
        i1 = new Interpreter(20, 30, 1, "1", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Transportation(1, "test"));
        b1 = new Beneficiary(2, "2", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), i1);
        beneficiaries.add(b1);
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
    void testSetId() {
        m1.setId(-1);
        assertEquals(1, m1.getId());
        m1.setId(2);
        assertEquals(2, m1.getId());
    }

    @Test
    void testSetBeneficiaries() {
        b2.add(new Beneficiary(3, "3", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), null));
        m1.setBeneficiaries(b2);
        b2.add(new Beneficiary(4, "4", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), null));
        assertNotEquals(m1.getBeneficiaries(), b2);
    }

    @Test
    void testGetBeneficiaries() {
        List<Beneficiary> b3 = m1.getBeneficiaries();
        b3.add(new Beneficiary(5, "5", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), null));
        assertNotEquals(m1.getBeneficiaries(), b3);
    }

    @Test
    void testAddBeneficiary() {
        Beneficiary b4 = new Beneficiary(6, "6", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", new Status(1, "test", 10), null));
        m1.addBeneficiary(b4);
        assertTrue(m1.getBeneficiaries().stream().anyMatch(b -> b.equals(b4)));
    }

    @Test
    void testDeleteBeneficiary() {
        m1.deleteBeneficiary(b1.getId());
        assertFalse(m1.getBeneficiaries().stream().anyMatch(b -> b.equals(b1)));
    }

    @Test
    void testSetInterpreters() {
        i2.add(new Interpreter(74, 105, 7, "7", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null));
        m1.setInterpreters(i2);
        i2.add(new Interpreter(10, 40, 8, "8", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null));
        assertNotEquals(m1.getInterpreters(), i2);
    }

    @Test
    void testGetInterpreters() {
        List<Interpreter> i3 = m1.getInterpreters();
        i3.add(new Interpreter(74, 105, 9, "9", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null));
        assertNotEquals(m1.getInterpreters(), i3);
    }

    @Test
    void testAddInterpreter() {
        Interpreter i4 = new Interpreter(74, 105, 10, "10", "test", "test", LocalDate.now(), "1234", "test@gmail.com", "123/45.67.89", null));
        m1.addInterpreter(i4);
        assertTrue(m1.getInterpreters().stream().anyMatch(i -> i.equals(i4)));
    }

    @Test
    void testDeleteInterpreter() {
        m1.deleteInterpreter(i1.getId());
        assertFalse(m1.getInterpreters().stream().anyMatch(i -> i.equals(i1)));
    }

    @Test
    void testEquals() {
        Mission m2 = new Mission(m1);
        Mission m3 =new Mission(2,
                "test",
                MissionState.PENDING,
                beneficiaries,
                interpreters,
                new Location(1, "test", new City(1, "Libramont", 6800), "test", "test", 20),
                new JobSkill(2, "test"),
                new AcademicSkill(1, "test"),
                "test",
                "test");

        Assertions.assertNotEquals(null, m1);
        Assertions.assertEquals(m1, m1);
        Assertions.assertEquals(m1, m2);
        Assertions.assertEquals(m2, m3);
    }
}