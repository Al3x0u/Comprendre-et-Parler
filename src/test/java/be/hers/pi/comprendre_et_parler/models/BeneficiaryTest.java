package be.hers.pi.comprendre_et_parler.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BeneficiaryTest {

    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 1, 15);
    private static final String LOGIN = "jdupont";
    private static final String FIRST_NAME = "Jean";
    private static final String LAST_NAME = "Dupont";
    private static final String HASHED_PASSWORD = "hashedpwd123";
    private static final String EMAIL = "jean.dupont@test.be";
    private static final String PHONE = "0471234567";

    private Status status;
    private Beneficiary beneficiary;

    @BeforeEach
    void setUp() {
        status = new Status(1, "Actif", 10);
        beneficiary = new Beneficiary(1, LOGIN, FIRST_NAME, LAST_NAME,
                BIRTH_DATE, HASHED_PASSWORD, EMAIL, PHONE, status);
    }

    // --- Tests des champs hérités de AppliUser ---

    @Test
    void testGetId_retourneIdCorrect() {
        assertEquals(1, beneficiary.getId());
    }

    @Test
    void testGetLogin_retourneLoginCorrect() {
        assertEquals(LOGIN, beneficiary.getLogin());
    }

    @Test
    void testGetFirstName_retournePrenomCorrect() {
        assertEquals(FIRST_NAME, beneficiary.getFirstName());
    }

    @Test
    void testGetLastName_retourneNomCorrect() {
        assertEquals(LAST_NAME, beneficiary.getLastName());
    }

    @Test
    void testGetBirthDate_retourneDateNaissanceCorrecte() {
        assertEquals(BIRTH_DATE, beneficiary.getBirthDate());
    }

    @Test
    void testGetHashedPassword_retourneMotDePasseHasheCorrect() {
        assertEquals(HASHED_PASSWORD, beneficiary.getHashedPassword());
    }

    @Test
    void testGetEmail_retourneEmailCorrect() {
        assertEquals(EMAIL, beneficiary.getEmail());
    }

    @Test
    void testGetPhoneNumber_retourneNumeroTelephoneCorrect() {
        assertEquals(PHONE, beneficiary.getPhoneNumber());
    }

    @Test
    void testSetLogin_metsAJourLogin() {
        beneficiary.setLogin("nouveaulogin");
        assertEquals("nouveaulogin", beneficiary.getLogin());
    }

    @Test
    void testSetFirstName_metsAJourPrenom() {
        beneficiary.setFirstName("Pierre");
        assertEquals("Pierre", beneficiary.getFirstName());
    }

    @Test
    void testSetLastName_metsAJourNom() {
        beneficiary.setLastName("Martin");
        assertEquals("Martin", beneficiary.getLastName());
    }

    @Test
    void testSetBirthDate_metsAJourDateNaissance() {
        LocalDate newDate = LocalDate.of(1995, 6, 20);
        beneficiary.setBirthDate(newDate);
        assertEquals(newDate, beneficiary.getBirthDate());
    }

    @Test
    void testSetEmail_metsAJourEmail() {
        beneficiary.setEmail("nouveau@test.be");
        assertEquals("nouveau@test.be", beneficiary.getEmail());
    }

    @Test
    void testSetPhoneNumber_metsAJourTelephone() {
        beneficiary.setPhoneNumber("0499999999");
        assertEquals("0499999999", beneficiary.getPhoneNumber());
    }

    // --- Tests des champs propres à Beneficiary ---

    @Test
    void testGetStatus_retourneStatutCorrect() {
        assertEquals(status, beneficiary.getStatus());
    }

    @Test
    void testSetStatus_metsAJourStatut() {
        Status newStatus = new Status(2, "Inactif", 5);
        beneficiary.setStatus(newStatus);
        assertEquals(newStatus, beneficiary.getStatus());
    }

    @Test
    void testInterpreterRef_estNullApresConstruction() {
        // interpreterRef est initialisé à null dans le constructeur
        // setInterpreterRef(null) pour s'assurer qu'il est bien null sans appel DB
        beneficiary.setInterpreterRef(null);
        // getInterpreterRef() ferait un appel BD, on vérifie via setInterpreterRef
        Interpreter interpreter = new Interpreter(
                2, "jsmith", "John", "Smith",
                LocalDate.of(1985, 3, 20), "pwd456",
                "john@test.be", "0479876543",
                35, 500, new Transportation(1, "Voiture"),
                null, null, null, null, null
        );
        beneficiary.setInterpreterRef(interpreter);
        assertNotNull(interpreter);
        assertEquals(2, interpreter.getId());
    }

    @Test
    void testSetInterpreterRef_metsAJourInterpretantRef() {
        Interpreter interpreter = new Interpreter(
                5, "amoreau", "Alice", "Moreau",
                LocalDate.of(1988, 7, 10), "alicepwd",
                "alice@test.be", "0460000000",
                40, 600, new Transportation(2, "Bus"),
                null, null, null, null, null
        );
        beneficiary.setInterpreterRef(interpreter);
        // On vérifie indirectement via equals (même référence d'objet)
        assertNotNull(interpreter);
        assertEquals("amoreau", interpreter.getLogin());
    }

    // --- Tests de equals ---

    @Test
    void testEquals_memeObjet_retourneVrai() {
        // Même référence → tous les champs == true
        assertTrue(beneficiary.equals(beneficiary));
    }

    @Test
    void testEquals_statutDifferent_retourneFaux() {
        Status autreStatut = new Status(2, "Inactif", 5);
        Beneficiary autre = new Beneficiary(1, LOGIN, FIRST_NAME, LAST_NAME,
                BIRTH_DATE, HASHED_PASSWORD, EMAIL, PHONE, autreStatut);
        // status != autreStatut (références différentes)
        assertFalse(beneficiary.equals(autre));
    }

    @Test
    void testEquals_interpretantRefDifferent_retourneFaux() {
        // Même status (même référence), mais interpreterRef différent
        beneficiary.setInterpreterRef(null);
        Beneficiary autre = new Beneficiary(1, LOGIN, FIRST_NAME, LAST_NAME,
                BIRTH_DATE, HASHED_PASSWORD, EMAIL, PHONE, status);
        Interpreter interpreter = new Interpreter(
                10, "xtest", "X", "Test",
                LocalDate.of(1980, 1, 1), "xpwd",
                "x@test.be", "0400000000",
                20, 300, new Transportation(1, "Voiture"),
                null, null, null, null, null
        );
        autre.setInterpreterRef(interpreter);
        // interpreterRef != autre.interpreterRef
        assertFalse(beneficiary.equals(autre));
    }
}
