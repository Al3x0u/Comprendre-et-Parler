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
class DAOJobSkillTest {
    private static JobSkill j1;
    private static JobSkill j2;
    private static JobSkill j3;
    private final static DAOJobSkill jobSkillDAO = new DAOJobSkill();


    @BeforeAll
    public static void init() {
        DatabaseConnector.initialize();
        j1 = new JobSkill(75, "Science");
        j2 = new JobSkill(1, "Français");
        j3 = new JobSkill(3, "Java");
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
        assertEquals(j3, jobSkillDAO.find(1), "Find the updated object.");
        assertEquals(j2, jobSkillDAO.find(2), "Find the unchanged object.");
        assertNull(jobSkillDAO.find(3), "There is no object with this ID.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            jobSkillDAO.create(j1);
        }, "Create a object in the database.");
        assertEquals(1, j1.getId(), "The ID must have been changed.");

        j1.setId(20);
        assertThrows(AlreadyExistsException.class, () -> {
            jobSkillDAO.create(j1);
        }, "This object already exists in the database with another ID.");

        assertDoesNotThrow(() -> {
            jobSkillDAO.create(j2);
        }, "Create another object in the database.");
        assertEquals(2, j2.getId(), "The ID must have been changed.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        assertThrows(NoSuchElementException.class, () -> {
            jobSkillDAO.update(j3);
        }, "There are no objects with this ID.");

        j3.setId(1);
        assertDoesNotThrow(() -> {
            jobSkillDAO.update(j3);
        }, "The object has been updated.");

        j3.setId(2);
        assertThrows(AlreadyExistsException.class, () -> {
            jobSkillDAO.update(j3);
        }, "This object already exists in the database with another ID.");
    }

    @Test
    @Order(6)
    public void testDelete() {
        assertDoesNotThrow(() -> {
            jobSkillDAO.delete(j2.getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            jobSkillDAO.delete(j2.getId());
        }, "The object has already been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            jobSkillDAO.delete(50);
        }, "There is no object with this ID.");

        assertDoesNotThrow(() -> {
            jobSkillDAO.delete(1);
        }, "The object has been removed from the database.");
    }

    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<JobSkill> jobSkills = jobSkillDAO.findAll();
        assertEquals(2, jobSkills.size(), "There are two objects in the database.");
        assertTrue(jobSkills.contains(j1));
        assertTrue(jobSkills.contains(j2));
    }

    @Test
    @Order(5)
    public void testGetJobSkillOfAnInterpreter() throws SQLException {
        assertThrows(IllegalArgumentException.class, () -> {
            jobSkillDAO.getJobSkillOfAnInterpreter(-1);
        }, "ID cannot be less than 0.");

        Set<JobSkill> databaseJobSkills = jobSkillDAO.getJobSkillOfAnInterpreter(50);
        assertTrue(databaseJobSkills.isEmpty(), "There is no interpreter with this ID.");

        City c1 = new City(1, "Bruxelles", 1000);
        new DAOCity().create(c1);
        Location l1 = new Location(1, "Bruxelles", c1, "Rue Neuve", "5", 0);
        new DAOLocation().create(l1);

        Set<JobSkill> jobSkills = new HashSet<>();
        jobSkills.add(j1);
        Interpreter i1 = new Interpreter(1, "i260001", "Tata", "Tata", LocalDate.now().minusYears(50),
                "9874", "tata@gmail.com", "987/65.41.32", 30, 450,
                "Auto", new HashSet<>(), jobSkills, l1, new HashSet<>());
        new DAOInterpreter().create(i1);
        jobSkills.add(j2);
        Interpreter i2 = new Interpreter(1, "i260001", "Toto", "Toto", LocalDate.now().minusYears(50),
                "9874", "toto@gmail.com", "123/45.67.89", 30, 450,
                "Auto", new HashSet<>(), jobSkills, l1, new HashSet<>());
        new DAOInterpreter().create(i2);

        databaseJobSkills = jobSkillDAO.getJobSkillOfAnInterpreter(i1.getId());
        assertEquals(1, databaseJobSkills.size(), "There is one object in the database for this interpreter.");
        assertTrue(databaseJobSkills.contains(j1));
    }
}