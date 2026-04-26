package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {
    private static Interpreter i1;

    @BeforeAll
    public static void init() {
        i1 = new Interpreter(1, "1", "test", "test", LocalDate.now(), "1234",
                "test@gmail.com", "123/45.67.89", 10, 120,
                "Velo", null, null, null, null, null);
    }

    @Test
    void testSetHourQuotaWeek() {
        i1.setHourQuotaWeek(-1);
        assertEquals(1, i1.getHourQuotaWeek(), "id cannot be negative.");
        i1.setHourQuotaWeek(20);
        assertEquals(20, i1.getHourQuotaWeek(), "id has to change.");
        i1.setHourQuotaWeek(500);
        assertEquals(20, i1.getHourQuotaWeek(), "id has to change.");
    }

    @Test
    void testSetHourQuotaYear() {
        i1.setHourQuotaYear(-1);
        assertEquals(1, i1.getHourQuotaYear(), "id cannot be negative.");
        i1.setHourQuotaYear(100);
        assertEquals(100, i1.getHourQuotaYear(), "id has to change.");
        i1.setHourQuotaYear(60000);
        assertEquals(100, i1.getHourQuotaYear(), "id has to change.");
    }

    @Test
    public void testHashCode() {
        int hash1 = i1.hashCode();
        int hash2 = i1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        Interpreter i2 = new Interpreter(i1);
        int hash3 = i2.hashCode();
        assertEquals(hash3, hash2, "A copied object must have the same hash.");

        i2.setTransportMode("The last test");
        int hash4 = i2.hashCode();
        assertNotEquals(hash3, hash4, "One attribute other than the ID has changed.");
    }

    @Test
    void testEquals() {
        assertNotEquals(null, i1, "The second object is null.");
        assertEquals(i1, i1, "The second object is the same as the first one.");

        Interpreter i2 = new Interpreter(i1);
        assertEquals(i1, i2, "The second object is a copy of the first one.");

        i2.setHourQuotaYear(70);
        assertNotEquals(i2, i1, "The second object has one of its attributes other than its id changed.");
    }
}