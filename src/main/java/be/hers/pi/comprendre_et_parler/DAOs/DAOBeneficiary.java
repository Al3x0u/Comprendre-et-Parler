package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Beneficiary;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.models.*;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;
import org.thymeleaf.standard.processor.StandardAttrprependTagProcessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOBeneficiary implements DAO<Beneficiary> {
    protected static final String TABLE = "Beneficiary";
    protected static final String TABLE_APPLIUSER = "AppliUser";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_LOGIN = "login";
    protected static final String FIELD_FIRST_NAME = "firstName";
    protected static final String FIELD_LAST_NAME = "lastName";
    protected static final String FIELD_BIRTH_DATE = "birthDate";
    protected static final String FIELD_HASHED_PASSWORD = "hashedPassword";
    protected static final String FIELD_EMAIL = "email";
    protected static final String FIELD_PHONE_NUMBER = "phoneNumber";
    protected static final String FIELD_INTERPRETER_REFERENCE = "referenceInterpreter";
    protected static final String FIELD_STATUS = "status";



    @Override
    public Beneficiary find(int id) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();

        String query = "SELECT %s FROM %s WHERE %s = ?";
        query = String.format(query, FIELD_LOGIN, TABLE, FIELD_ID);

        PreparedStatement statement = null;
        ResultSet result = null;
        Beneficiary beneficiary;
        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();

            if(result.next()){
                beneficiary = find(result.getString(FIELD_LOGIN));
            }else{
                throw new NoSuchElementException();
            }
        }finally {
            if(result != null){
                result.close();
            }
            if(statement != null){
                statement.close();
            }
        }
        return beneficiary;
    }

    /**
     * Search for a Beneficiary in the database with the String parameter
     * @param login the login of the object to find in database
     * @return the object identified by login in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Beneficiary find(String login) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();

        String query = "SELECT a.*, b.%s, b.%s FROM %s a JOIN %s b ON a.%s = b.%s WHERE %s = ?";
        query = String.format(query, FIELD_STATUS, FIELD_INTERPRETER_REFERENCE, TABLE, TABLE_APPLIUSER, TABLE, FIELD_LOGIN, FIELD_LOGIN, FIELD_LOGIN);

        PreparedStatement statement = null;
        ResultSet result = null;
        Beneficiary beneficiary;
        try{
            statement = connection.prepareStatement(query);
            statement.setString(1, FIELD_LOGIN);
            result = statement.executeQuery();

            if(result.next()){
                beneficiary = new Beneficiary(
                        result.getInt(FIELD_ID),
                        result.getString(FIELD_LOGIN),
                        result.getString(FIELD_FIRST_NAME),
                        result.getString(FIELD_LAST_NAME),
                        result.getDate(FIELD_BIRTH_DATE).toLocalDate(),
                        result.getString(FIELD_HASHED_PASSWORD),
                        result.getString(FIELD_EMAIL),
                        result.getString(FIELD_PHONE_NUMBER),
                        new DAOStatus().findById(result.getInt(FIELD_STATUS)),
                        null
                );
            }else{
                throw new NoSuchElementException();
            }
        }finally {
            if(result != null){
                result.close();
            }
            if(statement != null){
                statement.close();
            }
        }
        return beneficiary;
    }

    /**
     * Insert a Beneficiary object in the database
     * @param objectToInsert an object of type Beneficiary to add to the database
     * @throws AlreadyExistsException       if objectToInsert is already present in database
     * @throws SQLException          if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Beneficiary objectToInsert)
            throws AlreadyExistsException, SQLException {

    }

    /**
     * Update a Beneficiary line who already exist in the database
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
     * Delete a Beneficiary line in the TABLE in the database
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
     * Return all line of Beneficiary TABLE in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Beneficiary> findAll() throws SQLException {
        return List.of();
    }

    /**
     * Return all Beneficiary referenced by the interpreter with the given id
     * @param idInterpreter represent the id of the interpreter which we want the beneficiary
     * @return a List of Beneficiary which are referenced by the interpreter who have the idInterpreter, or null if no beneficiaries
     * @throws NoSuchElementException if the idInterpreter doesn't correspond to a existent interpreter
     */
    public List<Beneficiary> findReferencedBeneficiaries(String idInterpreter) throws SQLException, NoSuchElementException {
        Connection connection = DatabaseConnector.getInstance();
        List<Beneficiary> beneficiaries = new ArrayList<>();
        String query = "SELECT " + FIELD_LOGIN + " FROM " + TABLE;

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            while(rs.next()){
                beneficiaries.add(find(rs.getString(FIELD_LOGIN)));
            }
        }finally {
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
        return beneficiaries;
    }

    /**
     * Return all Beneficiary having the given status
     * @param idStatus represent the id of the status
     * @return a List of Beneficiary who have the id having the given idStatus
     * @throws SQLException
     * @return a List of Beneficiary who have the id having the given idStatus,or null if no or null if no beneficiaries having this Status
     */
    public List<Beneficiary> getByStatus(int idStatus) throws NoSuchElementException {
        return null;
    }
}