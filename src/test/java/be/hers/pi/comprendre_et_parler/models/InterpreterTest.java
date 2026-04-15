package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {

    private static final int ID = 1;
    private static final String LOGIN = "jsmith";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smith";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1985, 3, 20);
    private static final String HASHED_PASSWORD = "hashedpwd456";
    private static final String EMAIL = "john.smith@test.be";
    private static final String PHONE = "0479876543";
    private static final int HOUR_QUOTA_WEEK = 35;
    private static final int HOUR_QUOTA_YEAR = 500;

    private Transportation transportation;
    private Interpreter interpreter;

    @BeforeEach
    void setUp() {
        transportation = new Transportation(1, "Voiture");
        interpreter = new Interpreter(
                ID, LOGIN, FIRST_NAME, LAST_NAME,
                BIRTH_DATE, HASHED_PASSWORD, EMAIL, PHONE,
                HOUR_QUOTA_WEEK, HOUR_QUOTA_YEAR, transportation,
                new ArrayList<>(), new ArrayList<>(), null,
                new ArrayList<>(), new ArrayList<>()
        );
    }

    // --- Tests du constructeur ---

    @Test
    void testConstructeur_avecQuotasValides_creeInterpreter() {
        assertNotNull(interpreter);
        assertEquals(ID, interpreter.getId());
        assertEquals(LOGIN, interpreter.getLogin());
        assertEquals(FIRST_NAME, interpreter.getFirstName());
        assertEquals(LAST_NAME, interpreter.getLastName());
        assertEquals(BIRTH_DATE, interpreter.getBirthDate());
        assertEquals(HASHED_PASSWORD, interpreter.getHashedPassword());
        assertEquals(EMAIL, interpreter.getEmail());
        assertEquals(PHONE, interpreter.getPhoneNumber());
        assertEquals(HOUR_QUOTA_WEEK, interpreter.getHourQuotaWeek());
        assertEquals(HOUR_QUOTA_YEAR, interpreter.getHourQuotaYear());
        assertEquals(transportation, interpreter.getTransportMode());
    }

    @Test
    void testConstructeur_quotaSemainNegatif_leveIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Interpreter(2, "test", "Test", "User",
                        LocalDate.of(1990, 1, 1), "pwd",
                        "test@test.be", "0400000000",
                        -1, 500, transportation,
                        null, null, null, null, null)
        );
    }

    @Test
    void testConstructeur_quotaAnneeNegatif_leveIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Interpreter(3, "test2", "Test", "User",
                        LocalDate.of(1990, 1, 1), "pwd",
                        "test2@test.be", "0400000001",
                        35, -1, transportation,
                        null, null, null, null, null)
        );
    }

    @Test
    void testConstructeur_quotasSemainEtAnneeNegatifs_leveIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Interpreter(4, "test3", "Test", "User",
                        LocalDate.of(1990, 1, 1), "pwd",
                        "test3@test.be", "0400000002",
                        -5, -10, transportation,
                        null, null, null, null, null)
        );
    }

    @Test
    void testConstructeur_quotaZero_estValide() {
        assertDoesNotThrow(() ->
                new Interpreter(5, "test4", "Test", "User",
                        LocalDate.of(1990, 1, 1), "pwd",
                        "test4@test.be", "0400000003",
                        0, 0, transportation,
                        null, null, null, null, null)
        );
    }

    // --- Tests des setters avec validation ---

    @Test
    void testSetHourQuotaWeek_valeurValide_metsAJour() {
        interpreter.setHourQuotaWeek(40);
        assertEquals(40, interpreter.getHourQuotaWeek());
    }

    @Test
    void testSetHourQuotaWeek_valeurNegative_leveIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                interpreter.setHourQuotaWeek(-1)
        );
    }

    @Test
    void testSetHourQuotaWeek_zero_estValide() {
        assertDoesNotThrow(() -> interpreter.setHourQuotaWeek(0));
        assertEquals(0, interpreter.getHourQuotaWeek());
    }

    @Test
    void testSetHourQuotaYear_valeurValide_metsAJour() {
        interpreter.setHourQuotaYear(600);
        assertEquals(600, interpreter.getHourQuotaYear());
    }

    @Test
    void testSetHourQuotaYear_valeurNegative_leveIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                interpreter.setHourQuotaYear(-1)
        );
    }

    // --- Tests des getters/setters généraux ---

    @Test
    void testGetSetTransportMode() {
        Transportation newTransport = new Transportation(2, "Bus");
        interpreter.setTransportMode(newTransport);
        assertEquals(newTransport, interpreter.getTransportMode());
    }

    @Test
    void testGetSetLocation() {
        // Location est null par défaut dans ce test
        assertNull(interpreter.getLocation());
    }

    @Test
    void testBeneficiaries_estNullApresConstruction() {
        // beneficiaries est initialisé à null pour briser les dépendances circulaires
        assertNull(interpreter.getBeneficiaries());
    }

    @Test
    void testMissions_estNullApresConstruction() {
        // missions est initialisé à null pour briser les dépendances circulaires
        assertNull(interpreter.getMissions());
    }

    @Test
    void testSetBeneficiaries_metsAJourListe() {
        List<Beneficiary> beneficiaries = new ArrayList<>();
        interpreter.setBeneficiaries(beneficiaries);
        assertNotNull(interpreter.getBeneficiaries());
        assertTrue(interpreter.getBeneficiaries().isEmpty());
    }

    @Test
    void testGetSetAcademicSkills() {
        List<AcademicSkill> skills = new ArrayList<>();
        interpreter.setAcademicSkills(skills);
        assertEquals(skills, interpreter.getAcademicSkills());
    }

    @Test
    void testGetSetJobSkills() {
        List<JobSkill> skills = new ArrayList<>();
        interpreter.setJobSkills(skills);
        assertEquals(skills, interpreter.getJobSkills());
    }

    // --- Tests de equals ---

    @Test
    void testEquals_memeObjet_retourneVrai() {
        assertEquals(interpreter, interpreter);
    }

    @Test
    void testEquals_objetNonInterpreter_retourneFaux() {
        assertNotEquals(interpreter, "une chaine de caracteres");
    }

    @Test
    void testEquals_null_retourneFaux() {
        assertNotEquals(interpreter, null);
    }

    @Test
    void testEquals_deuxInterpretersIdentiques_retourneVrai() {
        // Utilise les mêmes références pour les champs String et LocalDate
        // afin de satisfaire la comparaison == dans AppliUser.equals
        Interpreter autre = new Interpreter(
                ID, LOGIN, FIRST_NAME, LAST_NAME,
                BIRTH_DATE, HASHED_PASSWORD, EMAIL, PHONE,
                HOUR_QUOTA_WEEK, HOUR_QUOTA_YEAR, transportation,
                new ArrayList<>(), new ArrayList<>(), null,
                new ArrayList<>(), new ArrayList<>()
        );
        assertEquals(interpreter, autre);
    }

    @Test
    void testEquals_quotaSemaineDifferent_retourneFaux() {
        Interpreter autre = new Interpreter(
                ID, LOGIN, FIRST_NAME, LAST_NAME,
                BIRTH_DATE, HASHED_PASSWORD, EMAIL, PHONE,
                99, HOUR_QUOTA_YEAR, transportation,
                new ArrayList<>(), new ArrayList<>(), null,
                new ArrayList<>(), new ArrayList<>()
        );
        assertNotEquals(interpreter, autre);
    }

    @Test
    void testEquals_quotaAnneeDifferent_retourneFaux() {
        Interpreter autre = new Interpreter(
                ID, LOGIN, FIRST_NAME, LAST_NAME,
                BIRTH_DATE, HASHED_PASSWORD, EMAIL, PHONE,
                HOUR_QUOTA_WEEK, 9999, transportation,
                new ArrayList<>(), new ArrayList<>(), null,
                new ArrayList<>(), new ArrayList<>()
        );
        assertNotEquals(interpreter, autre);
    }
}
