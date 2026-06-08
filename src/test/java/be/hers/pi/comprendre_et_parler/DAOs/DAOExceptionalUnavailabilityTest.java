package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DAOExceptionalUnavailabilityTest {
    private static ExceptionalUnavailability u1;
    private static ExceptionalUnavailability u2;
    private static ExceptionalUnavailability u3;
    private static Interpreter i1;
    private static Interpreter i2;
    private final static DAOExceptionalUnavailability unavailabilityDAO = new DAOExceptionalUnavailability();
    private final static LocalDateTime today = LocalDateTime.now();

    @BeforeAll
    public static void init() throws SQLException {
        DatabaseConnector.initialize();
        City c1 = new City(1, "Bruxelles", 1000);
        new DAOCity().create(c1);
        Location l1 = new Location(1, "Bruxelles", c1, "Rue Neuve", "5", 0);
        new DAOLocation().create(l1);
        i1 = new Interpreter(1, "test1", "Toto", "Toto", LocalDate.now().minusYears(30),
                "1234", "toto@gmail.com", "123/45.67.89", 10, 120,
                "Auto", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        i2 = new Interpreter(2, "i260001", "Tata", "Tata", LocalDate.now().minusYears(50),
                "9874", "tata@gmail.com", "987/65.41.32", 30, 450,
                "Auto", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        new DAOInterpreter().create(i1);
        new DAOInterpreter().create(i2);

        PunctualTimeSlot p1 = new PunctualTimeSlot(1, today.plusWeeks(1), today.plusWeeks(2));
        PunctualTimeSlot p2 = new PunctualTimeSlot(2, today.minusDays(3), today.plusDays(2));
        new DAOPunctualTimeSlot().create(p1);
        new DAOPunctualTimeSlot().create(p2);

        u1 = new ExceptionalUnavailability("Sick", p1);
        u2 = new ExceptionalUnavailability("Party", p2);
        u3 = new ExceptionalUnavailability("Vacancy", p1);
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
        assertEquals(u1, unavailabilityDAO.find(i1.getId(), u1.getTimeSlot().getId()), "Find the updated object.");
        assertEquals(u2, unavailabilityDAO.find(i1.getId(), u2.getTimeSlot().getId()), "Find the unchanged object.");
        assertNull(unavailabilityDAO.find(i2.getId(), u1.getTimeSlot().getId()), "There is no object with this ID.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            unavailabilityDAO.create(u1, i1);
        }, "Create a object in the database.");

        u1.setReason("Lazy");
        assertThrows(AlreadyExistsException.class, () -> {
            unavailabilityDAO.create(u1, i1);
        }, "This object already exists in the database with another reason.");

        assertDoesNotThrow(() -> {
            unavailabilityDAO.create(u2, i1);
        }, "Create another object in the database.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        u1.setReason("Lazy");
        assertDoesNotThrow(() -> {
            unavailabilityDAO.update(u1, i1);
        }, "The object has been updated.");

        assertThrows(NoSuchElementException.class, () -> {
            unavailabilityDAO.update(u3, i2);
        }, "There is no object with this Interpreter ID.");
    }

    @Test
    @Order(6)
    public void testDelete() {
        assertThrows(NoSuchElementException.class, () -> {
            unavailabilityDAO.delete(3, 3);
        }, "There are no objects with these IDs.");

        assertThrows(NoSuchElementException.class, () -> {
            unavailabilityDAO.delete(i2.getId(), u2.getTimeSlot().getId());
        }, "There are no objects with this combination of IDs.");

        assertDoesNotThrow(() -> {
            unavailabilityDAO.delete(i1.getId(), u1.getTimeSlot().getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            unavailabilityDAO.delete(i1.getId(), u1.getTimeSlot().getId());
        }, "The object has already been removed from the database.");

        assertDoesNotThrow(() -> {
            unavailabilityDAO.delete(i1.getId(), u2.getTimeSlot().getId());
        }, "The object has been removed from the database.");
    }

    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<ExceptionalUnavailability> unavailability = unavailabilityDAO.findAll();
        assertEquals(2, unavailability.size(), "There are two objects in the database.");
        u1.setReason("Sick");
        assertTrue(unavailability.contains(u1));
        assertTrue(unavailability.contains(u2));
    }

    @Test
    @Order(5)
    public void testFindForInterpreter() throws SQLException {
        Set<ExceptionalUnavailability> unavailability = unavailabilityDAO.findForInterpreter(50);
        assertTrue(unavailability.isEmpty(), "There is no interpreter with this ID.");

        unavailabilityDAO.create(u1, i2);

        unavailability = unavailabilityDAO.findForInterpreter(i1.getId());
        assertEquals(2, unavailability.size(), "There are two objects in the database for this interpreter.");
        assertTrue(unavailability.contains(u1));
        assertTrue(unavailability.contains(u2));
    }
}