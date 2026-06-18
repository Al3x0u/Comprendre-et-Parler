package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DAOMissionTest {
    private static Mission m1;
    private static Mission m2;
    private static Mission m3;
    private static Mission m4;
    private final static DAOMission missionDAO = new DAOMission();
    private final static LocalDate today = LocalDate.now();

    @BeforeAll
    public static void init() throws SQLException {
        DatabaseConnector.initialize();
        City c1 = new City(1, "Bruxelles", 1000);
        new DAOCity().create(c1);
        Location l1 = new Location(1, "Bruxelles", c1, "Rue Neuve", "5", 0);
        new DAOLocation().create(l1);

        JobSkill js1 = new JobSkill("LSFB");
        new DAOJobSkill().create(js1);

        AcademicSkill as1 = new AcademicSkill("Math");
        new DAOAcademicSkill().create(as1);

        Interpreter i1 = new Interpreter(75, "test1", "Toto", "Toto", today.minusYears(30),
                "1234", "toto@gmail.com", "123/45.67.89", 10, 120,
                "Auto", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        i1.setUnavailability(new HashSet<>());
        Interpreter i2 = new Interpreter(1, "i260001", "Tata", "Tata", today.minusYears(50),
                "9874", "tata@gmail.com", "987/65.41.32", 30, 450,
                "Auto", new HashSet<>(), new HashSet<>(), l1, new HashSet<>());
        i2.setUnavailability(new HashSet<>());
        new DAOInterpreter().create(i1);
        new DAOInterpreter().create(i2);

        PunctualTimeSlot t1 = new PunctualTimeSlot(1, LocalDateTime.now().minusHours(1), LocalDateTime.now());
        PunctualTimeSlot t2 = new PunctualTimeSlot(1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        new DAOPunctualTimeSlot().create(t1);
        new DAOPunctualTimeSlot().create(t2);

        Status s1 = new Status(1, "Test", 50);
        new DAOStatus().create(s1);
        Beneficiary b1 = new Beneficiary(2, "test1", "Toto", "Toto", today.minusYears(10),
                "1234", "toto@gmail.com", "123/45.67.89", s1, i1);
        Beneficiary b2 = new Beneficiary(2, "test2", "Tata", "Tata", today.minusYears(10),
                "9876", "tata@gmail.com", "987/65.43.21", s1, i1);
        new DAOBeneficiary().create(b1);
        new DAOBeneficiary().create(b2);

        m1 = new Mission(75, "Pending mission", MissionState.PENDING, "pending", t1,
                l1, new HashSet<>(), null, null, "B7", 2);
        m1.addInterpreter(i1);
        m2 = new Mission(4, "Accepted mission", MissionState.ACCEPTED, null, t1,
                b1, l1, js1, as1, "A34", 3);
        m2.addInterpreter(i2);
        m3 = new Mission(50, "Accepted mission", MissionState.ACCEPTED, "accepted", t2,
                b1, l1, js1, as1, "A34", 3);
        m3.addInterpreter(i2);
        m4 = new Mission(4, "Denied mission", MissionState.DENIED, "denied", t2,
                b2, l1, js1, as1, "B6", 0);
        m4.addInterpreter(i2);
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
        Mission m4 = missionDAO.find(1);
        m4.setInterpreters(new DAOInterpreter().findAllByMissionId(m4.getId()));
        assertEquals(m3, m4, "Find the updated object.");

        Mission m5 = missionDAO.find(3);
        assertNotEquals(m2, m5, "Find the unchanged object but the interpreter set was not initialized.");
        m5.setInterpreters(new DAOInterpreter().findAllByMissionId(m5.getId()));
        assertEquals(m2, m5, "Find the unchanged object.");

        assertNull(missionDAO.find(50), "There is no object with this ID.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        assertDoesNotThrow(() -> {
            missionDAO.create(m1);
        }, "Create a object in the database.");
        assertEquals(1, m1.getId(), "The ID must have been changed.");

        m1.setImportance(1);
        assertDoesNotThrow(() -> {
            missionDAO.create(m1);
        }, "This object already exists in the database with another ID, but the status is pending.");
        assertEquals(2, m1.getId(), "The ID must have been changed.");

        assertDoesNotThrow(() -> {
            missionDAO.create(m2);
        }, "Create another object in the database.");
        assertEquals(3, m2.getId(), "The ID must have been changed.");

        m2.setId(30);
        assertThrows(AlreadyExistsException.class, () -> {
            missionDAO.create(m2);
        }, "This object already exists in the database with another ID.");
        m2.setId(3);

        assertDoesNotThrow(() -> {
            missionDAO.create(m4);
        }, "Create another object in the database.");
        assertEquals(4, m4.getId(), "The ID must have been changed.");

        m4.setImportance(1);
        assertDoesNotThrow(() -> {
            missionDAO.create(m4);
        }, "This object already exists in the database with another ID, but the status is pending.");
        assertEquals(5, m4.getId(), "The ID must have been changed.");
    }

    @Test
    @Order(3)
    public void testUpdate() {
        assertThrows(NoSuchElementException.class, () -> {
            missionDAO.update(m3);
        }, "There are no objects with this ID.");

        m3.setId(1);
        assertDoesNotThrow(() -> {
            missionDAO.update(m3);
        }, "The object has been updated.");

        m3.setId(2);
        assertThrows(AlreadyExistsException.class, () -> {
            missionDAO.update(m3);
        }, "This object already exists in the database with another ID.");
        m3.setId(1);
    }

    @Test
    @Order(9)
    public void testDelete() {
        assertDoesNotThrow(() -> {
            missionDAO.delete(m2.getId());
        }, "The object has been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            missionDAO.delete(m2.getId());
        }, "The object has already been removed from the database.");

        assertThrows(NoSuchElementException.class, () -> {
            missionDAO.delete(50);
        }, "There is no object with this ID.");

        assertDoesNotThrow(() -> {
            missionDAO.delete(1);
        }, "The object has been removed from the database.");
    }

    @Test
    @Order(2)
    public void testFindAll() throws SQLException {
        Set<Mission> missions = missionDAO.findAll();
        assertEquals(5, missions.size(), "There are five objects in the database.");

        Set<Mission> missionsUpdated = new HashSet<>();
        for(Mission m : missions){
            m.setInterpreters(new DAOInterpreter().findAllByMissionId(m.getId()));
            missionsUpdated.add(m);
        }

        assertTrue(missionsUpdated.contains(m1));
        assertTrue(missionsUpdated.contains(m2));
    }

    @Test
    @Order(5)
    public void testGetAllMissionsForWeek() throws SQLException {
        int todayYear = today.getYear();
        int todayWeek = today.get(ChronoField.ALIGNED_WEEK_OF_YEAR) + 1;
        Set<Mission> missions = missionDAO.getAllMissionsForWeek(todayYear, todayWeek - 1);
        assertTrue(missions.isEmpty(), "There are no missions for this week.");

        missions = missionDAO.getAllMissionsForWeek(todayYear, todayWeek);
        assertEquals(5, missions.size(), "There are five missions for this week.");
    }

    @Test
    @Order(6)
    public void testGetScheduleForWeek() throws SQLException {
        int todayYear = today.getYear();
        int todayWeek = today.get(ChronoField.ALIGNED_WEEK_OF_YEAR) + 1;
        Set<Mission> missions = missionDAO.getScheduleForWeek(3, todayYear, todayWeek - 1);
        assertTrue(missions.isEmpty(), "There are no missions for this week.");

        missions = missionDAO.getScheduleForWeek(30, todayYear, todayWeek);
        assertTrue(missions.isEmpty(), "There is no user with this ID.");

        missions = missionDAO.getScheduleForWeek(3, todayYear, todayWeek);
        assertEquals(2, missions.size(), "There are two missions for this week and this beneficiary.");

        missions = missionDAO.getScheduleForWeek(10, todayYear, todayWeek);
        assertTrue(missions.isEmpty(), "There are no missions for this user.");

        missions = missionDAO.getScheduleForWeek(2, todayYear, todayWeek);
        assertEquals(4, missions.size(), "There are four missions for this week and this interpreter.");
    }

    @Test
    @Order(7)
    public void testGetScheduleForDay() throws SQLException {
        Set<Mission> missions = missionDAO.getScheduleForDay(3, today.minusDays(1));
        assertTrue(missions.isEmpty(), "There are no missions for this day.");

        missions = missionDAO.getScheduleForDay(30, today);
        assertTrue(missions.isEmpty(), "There is no user with this ID.");

        missions = missionDAO.getScheduleForDay(3, today);
        assertEquals(2, missions.size(), "There are two missions for this day and this beneficiary.");

        missions = missionDAO.getScheduleForDay(10, today);
        assertTrue(missions.isEmpty(), "There are no missions for this user.");

        missions = missionDAO.getScheduleForDay(2, today);
        assertEquals(4, missions.size(), "There are four missions for this day and this interpreter.");
    }

    @Test
    @Order(8)
    public void testHasActiveMissions() throws SQLException {
        assertFalse(missionDAO.hasActiveMissions(1), "There is no beneficiary with this ID.");
        assertTrue(missionDAO.hasActiveMissions(3), "This beneficiary has missions.");
        assertFalse(missionDAO.hasActiveMissions(4), "The only mission this beneficiary has is denied.");
    }
}