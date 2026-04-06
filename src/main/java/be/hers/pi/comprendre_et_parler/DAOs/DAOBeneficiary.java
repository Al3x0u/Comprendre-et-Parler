package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Beneficiary;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;
import be.hers.pi.comprendre_et_parler.models.Interpreter;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.thymeleaf.standard.processor.StandardAttrprependTagProcessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOBeneficiary implements DAO<Beneficiary> {

    /**
     *
     * @param objectToInsert an object of type T to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException       if objectToInsert is already present in database
     * @throws SQLException          if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Beneficiary objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        String query = "INSERT INTO Beneficiary"



    }

    /**
     *
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Beneficiary objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {

    }

    /**
     *
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Beneficiary objectToDelete)
            throws NoSuchElementException, SQLException {

    }

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Beneficiary> findAll() throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<Beneficiary> beneficiaries = new ArrayList<>();
        String query = "SELECT login FROM Beneficiary";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            while(rs.next()){
                beneficiaries.add(findByLogin(rs.getString("login")));
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return beneficiaries;
    }

    /**
     *
     * @param idStatus represent the id of the status
     * @return a List of Beneficiary who have the id having the given idStatus
     * @throws SQLException
     */
    public List<Beneficiary> getByStatus(int idStatus) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<Beneficiary> beneficiaries = new ArrayList<>();
        String query = "SELECT * FROM Beneficiary WHERE status = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, idStatus);
            rs = stmt.executeQuery();

            while(rs.next()){
                beneficiaries.add(findByLogin(rs.getString("login")));
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return beneficiaries;
    }

    private static Beneficiary getBeneficiary(ResultSet rs)throws SQLException, NoSuchElementException{
        Beneficiary beneficiary;
        if (rs.next()) {
            beneficiary = new Beneficiary(
                    rs.getString("login"),
                    rs.getString("lastName"),
                    rs.getString("firstName"),
                    rs.getDate("birthday").toLocalDate(),
                    rs.getString("password"),
                    rs.getString("mail"),
                    rs.getString("phone"),
                    DAOStatus.findById(rs.getInt("status")),
                    DAOInterpreter.findByLogin(rs.getString("referenceInterpreter"))
            );
        } else {
            throw new NoSuchElementException();
        }
        return beneficiary;
    }

    public static Beneficiary findByLogin(String login) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        Beneficiary beneficiary;
        String query = "SELECT a.*, b.status, b.referenceInterpreter FROM AppliUser a JOIN Beneficiary b ON a.login = b.login WHERE a.login = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            beneficiary = getBeneficiary(rs);
        } finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return beneficiary;
    }

    /**
     * @param loginInterpreter the login of the reference interpreter
     * @return the list of beneficiaries whose reference interpreter has this login, empty if none
     * @throws SQLException if the database could not be reached
     */
    public static List<Beneficiary> findAllReferenceInterpreter(String loginInterpreter) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<Beneficiary> beneficiaries = new ArrayList<>();
        String query = "SELECT *, b.status, b.referenceInterpreter FROM AppliUser a JOIN Beneficiary b ON a.login = b.login WHERE b.referenceInterpreter = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, loginInterpreter);
            rs = stmt.executeQuery();
            Interpreter interpreter = DAOInterpreter.findByLogin(loginInterpreter);
            while(rs.next()){
                beneficiaries.add(findByLogin(rs.getString("beneficiary")));
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return beneficiaries;
    }

    public static List<Beneficiary> findByIdBeneficiariesMission(int missionId)throws SQLException{
        Connection connection = DatabaseConnector.getConnection();
        String query = "SELECT beneficiary FROM BeneficiaryMission WHERE mission = ?";
        List<Beneficiary> list = new ArrayList<>();

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, missionId);
            rs = stmt.executeQuery();

            while(rs.next()){
               list.add(findByLogin(rs.getString("beneficiary")));
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return list;
    }
}