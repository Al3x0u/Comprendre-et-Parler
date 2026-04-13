package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Beneficiary;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.models.*;
import com.sun.jdi.request.ClassPrepareRequest;
import oracle.jdbc.proxy.annotation.Pre;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;
import org.thymeleaf.standard.processor.StandardAttrprependTagProcessor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.HashMap;
import java.util.Map;


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
        query = String.format(query, FIELD_LOGIN, TABLE_APPLIUSER, FIELD_ID);

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

        String query = "SELECT a.*, b." + FIELD_STATUS + " FROM "
                + TABLE_APPLIUSER + " a JOIN " + TABLE + " b ON a."
                + FIELD_ID + " = b." + FIELD_ID + " WHERE a." + FIELD_LOGIN + " = ?";

        PreparedStatement statement = null;
        ResultSet result = null;
        Beneficiary beneficiary = null;
        try{
            statement = connection.prepareStatement(query);
            statement.setString(1, login);
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
                        new DAOStatus().findById(result.getInt(FIELD_STATUS))
                );
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
     * Utility method to Insert a AppliUser object in the database
     * @param objectToInsert the Beneficiary object that contains the information for the AppliUser table in database
     * @param connection the connection object to connect to the database
     * @return the id of the AppliUser user inserted
     * @throws SQLException if the database could not be reached
     */
    private int insertAppliUser(Beneficiary objectToInsert, Connection connection) throws SQLException{
        String query = "INSERT INTO " + TABLE_APPLIUSER + " (" + FIELD_LOGIN + ", "
                + FIELD_LAST_NAME + ", " + FIELD_FIRST_NAME + ", "
                + FIELD_BIRTH_DATE + ", " + FIELD_HASHED_PASSWORD + ", "
                + FIELD_EMAIL + ", " + FIELD_PHONE_NUMBER + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;

        try{
            statement = connection.prepareStatement(query, new String[]{FIELD_ID});
            statement.setString(1, objectToInsert.getLogin());
            statement.setString(2, objectToInsert.getLastName());
            statement.setString(3, objectToInsert.getFirstName());
            statement.setDate(4, Date.valueOf(objectToInsert.getBirthDate()));
            statement.setString(5, objectToInsert.getHashedPassword());
            statement.setString(6, objectToInsert.getEmail());
            statement.setString(7, objectToInsert.getPhoneNumber());
            statement.executeQuery();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if(generatedKeys.next()){
                return generatedKeys.getInt(1);
            }
            throw new SQLException("Insert AppliUser échoué : aucun id généré");
        }finally {
            if(statement != null){
                statement.close();
            }
        }
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
        Connection connection = DatabaseConnector.getInstance();
        String query = "INSERT INTO " + TABLE + " (" + FIELD_ID + ", " + FIELD_STATUS + ", " + FIELD_INTERPRETER_REFERENCE + ") VALUES (?, ?, ?)";
        int rowsAffected = 0;
        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            if(find(objectToInsert.getLogin()) != null){
                throw new AlreadyExistsException();
            }

            int idAppluser = insertAppliUser(objectToInsert, connection);
            statement = connection.prepareStatement(query);
            statement.setInt(1, idAppluser);
            statement.setInt(2, objectToInsert.getStatus().getId());
            statement.setInt(3, objectToInsert.getInterpreterRef().getId());
            rowsAffected = statement.executeUpdate();
        }finally {
            if(statement != null){
                statement.close();
            }
            if(result != null){
                result.close();
            }
        }
    }

    /**
     * Utility method to Update a column in the AppliUser table in database
     * @param objectToUpdate the Beneficiary object that contains the information for the AppliUser table in database
     * @param connection the connection object to connect to the database
     * @throws SQLException if the database could not be reached
     */
    private void updateAppliUser(Beneficiary objectToUpdate, Connection connection) throws SQLException, NoSuchElementException{
        String query = "UPDATE " + TABLE_APPLIUSER + " SET " + FIELD_LOGIN
                + " = ?, " + FIELD_FIRST_NAME + " = ?, " + FIELD_LAST_NAME + " = ?, "
                + FIELD_BIRTH_DATE + " = ?, " + FIELD_HASHED_PASSWORD + " = ?, "
                + FIELD_EMAIL + " = ?, " + FIELD_PHONE_NUMBER + " = ? WHERE "
                + FIELD_ID + " = ? ";
        PreparedStatement statement = null;

        try{
            if(find(objectToUpdate.getId()) == null){
                throw new NoSuchElementException("[ERREUR] Aucun utilisateur n'a l'identifiant " + objectToUpdate.getId() + ".");
            }
            statement = connection.prepareStatement(query);
            statement.setString(1, objectToUpdate.getLogin());
            statement.setString(2, objectToUpdate.getFirstName());
            statement.setString(3, objectToUpdate.getLastName());
            statement.setDate(4, Date.valueOf(objectToUpdate.getBirthDate()));
            statement.setString(5, objectToUpdate.getHashedPassword());
            statement.setString(6, objectToUpdate.getEmail());
            statement.setString(7, objectToUpdate.getPhoneNumber());
            statement.setInt(8, objectToUpdate.getId());
            statement.executeUpdate();
        }finally {
            if(statement != null){
                statement.close();
            }
        }
    }

    /**
     * Update a Beneficiary line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Beneficiary objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String query = "UPDATE " + TABLE + " SET " + FIELD_STATUS + " = ?, "
                + FIELD_INTERPRETER_REFERENCE + " = ? WHERE " + FIELD_ID + " = ? ";
        PreparedStatement statement = null;

        try {
            updateAppliUser(objectToUpdate, connection);
            statement = connection.prepareStatement(query);
            statement.setInt(1, objectToUpdate.getStatus().getId());
            statement.setInt(2, objectToUpdate.getInterpreterRef().getId());
            statement.setInt(3, objectToUpdate.getId());
            statement.executeUpdate();
        }finally {
            if(statement != null){
                statement.close();
            }
        }
    }

    /**
     * Delete a Beneficiary line in the TABLE in the database
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Beneficiary objectToDelete) throws NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String query = "DELETE FROM " + TABLE_APPLIUSER + " WHERE " + FIELD_ID + " = ? ";

        PreparedStatement statement = null;
         try{
             if(find(objectToDelete.getId()) == null){
                 throw new NoSuchElementException("[ERREUR] Aucun utilisateur n'a l'identifiant " + objectToDelete.getId() + ".");
             }
             statement = connection.prepareStatement(query);
             statement.setInt(1, objectToDelete.getId());
             statement.executeUpdate();
         }finally {
             if(statement != null){
                 statement.close();
             }
         }
    }

    /**
     * Return all line of Beneficiary TABLE in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Beneficiary> findAll() throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String query = "SELECT " + FIELD_ID + " FROM " + TABLE;

        List<Beneficiary> beneficiaries = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            result = statement.executeQuery();

            while(result.next()){
                beneficiaries.add(find(result.getInt(FIELD_ID)));
            }
        }finally {
            if(result != null){
                result.close();
            }
            if(statement != null){
                statement.close();
            }
        }
        return beneficiaries;
    }

    /**
     * Return all Beneficiary linked to the given mission id
     * @param missionId represent the id of the mission
     * @return a List of Beneficiary linked to the mission, empty if none
     * @throws SQLException if the database could not be reached
     */
    public static List<Beneficiary> findByIdBeneficiariesMission(int missionId) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        List<Beneficiary> beneficiaries = new ArrayList<>();
        String query = "SELECT b." + FIELD_LOGIN + " FROM " + TABLE + " b JOIN BeneficiaryMission bm ON b."
                + FIELD_ID + " = bm.beneficiary WHERE bm.mission = ?";

        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, missionId);
            result = statement.executeQuery();

            while (result.next()) {
                beneficiaries.add(new DAOBeneficiary().find(result.getString(FIELD_LOGIN)));
            }
        } finally {
            if (result != null) {
                result.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
        return beneficiaries;
    }

    /**
     * Return all Beneficiary referenced by the interpreter with the given id
     * @param idInterpreter represent the id of the interpreter which we want the beneficiary
     * @return a List of Beneficiary which are referenced by the interpreter who have the idInterpreter, or null if no beneficiaries
     * @throws NoSuchElementException if the idInterpreter doesn't correspond to a existent interpreter
     */
    public List<Beneficiary> findReferencedBeneficiaries(int idInterpreter) throws SQLException, NoSuchElementException {
        Connection connection = DatabaseConnector.getInstance();
        List<Beneficiary> beneficiaries = new ArrayList<>();
        String query = "SELECT " + FIELD_LOGIN + " FROM " + TABLE + " WHERE " + FIELD_INTERPRETER_REFERENCE + " = ?";

        PreparedStatement statement = null;
        ResultSet result = null;
        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, idInterpreter);
            result = statement.executeQuery();

            while(result.next()){
                beneficiaries.add(find(result.getString(FIELD_LOGIN)));
            }
        }finally {
            if(result != null){
                result.close();
            }
            if(statement != null){
                statement.close();
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
    public List<Beneficiary> getByStatus(int idStatus) throws SQLException, NoSuchElementException {
        Connection connection = DatabaseConnector.getInstance();
        List<Beneficiary> beneficiaries = new ArrayList<>();
        String query = "SELECT b." + FIELD_ID + " FROM " + TABLE + " b JOIN "
                +  DAOStatus.TABLE + "s ON b." + FIELD_ID + " = s."
                + DAOStatus.FIELD_ID + " WHERE b." + FIELD_STATUS + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, idStatus);
            result = statement.executeQuery();

            while(result.next()){
                beneficiaries.add(find(result.getInt(FIELD_ID)));
            }
        }finally {
            if(result != null){
                result.close();
            }
            if(statement != null){
                statement.close();
            }
        }
        return beneficiaries;
    }

    /**
     * Get the beneficiaries and their importance for a mission via BeneficiaryMission table
     * @param missionId : id of the mission
     * @return Map of Beneficiary and their importance, or an empty Map if none was found
     * @throws SQLException if the database could not be reached
     */
    public Map<Beneficiary, Integer> getMissionBeneficiaries(int missionId) throws SQLException {
        String query = "SELECT beneficiary, importance FROM BeneficiaryMission WHERE mission = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        Map<Beneficiary, Integer> beneficiaries = new HashMap<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, missionId);
            result = statement.executeQuery();
            while (result.next())
                beneficiaries.put(find(result.getInt("beneficiary")), result.getInt("importance"));
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return beneficiaries;
    }
}