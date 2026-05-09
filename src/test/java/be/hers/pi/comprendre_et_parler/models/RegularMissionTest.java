package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RegularMissionTest {
    private static RegularMission r1;

    @BeforeAll
    public static void init() {
        r1 = new RegularMission(new PunctualTimeSlot(1, LocalDateTime.now(), LocalDateTime.now().plusHours(2)), MissionState.PENDING);
    }

    @Test
    public void testHashCode() {
        int hash1 = r1.hashCode();
        int hash2 = r1.hashCode();
        assertEquals(hash1, hash2, "Same object hashed.");

        RegularMission r2 = new RegularMission(r1);
        int hash3 = r2.hashCode();
        assertEquals(hash2, hash3, "A copied object must have the same hash.");

        r2.setStateOfMission(MissionState.ACCEPTED);
        int hash5 = r2.hashCode();
        assertNotEquals(hash3, hash5, "One attribute other than the ID has changed.");
    }

    @Test
    public void testEquals() {
        assertNotEquals(null, r1, "The second object is null.");
        assertEquals(r1, r1, "The second object is the same as the first one.");

        RegularMission r2 = new RegularMission(r1);
        assertEquals(r1, r2, "The second object is a copy of the first one.");

        r2.setStateOfMission(MissionState.CANCELED);
        assertNotEquals(r2, r1, "The second object has one of its attributes other than its id changed.");
    }
}