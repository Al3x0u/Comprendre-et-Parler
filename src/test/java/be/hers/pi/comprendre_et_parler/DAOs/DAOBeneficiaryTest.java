package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.Beneficiary;
import be.hers.pi.comprendre_et_parler.models.Status;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DAOBeneficiaryTest {

    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 1, 15);

    private Beneficiary creerBeneficiaireTest() {
        Status status = new Status(1, "Actif", 10);
        return new Beneficiary(1, "jdupont", "Jean", "Dupont",
                BIRTH_DATE, "hashedpwd", "jean@test.be", "0471234567", status);
    }

    // --- Tests de find(int id) ---

    @Test
    void testFind_parId_nonTrouve_leveNoSuchElementException() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            ResultSet mockRs = mock(ResultSet.class);

            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            DAOBeneficiary dao = new DAOBeneficiary();
            assertThrows(NoSuchElementException.class, () -> dao.find(999));
        }
    }

    @Test
    void testFind_parId_trouvé_retourneBeneficiary() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            Connection mockConn = mock(Connection.class);
            // stmt1 : SELECT login FROM AppliUser WHERE id = ?
            PreparedStatement mockStmt1 = mock(PreparedStatement.class);
            // stmt2 : SELECT a.*, b.status FROM AppliUser JOIN Beneficiary ...
            PreparedStatement mockStmt2 = mock(PreparedStatement.class);
            // stmt3 : SELECT * FROM Status WHERE id = ? (appelé par DAOStatus.findById)
            PreparedStatement mockStmt3 = mock(PreparedStatement.class);
            ResultSet mockRs1 = mock(ResultSet.class);
            ResultSet mockRs2 = mock(ResultSet.class);
            ResultSet mockRs3 = mock(ResultSet.class);

            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString()))
                    .thenReturn(mockStmt1, mockStmt2, mockStmt3);
            when(mockStmt1.executeQuery()).thenReturn(mockRs1);
            when(mockStmt2.executeQuery()).thenReturn(mockRs2);
            when(mockStmt3.executeQuery()).thenReturn(mockRs3);

            // Résultat de la requête id → login
            when(mockRs1.next()).thenReturn(true);
            when(mockRs1.getString("login")).thenReturn("jdupont");

            // Résultat de la requête login → beneficiary
            when(mockRs2.next()).thenReturn(true);
            when(mockRs2.getInt("id")).thenReturn(1);
            when(mockRs2.getString("login")).thenReturn("jdupont");
            when(mockRs2.getString("firstName")).thenReturn("Jean");
            when(mockRs2.getString("lastName")).thenReturn("Dupont");
            when(mockRs2.getDate("birthDate")).thenReturn(Date.valueOf(BIRTH_DATE));
            when(mockRs2.getString("hashedPassword")).thenReturn("hashedpwd");
            when(mockRs2.getString("email")).thenReturn("jean@test.be");
            when(mockRs2.getString("phoneNumber")).thenReturn("0471234567");
            when(mockRs2.getInt("status")).thenReturn(1);

            // Résultat de la requête DAOStatus.findById
            when(mockRs3.next()).thenReturn(true);
            when(mockRs3.getInt("id")).thenReturn(1);
            when(mockRs3.getString("designation")).thenReturn("Actif");
            when(mockRs3.getInt("hourQuota")).thenReturn(10);

            DAOBeneficiary dao = new DAOBeneficiary();
            Beneficiary result = dao.find(1);

            assertNotNull(result);
            assertEquals("jdupont", result.getLogin());
            assertEquals("Jean", result.getFirstName());
            assertEquals("Dupont", result.getLastName());
        }
    }

    // --- Tests de find(String login) ---

    @Test
    void testFind_parLogin_nonTrouve_retourneNull() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            ResultSet mockRs = mock(ResultSet.class);

            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            DAOBeneficiary dao = new DAOBeneficiary();
            Beneficiary result = dao.find("loginInexistant");
            assertNull(result);
        }
    }

    // --- Tests de create ---

    @Test
    void testCreate_dejaExistant_leveAlreadyExistsException() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            Connection mockConn = mock(Connection.class);
            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);

            Beneficiary existing = creerBeneficiaireTest();

            // On stubble find(String) pour simuler un bénéficiaire déjà présent
            DAOBeneficiary dao = spy(new DAOBeneficiary());
            doReturn(existing).when(dao).find(anyString());

            assertThrows(AlreadyExistsException.class, () -> dao.create(existing));
        }
    }

    // --- Tests de delete ---

    @Test
    void testDelete_nonTrouve_leveNoSuchElementException() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            Connection mockConn = mock(Connection.class);
            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);

            Beneficiary beneficiary = creerBeneficiaireTest();

            // find(int id) lève NoSuchElementException si non trouvé
            DAOBeneficiary dao = spy(new DAOBeneficiary());
            doThrow(new NoSuchElementException()).when(dao).find(anyInt());

            assertThrows(NoSuchElementException.class, () -> dao.delete(beneficiary));
        }
    }

    @Test
    void testDelete_trouve_executeSanErreur() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);

            Beneficiary beneficiary = creerBeneficiaireTest();

            DAOBeneficiary dao = spy(new DAOBeneficiary());
            doReturn(beneficiary).when(dao).find(anyInt());

            assertDoesNotThrow(() -> dao.delete(beneficiary));
        }
    }

    // --- Tests de findAll ---

    @Test
    void testFindAll_retourneListeNonNull() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            ResultSet mockRs = mock(ResultSet.class);

            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            DAOBeneficiary dao = new DAOBeneficiary();
            List<Beneficiary> result = dao.findAll();
            assertNotNull(result);
        }
    }

    // --- Tests de findReferencedBeneficiaries ---

    @Test
    void testFindReferencedBeneficiaries_sansResultat_retourneListeVide() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            ResultSet mockRs = mock(ResultSet.class);

            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            DAOBeneficiary dao = new DAOBeneficiary();
            List<Beneficiary> result = dao.findReferencedBeneficiaries("1");
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
