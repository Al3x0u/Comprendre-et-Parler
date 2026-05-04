package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.BaseTimeSlot;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DAOBaseTimeSlotTest {
    public static BaseTimeSlot b1;
    public static BaseTimeSlot b2;
    public static BaseTimeSlot b3;
    public final static DAOBaseTimeSlot baseTimeSlotDAO = new DAOBaseTimeSlot();
    public final static LocalDate today = LocalDate.now();
    public final static LocalTime noon = LocalTime.NOON;

    @BeforeAll
    public static void init() throws SQLException {
        DatabaseConnector.initialize();
        b1 = new BaseTimeSlot(75, today.minusYears(1), today.minusMonths(11), noon, noon.plusHours(1), DayOfWeek.WEDNESDAY);
        b2 = new BaseTimeSlot(1, today.plusWeeks(4), today.plusWeeks(5), noon.minusMinutes(45), noon, DayOfWeek.MONDAY);
        b3 = new BaseTimeSlot(3, today, today.plusDays(16), noon.minusHours(4), noon, DayOfWeek.FRIDAY);
    }

    @AfterAll
    public static void close() throws SQLException {
        try {
            TestDatabaseHelper.resetDatabase();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnector.closeInstance();
        }
    }

    @Test
    @Order(4)
    public void testFind() throws SQLException {
        assertEquals(b3, baseTimeSlotDAO.find(1), "Find the updated object.");
        assertEquals(b2, baseTimeSlotDAO.find(2), "Find the unchanged object.");
        assertNull(baseTimeSlotDAO.find(3), "There is no object with this ID.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            baseTimeSlotDAO.create(b1);
        }, "Create a object in the database.");
        assertEquals(1, b1.getId(), "The ID must have been changed.");

        b1.setId(20);
        assertThrows(AlreadyExistsException.class, () -> {
            baseTimeSlotDAO.create(b1);
        }, "This object already exists in the database with another ID.");

        assertDoesNotThrow(() -> {
            baseTimeSlotDAO.create(b2);
        }, "Create another object in the database.");
        assertEquals(2, b2.getId(), "The ID must have been changed.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        assertThrows(NoSuchElementException.class, () -> {
            baseTimeSlotDAO.update(b3);
        }, "There are no objects with this ID.");

        b3.setId(1);
        assertDoesNotThrow(() -> {
            baseTimeSlotDAO.update(b3);
        }, "The object has been updated.");

        b3.setId(2);
        assertThrows(AlreadyExistsException.class, () -> {
            baseTimeSlotDAO.update(b3);
        }, "This object already exists in the database with another ID.");
    }

    @Test
    @Order(5)
    public void testDelete() {
        assertDoesNotThrow(() -> {
            baseTimeSlotDAO.delete(b2.getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            baseTimeSlotDAO.delete(b2.getId());
        }, "The object has already been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            baseTimeSlotDAO.delete(50);
        }, "There is no object with this ID.");

        assertDoesNotThrow(() -> {
            baseTimeSlotDAO.delete(1);
        }, "The object has been removed from the database.");
    }

    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<BaseTimeSlot> baseTimeSlots = baseTimeSlotDAO.findAll();
        assertEquals(2, baseTimeSlots.size(), "There are two objects in the database.");
        assertTrue(baseTimeSlots.contains(b1));
        assertTrue(baseTimeSlots.contains(b2));
    }
}