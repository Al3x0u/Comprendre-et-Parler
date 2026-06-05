package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.*;
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
class DAOBeneficiaryTest {
    private static Beneficiary b1;
    private static Beneficiary b2;
    private static Beneficiary b3;
    private final static DAOBeneficiary beneficiaryDAO = new DAOBeneficiary();

    @BeforeAll
    public static void init() throws SQLException {
        DatabaseConnector.initialize();
        City c1 = new City(1, "Bruxelles", 1000);
        new DAOCity().create(c1);
        Location l1 = new Location(1, "Bruxelles", c1, "Rue Neuve", "5", 0);
        new DAOLocation().create(l1);
        Interpreter i1 = new Interpreter(1, "b741985", "Alice", "Charpentier", LocalDate.now().minusYears(25),
                "yth794t8rg", "alice@gmail.com", "4865/75.98.24", 20, 300,
                "Vélo", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        new DAOInterpreter().create(i1);

        Status s1 = new Status(1, "Test", 50);
        new DAOStatus().create(s1);
        Status s2 = new Status(2, "Test2", 24);
        new DAOStatus().create(s2);

        b1 = new Beneficiary(75, "test1", "Toto", "Toto", LocalDate.now().minusYears(10),
                "1234", "toto@gmail.com", "123/45.67.89", s2, i1);
        b2 = new Beneficiary(2, "r260001", "Tata", "Tata", LocalDate.now().minusYears(15),
                "9874", "tata@gmail.com", "987/65.41.32", s1, i1);

        Interpreter i2 = new Interpreter("b741985", "Jessica", "DuBuisson", LocalDate.now().minusYears(56),
                "rf894re6fe", "jessica@gmail.com", "4865/75.98.24", 10, 350,
                "Vélo", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        new DAOInterpreter().create(i2);
        b3 = new Beneficiary(50, "i412876", "Jessica", "DuBuisson", LocalDate.now().minusYears(7),
                "greg54re1fe", "jessica@gmail.com", "754/69.24.18", s1, i2);
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
        Beneficiary b4 = beneficiaryDAO.find(3);
        assertEquals(b3, b4, "Find the updated object.");
        assertNotEquals(b3.getLogin(), b4.getLogin(), "The login must not have been updated.");
        assertEquals(b2, beneficiaryDAO.find(4), "Find the unchanged object.");
        assertNull(beneficiaryDAO.find(5), "There is no object with this ID.");
    }

    @Test
    @Order(5)
    public void testFindLogin() throws SQLException {
        Beneficiary b4 = beneficiaryDAO.find("TT2601");
        assertEquals(b3, b4, "Find the updated object.");
        assertNotEquals(b3.getId(), b4.getId(), "The ID must not have been updated.");
        assertEquals(b2, beneficiaryDAO.find("TT2602"), "Find the unchanged object.");
        assertNull(beneficiaryDAO.find("TT2650"), "There is no object with this login.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            beneficiaryDAO.create(b1);
        }, "Create a object in the database.");
        assertEquals(3, b1.getId(), "The ID must have been changed.");
        assertEquals("TT2601", b1.getLogin(), "The login must have been changed.");

        b1.setId(20);
        assertThrows(AlreadyExistsException.class, () -> {
            beneficiaryDAO.create(b1);
        }, "This object already exists in the database with another ID.");

        assertDoesNotThrow(() -> {
            beneficiaryDAO.create(b2);
        }, "Create another object in the database.");
        assertEquals(4, b2.getId(), "The ID must have been changed.");
        assertEquals("TT2602", b2.getLogin(), "The login must have been changed.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        assertThrows(NoSuchElementException.class, () -> {
            beneficiaryDAO.update(b3);
        }, "There are no objects with this ID.");

        b3.setId(3);
        assertDoesNotThrow(() -> {
            beneficiaryDAO.update(b3);
        }, "The object has been updated.");

        b3.setId(4);
        assertThrows(AlreadyExistsException.class, () -> {
            beneficiaryDAO.update(b3);
        }, "This object already exists in the database with another ID.");
    }

    @Test
    @Order(11)
    public void testDelete() {
        assertDoesNotThrow(() -> {
            beneficiaryDAO.delete(b2.getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            beneficiaryDAO.delete(b2.getId());
        }, "The object has already been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            beneficiaryDAO.delete(50);
        }, "There is no object with this ID.");

        assertDoesNotThrow(() -> {
            beneficiaryDAO.delete(3);
        }, "The object has been removed from the database.");
    }

    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<Beneficiary> beneficiaries = beneficiaryDAO.findAll();
        assertEquals(2, beneficiaries.size(), "There are two objects in the database.");
        assertTrue(beneficiaries.contains(b1));
        assertTrue(beneficiaries.contains(b2));
    }

    @Test
    @Order(6)
    public void testFindReferencedBeneficiaries() throws SQLException {
        assertThrows(NoSuchElementException.class, () -> {
            beneficiaryDAO.findReferencedBeneficiaries(50);
        }, "There is no Interpreter with this ID.");

        Set<Beneficiary> beneficiaries = beneficiaryDAO.findReferencedBeneficiaries(1);
        assertEquals(1, beneficiaries.size(), "There is one beneficiaries in the database with this interpreter.");
        assertTrue(beneficiaries.contains(b2));
    }

    @Test
    @Order(7)
    public void testFindByStatus() throws SQLException {
        assertThrows(NoSuchElementException.class, () -> {
            beneficiaryDAO.findReferencedBeneficiaries(50);
        }, "There is no Status with this ID.");

        Set<Beneficiary> beneficiaries = beneficiaryDAO.findByStatus(1);
        assertEquals(2, beneficiaries.size(), "There are two beneficiaries in the database with this status.");
        assertTrue(beneficiaries.contains(b2));
        assertTrue(beneficiaries.contains(b3));
    }

    @Test
    @Order(8)
    public void testUpdateInterpreterRef() {
        assertThrows(NoSuchElementException.class, () -> {
            beneficiaryDAO.updateInterpreterRef(50, 1);
        }, "There is no Beneficiary with this ID.");

        assertThrows(SQLException.class, () -> {
            beneficiaryDAO.updateInterpreterRef(3, 50);
        }, "There is no Interpreter with this ID.");

        assertDoesNotThrow(() -> {
            beneficiaryDAO.updateInterpreterRef(4, 2);
        }, "The beneficiary's reference interpreter has been changed.");
    }

    @Test
    @Order(9)
    public void testUpdateStatus() {
        assertThrows(NoSuchElementException.class, () -> {
            beneficiaryDAO.updateStatus(50, 1);
        }, "There is no Beneficiary with this ID.");

        assertThrows(SQLException.class, () -> {
            beneficiaryDAO.updateStatus(3, 50);
        }, "There is no Status with this ID.");

        assertDoesNotThrow(() -> {
            beneficiaryDAO.updateStatus(4, 2);
        }, "The beneficiary's status has been changed.");
    }

    @Test
    @Order(10)
    public void testCount() throws SQLException {
        assertEquals(2, beneficiaryDAO.count(), "There are two beneficiaries in the database.");
    }
}