package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.City;
import be.hers.pi.comprendre_et_parler.models.Location;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DAOLocationTest {
    private static Location l1;
    private static Location l2;
    private static Location l3;
    private final static DAOLocation locationDAO = new DAOLocation();

    @BeforeAll
    public static void init() throws SQLException {
        DatabaseConnector.initialize();
        City c1 = new City(1, "Bruxelles", 1000);
        new DAOCity().create(c1);

        l1 = new Location(75, "Bruxelles", c1, "Rue Neuve", "5", 0);
        l2 = new Location(1, "Bruxelles", c1, "Chaussée de Waterloo", "8b", 5);
        l3 = new Location(3, "Bruxelles", c1, "Avenue Fonsny ", "14", 0);
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
        assertEquals(l3, locationDAO.find(1), "Find the updated object.");
        assertEquals(l2, locationDAO.find(2), "Find the unchanged object.");
        assertNull(locationDAO.find(3), "There is no object with this ID.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            locationDAO.create(l1);
        }, "Create a object in the database.");
        assertEquals(1, l1.getId(), "The ID must have been changed.");

        l1.setId(20);
        assertThrows(AlreadyExistsException.class, () -> {
            locationDAO.create(l1);
        }, "This object already exists in the database with another ID.");

        assertDoesNotThrow(() -> {
            locationDAO.create(l2);
        }, "Create another object in the database.");
        assertEquals(2, l2.getId(), "The ID must have been changed.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        assertThrows(NoSuchElementException.class, () -> {
            locationDAO.update(l3);
        }, "There are no objects with this ID.");

        l3.setId(1);
        assertDoesNotThrow(() -> {
            locationDAO.update(l3);
        }, "The object has been updated.");

        l3.setId(2);
        assertThrows(AlreadyExistsException.class, () -> {
            locationDAO.update(l3);
        }, "This object already exists in the database with another ID.");
    }

    @Test
    @Order(5)
    public void testDelete() {
        assertDoesNotThrow(() -> {
            locationDAO.delete(l2.getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            locationDAO.delete(l2.getId());
        }, "The object has already been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            locationDAO.delete(50);
        }, "There is no object with this ID.");

        assertDoesNotThrow(() -> {
            locationDAO.delete(1);
        }, "The object has been removed from the database.");
    }

    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<Location> locations = locationDAO.findAll();
        assertEquals(2, locations.size(), "There are two objects in the database.");
        assertTrue(locations.contains(l1));
        assertTrue(locations.contains(l2));
    }
}