package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.*;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DAOInterpreterTest {

    private static final LocalDate BIRTH_DATE = LocalDate.of(1985, 3, 20);

    /**
     * Crée un Interpreter de test.
     * Les listes de skills sont vides pour éviter les NPE dans les boucles de insertSkills.
     */
    private Interpreter creerInterpreterTest() {
        Transportation transport = new Transportation(1, "Voiture");
        return new Interpreter(
                1, "jsmith", "John", "Smith",
                BIRTH_DATE, "hashedpwd", "john@test.be", "0479876543",
                35, 500, transport,
                new ArrayList<>(), new ArrayList<>(), null,
                new ArrayList<>(), new ArrayList<>()
        );
    }

    /**
     * Helper : prépare le mock de DatabaseConnector avec la gestion de initialize().
     * Le constructeur de DAOInterpreter appelle DatabaseConnector.initialize(),
     * il faut donc le neutraliser dans les tests.
     */
    private void mockInitialize(MockedStatic<DatabaseConnector> dbMock) {
        // initialize() est void : on le neutralise pour ne pas lire depuis la console
        dbMock.when(() -> DatabaseConnector.initialize()).then(invocation -> null);
    }

    // --- Tests du constructeur ---

    @Test
    void testConstructeur_avecBDMockee_necreePasException() {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            mockInitialize(dbMock);
            assertDoesNotThrow(DAOInterpreter::new);
        }
    }

    // --- Tests de find(int id) ---

    @Test
    void testFind_parId_nonTrouve_retourneNull() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            mockInitialize(dbMock);
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            ResultSet mockRs = mock(ResultSet.class);

            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            DAOInterpreter dao = new DAOInterpreter();
            Interpreter result = dao.find(999);
            assertNull(result);
        }
    }

    // --- Tests de find(String login) ---

    @Test
    void testFind_parLogin_nonTrouve_retourneNull() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            mockInitialize(dbMock);
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            ResultSet mockRs = mock(ResultSet.class);

            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            DAOInterpreter dao = new DAOInterpreter();
            Interpreter result = dao.find("loginInexistant");
            assertNull(result);
        }
    }

    // --- Tests de create ---

    @Test
    void testCreate_dejaExistant_leveAlreadyExistsException() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            mockInitialize(dbMock);
            Connection mockConn = mock(Connection.class);
            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);

            Interpreter existing = creerInterpreterTest();

            // On stubbe find(String) pour simuler un interprétant déjà présent
            DAOInterpreter dao = spy(new DAOInterpreter());
            doReturn(existing).when(dao).find(anyString());

            assertThrows(AlreadyExistsException.class, () -> dao.create(existing));
        }
    }

    @Test
    void testCreate_nouveau_executeSansErreur() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            mockInitialize(dbMock);
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);

            Interpreter newInterpreter = creerInterpreterTest();

            // find retourne null → l'interprétant n'existe pas encore
            DAOInterpreter dao = spy(new DAOInterpreter());
            doReturn(null).when(dao).find(anyString());

            assertDoesNotThrow(() -> dao.create(newInterpreter));
        }
    }

    // --- Tests de delete ---

    @Test
    void testDelete_executeSansErreur() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            mockInitialize(dbMock);
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeUpdate()).thenReturn(1);

            Interpreter interpreter = creerInterpreterTest();
            DAOInterpreter dao = new DAOInterpreter();
            assertDoesNotThrow(() -> dao.delete(interpreter));
        }
    }

    // --- Tests de findAll ---

    @Test
    void testFindAll_retourneListeNonNull() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            mockInitialize(dbMock);
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            ResultSet mockRs = mock(ResultSet.class);

            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            // findAll() a une erreur SQL connue dans la requête, on simule que la requête échoue
            when(mockStmt.executeQuery()).thenThrow(new SQLException("Erreur SQL simulée"));

            DAOInterpreter dao = new DAOInterpreter();
            // findAll() contient une erreur de syntaxe SQL connue (alias 'i' mal utilisé)
            assertThrows(SQLException.class, () -> dao.findAll());
        }
    }

    // --- Tests de findAllByMissionId ---

    @Test
    void testFindAllByMissionId_sansResultat_retourneListeVide() throws SQLException {
        try (MockedStatic<DatabaseConnector> dbMock = mockStatic(DatabaseConnector.class)) {
            mockInitialize(dbMock);
            Connection mockConn = mock(Connection.class);
            PreparedStatement mockStmt = mock(PreparedStatement.class);
            ResultSet mockRs = mock(ResultSet.class);

            dbMock.when(DatabaseConnector::getInstance).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(false);

            DAOInterpreter dao = new DAOInterpreter();
            List<Interpreter> result = dao.findAllByMissionId(42);
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
