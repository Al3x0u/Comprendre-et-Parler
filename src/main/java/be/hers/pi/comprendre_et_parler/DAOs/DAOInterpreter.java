package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOInterpreter implements DAO<Interpreter> {
    protected static final String TABLE = "Interpreter";
    protected static final String TABLE_APPLIUSER = "AppliUser";
    protected static final String TABLE_ACADEMIC_SKILL_INTERPRETER = "AcademicSkillInterpreter";
    protected static final String TABLE_JOB_SKILL_INTERPRETER = "JobSkillInterpreter";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_LOGIN = "login";
    protected static final String FIELD_INTERPRETER = "interpreter";
    protected static final String FIELD_FIRST_NAME = "firstName";
    protected static final String FIELD_LAST_NAME = "lastName";
    protected static final String FIELD_BIRTH_DATE = "birthDate";
    protected static final String FIELD_HASHED_PASSWORD = "hashedPassword";
    protected static final String FIELD_EMAIL = "email";
    protected static final String FIELD_PHONE_NUMBER = "phoneNumber";
    protected static final String FIELD_WEEK_QUOTA = "weekHourlyQuota";
    protected static final String FIELD_YEAR_QUOTA = "yearHourlyQuota";
    protected static final String FIELD_TRANSPORT_MODE = "transportMode";
    protected static final String FIELD_LOCATION = "location";
    protected static final String FIELD_MISSION = "mission";
    protected static final String TABLE_INTERPRETER_MISSION = "InterpreterMission";

    /*
    * constructor for DAOInterpreter object*/
    public DAOInterpreter() {
        try{
            DatabaseConnector.initialize();
        }catch (SQLException e){
            e.printStackTrace();//most useful than an Exception
        }
    }


    /**
     * Search for an Interpreter in the database with the int parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Interpreter find(int id) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String query = "SELECT " + FIELD_LOGIN + " FROM " + TABLE + " WHERE " + FIELD_ID + " = ?";
        Interpreter interpreter = null;

        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();

            if(result.next()){
               interpreter = find(result.getString(FIELD_LOGIN));
            }
        }finally{
            if(result != null){
                result.close();
            }
            if(statement != null){
                statement.close();
            }
        }
        return interpreter;
    }

    /*int id, String login, String firstName, String lastName,
    LocalDate birthDate, String hashedPassword, String email,
    String phoneNumber, int hQW, int hQY, Transportation transportMode,
    List<AcademicSkill> academic, List<JobSkill> job, List<Beneficiary> beneficiaries,
    List<Mission> missions, Location location, List<PunctualTimeSlot> time, List<ExceptionalUnavailability> unavailability*/
    public Interpreter find(String login) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        Interpreter interpreter = null;

        String query = "SELECT a.*, i.%s, i.%s, i.%s, i.%s FROM %s a JOIN %s i " +
                "ON a.%s = i.%s WHERE a.%s = ?";
        query = String.format(query, FIELD_WEEK_QUOTA, FIELD_YEAR_QUOTA, FIELD_TRANSPORT_MODE,
                FIELD_LOCATION, TABLE_APPLIUSER, TABLE, FIELD_ID, FIELD_ID, FIELD_LOGIN);

        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            statement.setString(1, login);
            result = statement.executeQuery();

            if(result.next()){
                String interpreterId = result.getString(FIELD_ID);
                interpreter = new Interpreter(
                        login,
                        result.getString(FIELD_FIRST_NAME),
                        result.getString(FIELD_LAST_NAME),
                        result.getDate(FIELD_BIRTH_DATE).toLocalDate(),
                        result.getString(FIELD_HASHED_PASSWORD),
                        result.getString(FIELD_EMAIL),
                        result.getString(FIELD_PHONE_NUMBER),
                        result.getInt(FIELD_WEEK_QUOTA),
                        result.getInt(FIELD_YEAR_QUOTA),
                        result.getString(FIELD_TRANSPORT_MODE),
                        DAOAcademicSkill.findAllByInterpreterLogin(interpreterId),
                        DAOJobSkill.findAllByInterpreterLogin(login),
                        DAOLocation.findById(result.getInt(FIELD_LOCATION)),
                        DAOPunctualTimeSlot.findAllByInterpreterLogin(login),
                        new DAOExceptionalUnavailability().findByInterpreterLogin(result.getInt(FIELD_ID))
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
        return interpreter;
    }

    /**
     * Utility method to Insert a AppliUser object in the database
     * @param objectToInsert the Beneficiary object that contains the information for the AppliUser table in database
     * @param connection the connection object to connect to the database
     * @return the id of the AppliUser user inserted
     * @throws SQLException if the database could not be reached
     */
    private void insertAppliUser(Interpreter objectToInsert, Connection connection) throws SQLException{
        String query = "INSERT INTO " + TABLE_APPLIUSER + " (" + FIELD_LOGIN + ", "
                + FIELD_LAST_NAME + ", " + FIELD_FIRST_NAME + ", "
                + FIELD_BIRTH_DATE + ", " + FIELD_HASHED_PASSWORD + ", "
                + FIELD_EMAIL + ", " + FIELD_PHONE_NUMBER + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        int rowsAffected = 0;

        try{
            statement = connection.prepareStatement(query);
            statement.setString(1, objectToInsert.getLogin());
            statement.setString(2, objectToInsert.getLastName());
            statement.setString(3, objectToInsert.getFirstName());
            statement.setDate(4, Date.valueOf(objectToInsert.getBirthDate()));
            statement.setString(5, objectToInsert.getHashedPassword());
            statement.setString(6, objectToInsert.getEmail());
            statement.setString(7, objectToInsert.getPhoneNumber());
            rowsAffected = statement.executeUpdate();
        }finally {
            if(statement != null){
                statement.close();
            }
        }
    }

    /**
     * Utility method to Insert a ligne in the table that link Interpreter and academicSkill in database
     * @param objectToInsert the Beneficiary object that contains the information for table in database
     * @param connection the connection object to connect to the database
     * @throws SQLException if the database could not be reached
     */
    private void insertAcademicSkillInterpreter(Interpreter objectToInsert, Connection connection)throws SQLException{
        String query = "INSERT INTO " +  TABLE_ACADEMIC_SKILL_INTERPRETER +" ("
                + FIELD_ID + ", " + DAOAcademicSkill.FIELD_ID + ") VALUES(?, ?)";
        PreparedStatement statement = null;
        int rowsAffected = 0;

        try{
            statement = connection.prepareStatement(query);
            statement.setString(1,objectToInsert.getLogin());
            for(AcademicSkill element : objectToInsert.getAcademicSkills()){
                statement.setInt(2, element.getId());
                rowsAffected= statement.executeUpdate();
            }
        }finally {
            if(statement != null){
                statement.close();
            }
        }
    }

    /**
     * Utility method to Insert a ligne in the table that link Interpreter and jobSkill in database
     * @param objectToInsert the Beneficiary object that contains the information for table in database
     * @param connection the connection object to connect to the database
     * @throws SQLException if the database could not be reached
     */
    private void insertJobSkillInterpreter(Interpreter objectToInsert, Connection connection)throws SQLException{
        String query = "INSERT INTO " + TABLE_JOB_SKILL_INTERPRETER + " ("+ FIELD_LOGIN
                + ", " + DAOJobSkill.FIELD_ID + ") VALUES(?, ?)";
        PreparedStatement statement = null;
        int rowsAffected = 0;

        try{
            statement = connection.prepareStatement(query);
            statement.setString(1,objectToInsert.getLogin());
            for(JobSkill element : objectToInsert.getJobSkills()){
                statement.setInt(2, element.getId());
                rowsAffected = statement.executeUpdate();
            }
        }finally {
            if(statement != null){
                statement.close();
            }
        }
    }

    //TODO preciser que l'objet est modifié
    /**
     * Insert an Interpreter object in the database
     * @param objectToInsert an object of type Interpreter to add to the database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Interpreter objectToInsert) throws AlreadyExistsException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String query = "INSERT INTO " + TABLE + " (" + FIELD_LOGIN + ", "
                            + FIELD_WEEK_QUOTA + ", " + FIELD_YEAR_QUOTA + ", "
                            + FIELD_TRANSPORT_MODE + ") VALUES (?, ?, ?, ?)";
        PreparedStatement statement = null;
        int rowsAffected = 0;

        try{
            if (find(objectToInsert.getLogin()) != null){
                throw new AlreadyExistsException();
            }
            insertAppliUser(objectToInsert, connection);

            statement = connection.prepareStatement(query, new String[]{FIELD_ID});
            statement.setString(1, objectToInsert.getLogin());
            statement.setInt(2, objectToInsert.getHourQuotaWeek());
            statement.setInt(3, objectToInsert.getHourQuotayear());
            statement.setString(4, objectToInsert.getTransportMode());
            rowsAffected = statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next())
                objectToInsert.setId(generatedKeys.getInt(1));

            insertAcademicSkillInterpreter(objectToInsert, connection);
            insertJobSkillInterpreter(objectToInsert, connection);
        }finally {
            if(statement != null){
                statement.close();
            }
        }
    }

    //TODO AlreadyExist
    /**
     * Utility method to update the AppliUser part of an Interpreter in the database
     * @param objectToUpdate the Interpreter object that contains the information for the AppliUser table
     * @param connection the connection object to connect to the database
     * @throws NoSuchElementException if no user with objectToUpdate's id exists in the database
     * @throws SQLException if the database could not be reached
     */
    private void updateAppliUser(Interpreter objectToUpdate, Connection connection) throws NoSuchElementException, SQLException {
        String query = "UPDATE " + TABLE_APPLIUSER + " SET " + FIELD_LOGIN + " = ?, "
                + FIELD_LAST_NAME + " = ?, " + FIELD_FIRST_NAME + " = ?, "
                + FIELD_BIRTH_DATE + " = ?, " + FIELD_HASHED_PASSWORD + " = ?, "
                + FIELD_EMAIL + " = ?, " + FIELD_PHONE_NUMBER + " = ? WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;

        try {
            if (find(objectToUpdate.getId()) == null) {
                throw new NoSuchElementException("[ERREUR] Aucun interprète n'a l'identifiant " + objectToUpdate.getId() + ".");
            }
            statement = connection.prepareStatement(query);
            statement.setString(1, objectToUpdate.getLogin());
            statement.setString(2, objectToUpdate.getLastName());
            statement.setString(3, objectToUpdate.getFirstName());
            statement.setDate(4, Date.valueOf(objectToUpdate.getBirthDate()));
            statement.setString(5, objectToUpdate.getHashedPassword());
            statement.setString(6, objectToUpdate.getEmail());
            statement.setString(7, objectToUpdate.getPhoneNumber());
            statement.setInt(8, objectToUpdate.getId());
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Update an Interpreter line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Interpreter objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String queryInterpreter = "UPDATE " + TABLE + " SET " + FIELD_LOGIN + " = ?, "
                + FIELD_WEEK_QUOTA + " = ?, " + FIELD_YEAR_QUOTA + " = ?, "
                + FIELD_TRANSPORT_MODE + " = ? WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;

        try {
            updateAppliUser(objectToUpdate, connection);

            statement = connection.prepareStatement(queryInterpreter);
            statement.setString(1, objectToUpdate.getLogin());
            statement.setInt(2, objectToUpdate.getHourQuotaWeek());
            statement.setInt(3, objectToUpdate.getHourQuotayear());
            statement.setString(4, objectToUpdate.getTransportMode());
            statement.setInt(5, objectToUpdate.getId());
            statement.executeUpdate();

            deleteAcademicSkillInterpreter(objectToUpdate, connection);
            insertAcademicSkillInterpreter(objectToUpdate, connection);
            deleteJobSkillInterpreter(objectToUpdate, connection);
            insertJobSkillInterpreter(objectToUpdate, connection);
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Utility method to delete the AcademicSkillInterpreter links from the database
     * @param objectToDelete the Interpreter object
     * @param connection the connection object to connect to the database
     * @throws SQLException if the database could not be reached
     */
    private void deleteAcademicSkillInterpreter(Interpreter objectToDelete, Connection connection) throws SQLException {
        String query = "DELETE FROM " + TABLE_ACADEMIC_SKILL_INTERPRETER + " WHERE " + FIELD_INTERPRETER + " = ?";
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, objectToDelete.getLogin());
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Utility method to delete the JobSkillInterpreter links from the database
     * @param objectToDelete the Interpreter object
     * @param connection the connection object to connect to the database
     * @throws SQLException if the database could not be reached
     */
    private void deleteJobSkillInterpreter(Interpreter objectToDelete, Connection connection) throws SQLException {
        String query = "DELETE FROM " + TABLE_JOB_SKILL_INTERPRETER + " WHERE " + FIELD_INTERPRETER + " = ?";
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, objectToDelete.getLogin());
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Utility method to delete the Interpreter row from the database
     * @param objectToDelete the Interpreter object
     * @param connection the connection object to connect to the database
     * @throws SQLException if the database could not be reached
     */
    private void deleteInterpreter(Interpreter objectToDelete, Connection connection) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Utility method to delete the AppliUser row from the database
     * @param objectToDelete the Interpreter object
     * @param connection the connection object to connect to the database
     * @throws SQLException if the database could not be reached
     */
    private void deleteAppliUser(Interpreter objectToDelete, Connection connection) throws SQLException {
        String query = "DELETE FROM " + TABLE_APPLIUSER + " WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    //TODO supp delete acSkill et JOb Skill
    /**
     * Delete an Interpreter line in the table in the database
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Interpreter objectToDelete)
            throws NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getInstance();

        deleteAcademicSkillInterpreter(objectToDelete, connection);
        deleteJobSkillInterpreter(objectToDelete, connection);
        deleteInterpreter(objectToDelete, connection);
        deleteAppliUser(objectToDelete, connection);
    }

    /**
     * Return all line of Interpreter table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Interpreter> findAll() throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        List<Interpreter> interpreters = new ArrayList<>();
        String query = "SELECT " + FIELD_ID + " i FROM " + TABLE + " JOIN " + TABLE_APPLIUSER + " a ON i." + FIELD_ID + " = a." + FIELD_ID;

        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            result = statement.executeQuery();

            while(result.next()){
                interpreters.add(find(result.getString("login")));
            }
        }finally {
            if(result != null){
                result.close();
            }
            if(statement != null){
                statement.close();
            }
        }
        return interpreters;
    }

    /**
     * finds all the interpreter who have the same mission
     * @param idMission the id of the Mission
     * @return the list of the interpreter who have the mission with the idMission for id
     * @throws SQLException if the database could not be reached
     */
    public List<Interpreter> findAllByMissionId(int idMission) throws SQLException{
        Connection connection = DatabaseConnector.getInstance();
        List<Interpreter> list = new ArrayList<>();
        String query = "SELECT i." + FIELD_ID + " FROM " + TABLE + " i JOIN " + TABLE_INTERPRETER_MISSION + " im ON i." + FIELD_ID + " = im." + FIELD_INTERPRETER + " WHERE im." + FIELD_MISSION + " = ?";

        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, idMission);
            result = statement.executeQuery();

            while(result.next()){
                list.add(find(result.getString(FIELD_LOGIN)));
            }
        }finally {
            if(result != null){
                result.close();
            }
            if(statement != null){
                statement.close();
            }
        }

        return list;
    }

    //TODO TABLE_AVAILABILITY ici
    /**
     * Return all Interpreter who are available in the given time and date
     * @param start represent the start of the time that we want the availability
     * @param end represent the end of the time that we want the availability
     * @param date represent the date
     * @return a List of Interpreter who are available in the given time and date or null
     * @return a List of Interpreter who are available in the given time and date, or an empty List if no Interpreter is available
     */
    public List<Interpreter> findAvailable(LocalTime start, LocalTime end, LocalDate date)throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        List<Interpreter> interpreters = new ArrayList<>();
        String query = "SELECT i." + FIELD_LOGIN + " FROM " + TABLE + " i JOIN "
                + DAOPunctualTimeSlot.TABLE_AVAILABILITY + " av ON i." + FIELD_LOGIN
                + " = av." + DAOPunctualTimeSlot.FIELD_INTERPRETER + " JOIN "
                + DAOPunctualTimeSlot.TABLE_TIMESLOT + " t ON av." + DAOPunctualTimeSlot.TABLE_TIMESLOT
                + " = t." + DAOPunctualTimeSlot.TABLE_TIMESLOT_ID + " WHERE t."
                + DAOPunctualTimeSlot.FIELD_START_TIME + " = ? AND t."
                + DAOPunctualTimeSlot.FIELD_END_TIME + " = ? AND TRUNC(t."
                + DAOPunctualTimeSlot.FIELD_START_TIME + ") = ?";

        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(date, start)));
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(date, end)));
            statement.setDate(3, Date.valueOf(date));
            result = statement.executeQuery();

            while(result.next()){
                interpreters.add(find(result.getString("login")));
            }
        }finally {
            if(result != null){
                result.close();
            }
            if(statement != null){
                statement.close();
            }
        }
        return interpreters;
    }

    /**
     * Return all Interpreter who have the AcademicSkill having the given id
     * @param idAcademicSkills the id of the AcademicSkill
     * @return a List of Interpreter who have the AcademicSkill having the idAcademicSkills, or an empty List if no Interpreter have this AcademicSkill
     * @throws NoSuchElementException if idAcademicSkills doesn't correspond to the id of any AcademicSkill
     */
    public List<Interpreter> findByAcademicSkills(int idAcademicSkills)
            throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        List<Interpreter> list = new ArrayList<>();
        String query = "SELECT i." + FIELD_LOGIN + " FROM " + TABLE
                + " i ON JOIN " + TABLE_ACADEMIC_SKILL_INTERPRETER
                + " ai ON i." + FIELD_LOGIN + " = ai." + FIELD_INTERPRETER
                + " WHERE ai." + DAOAcademicSkill.FIELD_SKILL + " = ?";

        PreparedStatement statement = null;
        ResultSet result = null;
        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, idAcademicSkills);
            result = statement.executeQuery();

            while(result.next()){
                list.add(find(result.getString(FIELD_LOGIN)));
            }
        }finally {
            if(result != null){
                result.close();
            }
            if(statement != null){
                statement.close();
            }
        }
        return list;
    }

    /**
     * Return all Interpreter who have the JobSkill having the given id
     * @param idJobSkills the id of the JobSkill
     * @return a List of Interpreter who have the JobSkill having the idJobSkills, or an empty List if no Interpreter have this JobSkill
     * @throws NoSuchElementException if idJobSkills doesn't correspond to the id of any JobSkill
     */
    public List<Interpreter> findByJobSkills(int idJobSkills) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        List<Interpreter> interpreters = null;
        String query = "SELECT i." + FIELD_LOGIN + " FROM " + TABLE + "i ON JOIN " + TABLE_JOB_SKILL_INTERPRETER
                + " ai ON i." + FIELD_LOGIN + " = ai." + FIELD_INTERPRETER + " WHERE ai." + DAOJobSkill.FIELD_SKILL + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, idJobSkills);
            result = statement.executeQuery();

            while(result.next()){
                interpreters.add(find(result.getString(FIELD_LOGIN)));
            }
        }finally {
            if(result != null){
                result.close();
            }
            if(statement != null){
                statement.close();
            }
        }
        return interpreters;
    }
}