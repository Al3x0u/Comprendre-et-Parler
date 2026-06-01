package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.AcademicSkill;
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
class DAOAcademicSkillTest {
    public static AcademicSkill a1;
    public static AcademicSkill a2;
    public static AcademicSkill a3;
    public final static DAOAcademicSkill academicSkillDAO = new DAOAcademicSkill();

    @BeforeAll
    public static void init() {
        DatabaseConnector.initialize();
        a1 = new AcademicSkill(75, "Science");
        a2 = new AcademicSkill(1, "Français");
        a3 = new AcademicSkill(3, "Java");
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
        assertEquals(a3, academicSkillDAO.find(1), "Find the updated object.");
        assertEquals(a2, academicSkillDAO.find(2), "Find the unchanged object.");
        assertNull(academicSkillDAO.find(3), "There is no object with this ID.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            academicSkillDAO.create(a1);
        }, "Create a object in the database.");
        assertEquals(1, a1.getId(), "The ID must have been changed.");

        a1.setId(20);
        assertThrows(AlreadyExistsException.class, () -> {
            academicSkillDAO.create(a1);
        }, "This object already exists in the database with another ID.");

        assertDoesNotThrow(() -> {
            academicSkillDAO.create(a2);
        }, "Create another object in the database.");
        assertEquals(2, a2.getId(), "The ID must have been changed.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        assertThrows(NoSuchElementException.class, () -> {
            academicSkillDAO.update(a3);
        }, "There are no objects with this ID.");

        a3.setId(1);
        assertDoesNotThrow(() -> {
            academicSkillDAO.update(a3);
        }, "The object has been updated.");

        a3.setId(2);
        assertThrows(AlreadyExistsException.class, () -> {
            academicSkillDAO.update(a3);
        }, "This object already exists in the database with another ID.");
    }

    @Test
    @Order(6)
    public void testDelete() {
        assertDoesNotThrow(() -> {
            academicSkillDAO.delete(a2.getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            academicSkillDAO.delete(a2.getId());
        }, "The object has already been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            academicSkillDAO.delete(50);
        }, "There is no object with this ID.");

        assertDoesNotThrow(() -> {
            academicSkillDAO.delete(1);
        }, "The object has been removed from the database.");
    }

    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<AcademicSkill> academicSkills = academicSkillDAO.findAll();
        assertEquals(2, academicSkills.size(), "There are two objects in the database.");
        assertTrue(academicSkills.contains(a1));
        assertTrue(academicSkills.contains(a2));
    }

    @Test
    @Order(5)
    public void testGetAcademicSkillOfAnInterpreter() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            academicSkillDAO.getAcademicSkillOfAnInterpreter(-1);
        }, "ID cannot be less than 0.");

        Set<AcademicSkill> databaseAcademicSkills = academicSkillDAO.getAcademicSkillOfAnInterpreter(50);
        assertTrue(databaseAcademicSkills.isEmpty(), "There is no interpreter with this ID.");

        City c1 = new City(1, "Bruxelles", 1000);
        new DAOCity().create(c1);
        Location l1 = new Location(1, "Bruxelles", c1, "Rue Neuve", "5", 0);
        new DAOLocation().create(l1);

        Set<AcademicSkill> academicSkills = new HashSet<>();
        academicSkills.add(a1);
        Interpreter i1 = new Interpreter(1, "i260001", "Tata", "Tata", LocalDate.now().minusYears(50),
                "9874", "tata@gmail.com", "987/65.41.32", 30, 450,
                "Auto", academicSkills, new HashSet<>(), l1, new HashSet<>());
        new DAOInterpreter().create(i1);
        academicSkills.add(a2);
        Interpreter i2 = new Interpreter(1, "i260001", "Toto", "Toto", LocalDate.now().minusYears(50),
                "9874", "toto@gmail.com", "123/45.67.89", 30, 450,
                "Auto", academicSkills, new HashSet<>(), l1, new HashSet<>());
        new DAOInterpreter().create(i2);

        databaseAcademicSkills = academicSkillDAO.getAcademicSkillOfAnInterpreter(i1.getId());
        assertEquals(1, databaseAcademicSkills.size(), "There is one object in the database for this interpreter.");
        assertTrue(databaseAcademicSkills.contains(a1));
    }
}