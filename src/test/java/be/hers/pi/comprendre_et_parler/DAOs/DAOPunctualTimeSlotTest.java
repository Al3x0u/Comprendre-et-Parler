package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.PunctualTimeSlot;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DAOPunctualTimeSlotTest {
    private static PunctualTimeSlot p1;
    private static PunctualTimeSlot p2;
    private static PunctualTimeSlot p3;
    private final static DAOPunctualTimeSlot punctualTimeSlotDAO = new DAOPunctualTimeSlot();
    private final static LocalDateTime today = LocalDateTime.now();

    @BeforeAll
    public static void init() throws SQLException {
        DatabaseConnector.initialize();
        p1 = new PunctualTimeSlot(75, today.plusWeeks(1), today.plusWeeks(2));
        p2 = new PunctualTimeSlot(1, today.minusDays(3), today.plusDays(2));
        p3 = new PunctualTimeSlot(3, today.minusMonths(4), today.minusWeeks(9));
    }

    @AfterAll
    public static void close() throws SQLException {
        try {
            TestDatabaseHelper.resetDatabase();
        }catch (URISyntaxException | IOException e){
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeInstance();
        }
    }

    @Test
    @Order(4)
    public void testFind() throws SQLException {
        assertEquals(p3, punctualTimeSlotDAO.find(1), "Find the updated object.");
        assertEquals(p2, punctualTimeSlotDAO.find(2), "Find the unchanged object.");
        assertNull(punctualTimeSlotDAO.find(3), "There is no object with this ID.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            punctualTimeSlotDAO.create(p1);
        }, "Create a object in the database.");
        assertEquals(1, p1.getId(), "The ID must have been changed.");

        p1.setId(20);
        assertThrows(AlreadyExistsException.class, () -> {
            punctualTimeSlotDAO.create(p1);
        }, "This object already exists in the database with another ID.");

        assertDoesNotThrow(() -> {
            punctualTimeSlotDAO.create(p2);
        }, "Create another object in the database.");
        assertEquals(2, p2.getId(), "The ID must have been changed.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        assertThrows(NoSuchElementException.class, () -> {
            punctualTimeSlotDAO.update(p3);
        }, "There are no objects with this ID.");

        p3.setId(1);
        assertDoesNotThrow(() -> {
            punctualTimeSlotDAO.update(p3);
        }, "The object has been updated.");

        p3.setId(2);
        assertThrows(AlreadyExistsException.class, () -> {
            punctualTimeSlotDAO.update(p3);
        }, "This object already exists in the database with another ID.");
    }

    @Test
    @Order(5)
    public void testDelete() {
        assertDoesNotThrow(() -> {
            punctualTimeSlotDAO.delete(p2.getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            punctualTimeSlotDAO.delete(p2.getId());
        }, "The object has already been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            punctualTimeSlotDAO.delete(50);
        }, "There is no object with this ID.");

        assertDoesNotThrow(() -> {
            punctualTimeSlotDAO.delete(1);
        }, "The object has been removed from the database.");
    }

    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<PunctualTimeSlot> punctualTimeSlots = punctualTimeSlotDAO.findAll();
        assertEquals(2, punctualTimeSlots.size(), "There are two objects in the database.");
        assertTrue(punctualTimeSlots.contains(p1));
        assertTrue(punctualTimeSlots.contains(p2));
    }
}