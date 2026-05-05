package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.City;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DAOCityTest {
    public static City c1;
    public static City c2;
    public static City c3;
    public final static DAOCity cityDAO = new DAOCity();

    @BeforeAll
    public static void init() {
        DatabaseConnector.initialize();
        c1 = new City(75, "Bruxelles", 1000);
        c2 = new City(1, "Libramont", 6800);
        c3 = new City(3, "Bastogne", 6600);
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
            assertEquals(c3, cityDAO.find(1), "Find the updated object.");
            assertEquals(c2, cityDAO.find(2), "Find the unchanged object.");
            assertNull(cityDAO.find(3), "There is no object with this ID.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            cityDAO.create(c1);
        }, "Create a object in the database.");
        assertEquals(1, c1.getId(), "The ID must have been changed.");

        c1.setId(20);
        assertThrows(AlreadyExistsException.class, () -> {
            cityDAO.create(c1);
        }, "This object already exists in the database with another ID.");

        assertDoesNotThrow(() -> {
            cityDAO.create(c2);
        }, "Create another object in the database.");
        assertEquals(2, c2.getId(), "The ID must have been changed.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        assertThrows(NoSuchElementException.class, () -> {
            cityDAO.update(c3);
        }, "There are no objects with this ID.");

        c3.setId(1);
        assertDoesNotThrow(() -> {
            cityDAO.update(c3);
        }, "The object has been updated.");

        c3.setId(2);
        assertThrows(AlreadyExistsException.class, () -> {
            cityDAO.update(c3);
        }, "This object already exists in the database with another ID.");
    }

    @Test
    @Order(5)
    public void testDelete() {
        assertDoesNotThrow(() -> {
            cityDAO.delete(c2.getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            cityDAO.delete(c2.getId());
        }, "The object has already been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            cityDAO.delete(50);
        }, "There is no object with this ID.");

        assertDoesNotThrow(() -> {
            cityDAO.delete(1);
        }, "The object has been removed from the database.");
    }

    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<City> cities = cityDAO.findAll();
        assertEquals(2, cities.size(), "There are two objects in the database.");
        assertTrue(cities.contains(c1));
        assertTrue(cities.contains(c2));
    }
}