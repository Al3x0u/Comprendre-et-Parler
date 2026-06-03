package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.City;
import be.hers.pi.comprendre_et_parler.models.Interpreter;
import be.hers.pi.comprendre_et_parler.models.Manager;
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
class DAOManagerTest {
    public static Manager m1;
    public static Manager m2;
    public static Manager m3;
    public final static DAOManager managerDAO = new DAOManager();

    @BeforeAll
    public static void init() throws SQLException {
        DatabaseConnector.initialize();
        City c1 = new City(1, "Bruxelles", 1000);
        new DAOCity().create(c1);
        Location l1 = new Location(1, "Bruxelles", c1, "Rue Neuve", "5", 0);
        new DAOLocation().create(l1);

        m1 = new Manager(75, "test1", "Toto", "Toto", LocalDate.now().minusYears(30),
                "1234", "toto@gmail.com", "123/45.67.89", 10, 120,
                "Auto", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        m1.setUnavailability(new HashSet<>());
        m2 = new Manager(1, "r260001", "Tata", "Tata", LocalDate.now().minusYears(50),
                "9874", "tata@gmail.com", "987/65.41.32", 30, 450,
                "Auto", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        m2.setUnavailability(new HashSet<>());
        m3 = new Manager(3, "b741985", "Alice", "Charpentier", LocalDate.now().minusYears(25),
                "yth794t8rg", "alice@gmail.com", "4865/75.98.24", 20, 300,
                "Vélo", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        m3.setUnavailability(new HashSet<>());
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
    public void testFindId() throws SQLException {
        Manager m4 = managerDAO.find(1);
        assertEquals(m3, m4, "Find the updated object.");
        assertNotEquals(m3.getLogin(), m4.getLogin(), "The login must not have been updated.");
        assertEquals(m2, managerDAO.find(2), "Find the unchanged object.");
        assertNull(managerDAO.find(3), "There is no object with this ID.");
    }

    @Test
    @Order(5)
    public void testFindLogin() throws SQLException {
        Manager m4 = managerDAO.find("TT2601");
        assertEquals(m3, m4, "Find the updated object.");
        assertNotEquals(m3.getId(), m4.getId(), "The ID must not have been updated.");
        assertEquals(m2, managerDAO.find("TT2602"), "Find the unchanged object.");
        assertNull(managerDAO.find("TT2650"), "There is no object with this login.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            managerDAO.create(m1);
        }, "Create a object in the database.");
        assertEquals(1, m1.getId(), "The ID must have been changed.");
        assertEquals("TT2601", m1.getLogin(), "The login must have been changed.");

        m1.setId(20);
        assertThrows(AlreadyExistsException.class, () -> {
            managerDAO.create(m1);
        }, "This object already exists in the database with another ID.");

        assertDoesNotThrow(() -> {
            managerDAO.create(m2);
        }, "Create another object in the database.");
        assertEquals(2, m2.getId(), "The ID must have been changed.");
        assertEquals("TT2602", m2.getLogin(), "The login must have been changed.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        assertThrows(NoSuchElementException.class, () -> {
            managerDAO.update(m3);
        }, "There are no objects with this ID.");

        m3.setId(1);
        assertDoesNotThrow(() -> {
            managerDAO.update(m3);
        }, "The object has been updated.");

        m3.setId(2);
        assertThrows(AlreadyExistsException.class, () -> {
            managerDAO.update(m3);
        }, "This object already exists in the database with another ID.");
    }

    @Test
    @Order(6)
    public void testDelete() {
        assertDoesNotThrow(() -> {
            managerDAO.delete(m2.getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            managerDAO.delete(m2.getId());
        }, "The object has already been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            managerDAO.delete(50);
        }, "There is no object with this ID.");

        assertDoesNotThrow(() -> {
            managerDAO.delete(1);
        }, "The object has been removed from the database.");
    }


    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<Manager> managers = managerDAO.findAll();
        assertEquals(2, managers.size(), "There are two objects in the database.");
        assertTrue(managers.contains(m1));
        assertTrue(managers.contains(m2));
    }
}