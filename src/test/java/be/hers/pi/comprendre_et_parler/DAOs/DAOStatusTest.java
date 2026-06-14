package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.Status;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DAOStatusTest {
    private static Status s1;
    private static Status s2;
    private static Status s3;
    private final static DAOStatus statusDAO = new DAOStatus();

    @BeforeAll
    public static void init() {
        DatabaseConnector.initialize();
        s1 = new Status(75, "Etudiant", 10);
        s2 = new Status(1, "Test", 20);
        s3 = new Status(3, "Ecolier", 30);
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
        assertEquals(s3, statusDAO.find(1), "Find the updated object.");
        assertEquals(s2, statusDAO.find(2), "Find the unchanged object.");
        assertNull(statusDAO.find(3), "There is no object with this ID.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            statusDAO.create(s1);
        }, "Create a object in the database.");
        assertEquals(1, s1.getId(), "The ID must have been changed.");

        s1.setId(20);
        assertThrows(AlreadyExistsException.class, () -> {
            statusDAO.create(s1);
        }, "This object already exists in the database with another ID.");

        assertDoesNotThrow(() -> {
            statusDAO.create(s2);
        }, "Create another object in the database.");
        assertEquals(2, s2.getId(), "The ID must have been changed.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        assertThrows(NoSuchElementException.class, () -> {
            statusDAO.update(s3);
        }, "There are no objects with this ID.");

        s3.setId(1);
        assertDoesNotThrow(() -> {
            statusDAO.update(s3);
        }, "The object has been updated.");

        s3.setId(2);
        assertThrows(AlreadyExistsException.class, () -> {
            statusDAO.update(s3);
        }, "This object already exists in the database with another ID.");
    }

    @Test
    @Order(5)
    public void testDelete() {
        assertDoesNotThrow(() -> {
            statusDAO.delete(s2.getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            statusDAO.delete(s2.getId());
        }, "The object has already been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            statusDAO.delete(50);
        }, "There is no object with this ID.");

        assertDoesNotThrow(() -> {
            statusDAO.delete(1);
        }, "The object has been removed from the database.");
    }

    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<Status> statuses = statusDAO.findAll();
        assertEquals(2, statuses.size(), "There are two objects in the database.");
        assertTrue(statuses.contains(s1));
        assertTrue(statuses.contains(s2));
    }
}