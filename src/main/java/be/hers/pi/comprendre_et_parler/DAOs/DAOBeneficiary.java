package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.HashSet;


public class DAOBeneficiary extends DAO<Beneficiary> {
    protected static final String TABLE_VIEW = "Beneficiary";
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

    /**
     * Check if an object already exists in the database
     * @param objectToCheck the object to check
     * @return true if the object already exists, else false
     * @throws SQLException if the database could not be reached
     */
    protected boolean checkAlreadyExists(Beneficiary objectToCheck) throws SQLException{
        boolean exists = false;
        Connection connection = DatabaseConnector.getInstance();
        String query = "SELECT COUNT(*) FROM " + TABLE_VIEW + " WHERE " +
                FIELD_LOGIN + " = ? AND " + FIELD_FIRST_NAME + " = ? AND "
                + FIELD_LAST_NAME + " = ? AND " + FIELD_BIRTH_DATE + " = ? AND "
                + FIELD_HASHED_PASSWORD + " = ? AND " + FIELD_EMAIL + " = ? AND "
                + FIELD_PHONE_NUMBER + " = ? AND " + FIELD_STATUS + " = ? AND "
                + FIELD_INTERPRETER_REFERENCE + " = ? AND " + FIELD_ID + " != ?";

        ResultSet result = null;
        PreparedStatement statement = null;
        try{
            statement = connection.prepareStatement(query);
            statement.setString(1, objectToCheck.getLogin());
            statement.setString(2, objectToCheck.getFirstName());
            statement.setString(3, objectToCheck.getLastName());
            statement.setDate(4, Date.valueOf(objectToCheck.getBirthDate()));
            statement.setString(5, objectToCheck.getHashedPassword());
            statement.setString(6, objectToCheck.getEmail());
            statement.setString(7, objectToCheck.getPhoneNumber());
            statement.setInt(8, objectToCheck.getStatus().getId());
            statement.setInt(9, objectToCheck.getInterpreterRef().getId());
            statement.setInt(10, objectToCheck.getId());
            result = statement.executeQuery();

            if(result.next()){
                exists = result.getInt(1) > 0;
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return exists;
    }

    /**
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Beneficiary find(int id) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();

        String query = "SELECT * FROM %s WHERE %s = ?";
        query = String.format(query, TABLE_VIEW, FIELD_ID);

        PreparedStatement statement = null;
        ResultSet result = null;
        Beneficiary beneficiary = null;
        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();

            if(result.next()){
                beneficiary = getResult(result);
            }else{
                throw new NoSuchElementException();
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiary;
    }

    /**
     * Populates a Beneficiary object from the current row of the given ResultSet.
     * Fetches the associated Status and reference Interpreter from the database
     * using their respective DAOs.
     * @param beneficiary the Beneficiary object to populate
     * @param result      the ResultSet positioned on the row to read, must not be null
     * @throws SQLException if a database access error occurs while reading the ResultSet
     */
    public Beneficiary getResult(ResultSet result)throws SQLException{
        return new Beneficiary(
                result.getInt(FIELD_ID),
                result.getString(FIELD_LOGIN),
                result.getString(FIELD_FIRST_NAME),
                result.getString(FIELD_LAST_NAME),
                result.getDate(FIELD_BIRTH_DATE).toLocalDate(),
                result.getString(FIELD_HASHED_PASSWORD),
                result.getString(FIELD_EMAIL),
                result.getString(FIELD_PHONE_NUMBER),
                new DAOStatus().find(result.getInt(FIELD_STATUS)),
                new DAOInterpreter().find(result.getInt(FIELD_INTERPRETER_REFERENCE))
        );
    }

    /**
     * Search for a Beneficiary in the database with the String parameter
     * @param login the login of the object to find in database
     * @return the object identified by login in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Beneficiary find(String login) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();

        String query = "SELECT * FROM " + TABLE_VIEW + " WHERE " + FIELD_LOGIN + " = ?";

        PreparedStatement statement = null;
        ResultSet result = null;
        Beneficiary beneficiary = null;
        try{
            statement = connection.prepareStatement(query);
            statement.setString(1, login);
            result = statement.executeQuery();
            if(result.next()){
                beneficiary = getResult(result);
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiary;
    }

    /**
     * @param objectToInsert an object of type T to add to the database
     * @post objectToInsert has been added to the database, and the change was commited
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the insertion failed for any other reason
     */
    @Override
    public void create(Beneficiary objectToInsert)
            throws AlreadyExistsException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String query = "INSERT INTO " + TABLE_VIEW + " (" + FIELD_LOGIN + ", " +
                FIELD_FIRST_NAME + ", " + FIELD_LAST_NAME + ", " + FIELD_BIRTH_DATE + ", " +
                FIELD_HASHED_PASSWORD + ", " + FIELD_EMAIL + ", " + FIELD_PHONE_NUMBER + ", " +
                FIELD_STATUS + ", " + FIELD_INTERPRETER_REFERENCE + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";


        ResultSet rs = null;
        if(find(objectToInsert.getLogin()) != null){
            throw new AlreadyExistsException("Object already exists in database");
        }
        PreparedStatement statement = null;

        try{
            statement = connection.prepareStatement(query,  new String[]{FIELD_ID});

            statement.setString(1, objectToInsert.getLogin());
            statement.setString(2, objectToInsert.getFirstName());
            statement.setString(3, objectToInsert.getLastName());
            statement.setDate(4, Date.valueOf(objectToInsert.getBirthDate()));
            statement.setString(5, objectToInsert.getHashedPassword());
            statement.setString(6, objectToInsert.getEmail());
            statement.setString(7, objectToInsert.getPhoneNumber());
            statement.setInt(8, objectToInsert.getStatus().getId());
            statement.setInt(9, objectToInsert.getInterpreterRef().getId());
            statement.executeUpdate();

            rs = statement.getGeneratedKeys();
            if (rs.next()) {
                objectToInsert.setId(rs.getInt(1));
            }
        }finally {
            closeResultSet(rs);
            closeStatement(statement);
        }
    }

    /**
     * @param objectToUpdate the object to edit in the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws AlreadyExistsException if an object with a different id but otherwise identical fields already exists in database
     * @throws SQLException if the update failed for any other reason
     */
    @Override
    public void update(Beneficiary objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String query = "UPDATE " + TABLE_VIEW + " SET " + FIELD_LOGIN + " = ?, " +
                FIELD_FIRST_NAME + " = ?, " + FIELD_LAST_NAME + " = ?, " + FIELD_BIRTH_DATE + " = ?, " +
                FIELD_HASHED_PASSWORD + " = ?, " + FIELD_EMAIL + " = ?, " + FIELD_PHONE_NUMBER + " = ?, " +
                FIELD_STATUS + " = ?, " + FIELD_INTERPRETER_REFERENCE + " = ? WHERE " + FIELD_ID + " = ? ";
        PreparedStatement statement = null;
        int rowsAffected = 0;

        if(checkAlreadyExists(objectToUpdate)){
            throw new AlreadyExistsException("The beneficiary already exists in database.");
        }

        try {
            statement.setString(1, objectToUpdate.getLogin());
            statement.setString(2, objectToUpdate.getFirstName());
            statement.setString(3, objectToUpdate.getLastName());
            statement.setDate(4, Date.valueOf(objectToUpdate.getBirthDate()));
            statement.setString(5, objectToUpdate.getHashedPassword());
            statement.setString(6, objectToUpdate.getEmail());
            statement.setString(7, objectToUpdate.getPhoneNumber());
            statement.setInt(8, objectToUpdate.getStatus().getId());
            statement.setInt(9, objectToUpdate.getInterpreterRef().getId());
            statement.setInt(10, objectToUpdate.getId());
            rowsAffected = statement.executeUpdate();

            if(rowsAffected < 1){
                throw new NoSuchElementException("[ERROR] There is no user with the id " + objectToUpdate.getId() + ".");
            }
        }finally {
            closeStatement(statement);
        }
    }

    /**
     *
     * @param objectToDelete the object to delete in the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the deletion failed for any other reason
     */
    @Override
    public void delete(Beneficiary objectToDelete) throws NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String query = "DELETE FROM " + TABLE_VIEW + " WHERE " + FIELD_ID + " = ? ";
        int rowsAffected = 0;
        PreparedStatement statement = null;
         try{
             statement = connection.prepareStatement(query);
             statement.setInt(1, objectToDelete.getId());
             rowsAffected = statement.executeUpdate();

             if(rowsAffected  < 1){
                 throw new NoSuchElementException("[ERROR] There is no user with the id " + objectToDelete.getId() + ".");
             }
         }finally {
             closeStatement(statement);
         }
    }

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty Set)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Set<Beneficiary> findAll() throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String query = "SELECT *  FROM " + TABLE_VIEW;

        Set<Beneficiary> beneficiaries = new HashSet<>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try{
            statement = connection.prepareStatement(query);
            result = statement.executeQuery();

            while(result.next()){
                beneficiaries.add(getResult(result));
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiaries;
    }

    /**
     * Return all Beneficiary referenced by the interpreter with the given id
     * @param idInterpreter represent the id of the interpreter which we want the beneficiary
     * @return a set of Beneficiary which are referenced by the interpreter who have the idInterpreter, or an empty set if no beneficiaries
     * @throws NoSuchElementException if the idInterpreter doesn't correspond to an existent interpreter
     * @throws SQLException if the database could not be reached
     */
    public Set<Beneficiary> findReferencedBeneficiaries(int idInterpreter) throws SQLException, NoSuchElementException {
        Connection connection = DatabaseConnector.getInstance();
        Set<Beneficiary> beneficiaries = new HashSet<>();
        String query = "SELECT * FROM " + TABLE_VIEW + " WHERE " + FIELD_INTERPRETER_REFERENCE + " = ?";

        if(new DAOInterpreter().find(idInterpreter) == null ){
            throw new NoSuchElementException("[ERROR] There is no interpreter with the id " + idInterpreter);
        }

        PreparedStatement statement = null;
        ResultSet result = null;
        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, idInterpreter);
            result = statement.executeQuery();

            while(result.next()){
                beneficiaries.add(getResult(result));
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiaries;
    }

    /**
     * Return all Beneficiary having the given status
     * @param idStatus represent the id of the status
     * @throws SQLException if the database could not be reached
     * @throws NoSuchElementException if the idStatus doesn't correspond to a existent Status
     * @return a Set of Beneficiary who have the id having the given idStatus,or an empty Set if no beneficiaries having this Status
     */
    public Set<Beneficiary> getByStatus(int idStatus) throws SQLException, NoSuchElementException {
        Connection connection = DatabaseConnector.getInstance();
        Set<Beneficiary> beneficiaries = new HashSet<>();
        String query = "SELECT * FROM " + TABLE_VIEW + " WHERE " + FIELD_STATUS + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        if(new DAOInterpreter().find(idStatus) == null ){
            throw new NoSuchElementException("[ERROR] There is no status with the id " + idStatus);
        }
        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, idStatus);
            result = statement.executeQuery();

            while(result.next()){
                beneficiaries.add(getResult(result));
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return beneficiaries;
    }

}