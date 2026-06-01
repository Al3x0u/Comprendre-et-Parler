package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.City;
import be.hers.pi.comprendre_et_parler.models.Interpreter;
import be.hers.pi.comprendre_et_parler.models.Location;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DAOInterpreterTest {
    public static Interpreter i1;
    public static Interpreter i2;
    public static Interpreter i3;
    public final static DAOInterpreter interpreterDAO = new DAOInterpreter();

    @BeforeAll
    public static void init() throws SQLException {
        DatabaseConnector.initialize();
        City c1 = new City(1, "Bruxelles", 1000);
        new DAOCity().create(c1);
        Location l1 = new Location(1, "Bruxelles", c1, "Rue Neuve", "5", 0);
        new DAOLocation().create(l1);

        i1 = new Interpreter(75, "test1", "Toto", "Toto", LocalDate.now().minusYears(30),
                "1234", "toto@gmail.com", "123/45.67.89", 10, 120,
                "Auto", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        i1.setUnavailability(new HashSet<>());
        i2 = new Interpreter(1, "i260001", "Tata", "Tata", LocalDate.now().minusYears(50),
                "9874", "tata@gmail.com", "987/65.41.32", 30, 450,
                "Auto", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        i2.setUnavailability(new HashSet<>());
        i3 = new Interpreter(3, "b741985", "Alice", "Charpentier", LocalDate.now().minusYears(25),
                "yth794t8rg", "alice@gmail.com", "4865/75.98.24", 20, 300,
                "Vélo", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        i3.setUnavailability(new HashSet<>());
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
        Interpreter i4 = interpreterDAO.find(1);
        assertEquals(i3, i4, "Find the updated object.");
        assertNotEquals(i3.getLogin(), i4.getLogin(), "The login must not have been updated.");
        assertEquals(i2, interpreterDAO.find(2), "Find the unchanged object.");
        assertNull(interpreterDAO.find(3), "There is no object with this ID.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            interpreterDAO.create(i1);
        }, "Create a object in the database.");
        assertEquals(1, i1.getId(), "The ID must have been changed.");
        assertEquals("TT2601", i1.getLogin(), "The login must have been changed.");

        i1.setId(20);
        assertThrows(AlreadyExistsException.class, () -> {
            interpreterDAO.create(i1);
        }, "This object already exists in the database with another ID.");

        assertDoesNotThrow(() -> {
            interpreterDAO.create(i2);
        }, "Create another object in the database.");
        assertEquals(2, i2.getId(), "The ID must have been changed.");
        assertEquals("TT2602", i2.getLogin(), "The login must have been changed.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        assertThrows(NoSuchElementException.class, () -> {
            interpreterDAO.update(i3);
        }, "There are no objects with this ID.");

        i3.setId(1);
        assertDoesNotThrow(() -> {
            interpreterDAO.update(i3);
        }, "The object has been updated.");

        i3.setId(2);
        assertThrows(AlreadyExistsException.class, () -> {
            interpreterDAO.update(i3);
        }, "This object already exists in the database with another ID.");
    }

    @Test
    @Order(5)
    public void testDelete() {
        assertDoesNotThrow(() -> {
            interpreterDAO.delete(i2.getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            interpreterDAO.delete(i2.getId());
        }, "The object has already been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            interpreterDAO.delete(50);
        }, "There is no object with this ID.");

        assertDoesNotThrow(() -> {
            interpreterDAO.delete(1);
        }, "The object has been removed from the database.");
    }

    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<Interpreter> interpreters = interpreterDAO.findAll();
        assertEquals(2, interpreters.size(), "There are two objects in the database.");
        assertTrue(interpreters.contains(i1));
        assertTrue(interpreters.contains(i2));
    }
}