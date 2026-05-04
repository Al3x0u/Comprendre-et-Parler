package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MissionTest {
    private static Mission m1;

    @BeforeAll
    public static void init() {
        Interpreter i1 = new Interpreter(1, "1", "test", "test", LocalDate.now(),
                "1234", "test@gmail.com", "123/45.67.89", 10, 120,
                "Auto", null, null, null, null);
        Beneficiary b1 = new Beneficiary(2, "2", "test", "test", LocalDate.now(),
                "1234", "test@gmail.com", "123/45.67.89",
                new Status(1, "test", 10), i1);
        Set<TimeSlot> t1 = new HashSet<>();
        t1.add(new RegularMission(new PunctualTimeSlot(1, LocalDateTime.now(), LocalDateTime.now().plusHours(2)), MissionState.ACCEPTED));
        m1 = new Mission(1,
                "test",
                "test",
                t1,
                b1,
                new Location(1, "test", new City(1, "Libramont", 6800), "test", "test", 15),
                new JobSkill(2, "test"),
                new AcademicSkill(1, "test"),
                "test",
                0);
    }

    @Test
    public void testSetId() {
        m1.setId(-1);
        assertEquals(1, m1.getId(), "id cannot be negative.");
        m1.setId(2);
        assertEquals(2, m1.getId(), "id has to change.");
    }

    @Test
    public void testHashCode() {
        int hash1 = m1.hashCode();
        int hash2 = m1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        Mission m2 = new Mission(m1);
        int hash3 = m2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        m2.setId(50);
        int hash4 = m2.hashCode();
        assertEquals(hash1, hash4, "IDs are different but must not impact the hash.");

        m2.setCommentary("The last test");
        int hash5 = m2.hashCode();
        assertNotEquals(hash4, hash5, "One attribute other than the ID has changed.");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, m1, "The second object is null.");
        assertEquals(m1, m1, "The second object is the same as the first one.");

        Mission m2 = new Mission(m1);
        assertEquals(m1, m2, "The second object is a copy of the first one.");

        m1.setId(20);
        assertEquals(m2, m1, "The second object has its id changed.");

        m2.setCommentary("The last test");
        assertNotEquals(m2, m1, "The second object has one of its attributes other than its id changed.");
    }
}