package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotTest {
    private TimeSlot t1;

    @BeforeAll
    public static void init() {
        t1 = new PunctualTimeSlot(1, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
    }

    @Test
    public void testSetId() {
        t1.setId(-1);
        assertEquals(1, t1.getId(), "id cannot be negative.");
        t1.setId(2);
        assertEquals(2, t1.getId(), "id has to change.");
    }
}