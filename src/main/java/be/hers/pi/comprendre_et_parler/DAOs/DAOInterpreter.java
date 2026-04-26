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
import java.util.Set;
import java.util.HashSet;
import java.util.NoSuchElementException;

public class DAOInterpreter extends DAO<Interpreter> {
    protected static final String TABLE_VIEW = "Interpreter";
    protected static final String TABLE_ACADEMIC_SKILL_INTERPRETER = "AcademicSkillInterpreter";
    protected static final String TABLE_JOBSKILL_INTERPRETER = "JobSkillInterpreter";
    protected static final String TABLE_AVAILABILITY = "Availability";
    protected static final String FIELD_JOB_SKILL_INTERPRETER = "JobSkillInterpreter";
    protected static final String FIELD_ACADEMIC_SKILL_INTERPRETER = "interpreter";
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

    /**
     * Populates an Interpreter object from the current row of the given ResultSet.
     * Fetches all related data (academic skills, job skills, location, time slots,
     * unavailabilities) from the database using their respective DAOs.
     * @param result      the ResultSet positioned on the row to read, must not be null
     * @throws SQLException if a database access error occurs while reading the ResultSet
     */
    public Interpreter getResult(ResultSet result)throws SQLException{
        return new Interpreter(
                result.getString(FIELD_LOGIN),
                result.getString(FIELD_FIRST_NAME),
                result.getString(FIELD_LAST_NAME),
                result.getDate(FIELD_BIRTH_DATE).toLocalDate(),
                result.getString(FIELD_HASHED_PASSWORD),
                result.getString(FIELD_EMAIL),
                result.getString(FIELD_PHONE_NUMBER),
                result.getInt(FIELD_WEEK_QUOTA),
                result.getInt(FIELD_YEAR_QUOTA),
                result.getString(FIELD_TRANSPORT_MODE),
                DAOAcademicSkill.getAcademicSkillOfAnInterpreter(result.getInt(FIELD_ID)),
                DAOJobSkill.getJobSkillOfAnInterpreter(result.getInt(FIELD_ID)),
                DAOLocation.find(result.getInt(FIELD_LOCATION)),
                DAOBaseTimeSlot.findAllByInterpreterId(result.getInt(FIELD_ID)),
                new DAOExceptionalUnavailability().findByInterpreterId(result.getInt(FIELD_ID))
        );
    }

    /**
     * Check if an object already exists in the database
     * @param objectToCheck the object to check
     * @return true if the object already exists, else false
     * @throws SQLException if the database could not be reached
     */
    protected boolean checkAlreadyExists(Interpreter objectToCheck) throws SQLException{
        boolean exists = false;
        Set<Interpreter> interpreters = findAll();
        for(Interpreter interpreter : interpreters){
            if(objectToCheck.equals(interpreter)){
                exists = true;
            }
        }
        return exists;
    }

    /**
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Interpreter find(int id) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String query = "SELECT * FROM " + TABLE_VIEW + " WHERE " + FIELD_ID + " = ?";
        Interpreter interpreter = null;

        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();

            if(result.next()){
               interpreter = getResult(result);
            }
        }finally{
            closeResultSet(result);
            closeStatement(statement);

        }
        return interpreter;
    }

    /**
     * Search for an Interpreter in the database with the int parameter
     * @param login the login of the interpreter to find in database
     * @return the object identified by login in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Interpreter find(String login) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        Interpreter interpreter = null;

        String query = "SELECT * FROM %s WHERE %s = ?";
        query = String.format(query, TABLE_VIEW, FIELD_LOGIN);

        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            statement.setString(1, login);
            result = statement.executeQuery();

            if(result.next()){
                interpreter = getResult(result);
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);

        }
        return interpreter;
    }

    /**
     * @param objectToInsert an object of type T to add to the database
     * @post objectToInsert has been added to the database, and the change was commited
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the insertion failed for any other reason
     */
    @Override
    public void create(Interpreter objectToInsert) throws AlreadyExistsException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String query = "INSERT INTO " + TABLE_VIEW + " (" + FIELD_LOGIN + ", " +
                FIELD_FIRST_NAME + ", " + FIELD_LAST_NAME + ", " + FIELD_BIRTH_DATE + ", " +
                FIELD_HASHED_PASSWORD + ", " + FIELD_EMAIL + ", " + FIELD_PHONE_NUMBER + ", "
                + FIELD_WEEK_QUOTA + ", " + FIELD_YEAR_QUOTA + ", " + FIELD_TRANSPORT_MODE + ", "
                + FIELD_LOCATION + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        if (find(objectToInsert.getLogin()) != null){
            throw new AlreadyExistsException("Object already exist in database");
        }
        if(checkAlreadyExists(objectToInsert)){
            throw new AlreadyExistsException("The interpreter already exists in the database");
        }
        PreparedStatement statement = null;
        ResultSet rs;

        try{
            statement = connection.prepareStatement(query, new String[]{FIELD_ID});
            statement.setString(1, objectToInsert.getLogin());
            statement.setString(2, objectToInsert.getFirstName());
            statement.setString(3, objectToInsert.getLastName());
            statement.setDate(4, Date.valueOf(objectToInsert.getBirthDate()));
            statement.setString(5, objectToInsert.getHashedPassword());
            statement.setString(6, objectToInsert.getEmail());
            statement.setString(7, objectToInsert.getPhoneNumber());
            statement.setInt(8, objectToInsert.getHourQuotaWeek());
            statement.setInt(9, objectToInsert.getHourQuotaYear());
            statement.setString(10, objectToInsert.getTransportMode());
            statement.setInt(11, objectToInsert.getLocation().getId());
            statement.executeUpdate();
            rs = statement.getGeneratedKeys();
            if(rs.next()){
                objectToInsert.setId(rs.getInt(FIELD_ID));
            }
        }finally {
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
    public void update(Interpreter objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        String queryInterpreter = "UPDATE " + TABLE_VIEW + " SET " + FIELD_LOGIN + " = ?, " +
                FIELD_FIRST_NAME + " = ?, " + FIELD_LAST_NAME + " = ?, " + FIELD_BIRTH_DATE + " = ?, " +
                FIELD_HASHED_PASSWORD + " = ?, " + FIELD_EMAIL + " = ?, " + FIELD_PHONE_NUMBER + " = ?, " + " = ?, "
                + FIELD_WEEK_QUOTA + " = ?, " + FIELD_YEAR_QUOTA + " = ?, "
                + FIELD_TRANSPORT_MODE + " = ?" + FIELD_LOCATION + " = ? WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        int rowsAffected = 0;

        if(checkAlreadyExists(objectToUpdate)){
            throw new AlreadyExistsException("The beneficiary already exists in database.");
        }

        try {
            statement = connection.prepareStatement(queryInterpreter);
            statement.setString(1, objectToUpdate.getLogin());
            statement.setString(2, objectToUpdate.getFirstName());
            statement.setString(3, objectToUpdate.getLastName());
            statement.setDate(4, Date.valueOf(objectToUpdate.getBirthDate()));
            statement.setString(5, objectToUpdate.getHashedPassword());
            statement.setString(6, objectToUpdate.getEmail());
            statement.setString(7, objectToUpdate.getPhoneNumber());
            statement.setInt(8, objectToUpdate.getHourQuotaWeek());
            statement.setInt(9, objectToUpdate.getHourQuotaYear());
            statement.setString(10, objectToUpdate.getTransportMode());
            statement.setInt(11, objectToUpdate.getLocation().getId());
            statement.setInt(12, objectToUpdate.getId());
            rowsAffected = statement.executeUpdate();

            if(rowsAffected < 1){
                throw new NoSuchElementException("[ERROR] There is no interpreter with the id " + objectToUpdate.getId() + ".");
            }
        } finally {
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
    public void delete(Interpreter objectToDelete)
            throws NoSuchElementException, SQLException {
        String query = "DELETE FROM " + TABLE_VIEW + " WHERE " + FIELD_ID + " = ?";
        Connection connection = DatabaseConnector.getInstance();
        PreparedStatement statement = null;
        int rowsAffected = 0;

        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            statement.executeUpdate();

            if(rowsAffected  < 1){
                throw new NoSuchElementException("[ERROR] There is no user with the id " + objectToDelete.getId() + ".");
            }
        } finally {
            closeStatement(statement);
        }
    }

    /**
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Set<Interpreter> findAll() throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        Set<Interpreter> interpreters = new HashSet<>();
        String query = "SELECT *  FROM " + TABLE_VIEW;

        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            result = statement.executeQuery();

            while(result.next()){
                interpreters.add(getResult(result));
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return interpreters;
    }

    /**
     * finds all the interpreter who have the same mission
     * @param idMission the id of the Mission
     * @return the set of the interpreter who have the mission with the idMission for id or an empty set
     * @throws SQLException if the database could not be reached
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     */
    public Set<Interpreter> findAllByMissionId(int idMission) throws SQLException, NoSuchElementException{
        Connection connection = DatabaseConnector.getInstance();
        Set<Interpreter> interpreters = new HashSet<>();
        String query = "SELECT i.* FROM " + TABLE_VIEW + " i JOIN "
                + TABLE_INTERPRETER_MISSION + " im ON i." + FIELD_ID
                + " = im." + FIELD_INTERPRETER + " WHERE im." + FIELD_MISSION + " = ?";

        if(new DAOMission().find(idMission) == null){
            throw new NoSuchElementException("[ERROR] There is no mission with the id " + idMission + ".");
        }

        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, idMission);
            result = statement.executeQuery();

            while(result.next()){
                interpreters.add(getResult(result));
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }

        return interpreters;
    }

    /**
     * Return all Interpreter who are available in the given time and date
     * @param start represent the start of the time that we want the availability
     * @param end represent the end of the time that we want the availability
     * @param date represent the date
     * @throws SQLException if the database could not be reached
     * @return a set of Interpreter who are available in the given time and date, or an empty set if no Interpreter is available
     */
    public Set<Interpreter> findAvailable(LocalTime start, LocalTime end, LocalDate date)throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        Set<Interpreter> interpreters = new HashSet<>();
        String query = "SELECT i.* FROM " + TABLE_VIEW + " i JOIN "
                + TABLE_AVAILABILITY + " av ON i." + FIELD_ID
                + " = av." + FIELD_INTERPRETER + " JOIN "
                + DAOBaseTimeSlot.TABLE_TIMESLOT + " t ON av." + DAOBaseTimeSlot.TABLE_TIMESLOT
                + " = t." + DAOBaseTimeSlot.TABLE_TIMESLOT_ID + " WHERE t."
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
                interpreters.add(getResult(result));
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return interpreters;
    }

    /**
     * Return all Interpreter who have the AcademicSkill having the given id
     * @param idAcademicSkills the id of the AcademicSkill
     * @return a set of Interpreter who have the AcademicSkill having the idAcademicSkills, or an empty set if no Interpreter have this AcademicSkill
     * @throws SQLException if the database could not be reached
     * @throws NoSuchElementException if idAcademicSkills doesn't correspond to the id of any AcademicSkill
     */
    public Set<Interpreter> findByAcademicSkills(int idAcademicSkills) throws NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        Set<Interpreter> interpreters = new HashSet<>();
        String query = "SELECT i.* FROM " + TABLE_VIEW
                + " i ON JOIN " + TABLE_ACADEMIC_SKILL_INTERPRETER
                + " ai ON i." + FIELD_ID + " = ai." + FIELD_INTERPRETER
                + " WHERE ai." + FIELD_ACADEMIC_SKILL_INTERPRETER + " = ?";
        if(new DAOMission().find(idAcademicSkills) == null){
            throw new NoSuchElementException("[ERROR] There is no academicskill with the id " + idAcademicSkills + ".");
        }
        PreparedStatement statement = null;
        ResultSet result = null;
        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, idAcademicSkills);
            result = statement.executeQuery();

            while(result.next()){
                interpreters.add(getResult(result));
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return interpreters;
    }

    /**
     * Return all Interpreter who have the JobSkill having the given id
     * @param idJobSkills the id of the JobSkill
     * @return a Set of Interpreter who have the JobSkill having the idJobSkills, or an empty set if no Interpreter have this JobSkill
     * @throws NoSuchElementException if idJobSkills doesn't correspond to the id of any JobSkill
     * @throws SQLException if the database could not be reached
     */
    public Set<Interpreter> findByJobSkills(int idJobSkills) throws NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getInstance();
        Set<Interpreter> interpreters = new HashSet<>();
        String query = "SELECT i." + FIELD_LOGIN + " FROM " + TABLE_VIEW
                + "i ON JOIN " + TABLE_JOBSKILL_INTERPRETER + " ai ON i."
                + FIELD_LOGIN + " = ai." + FIELD_INTERPRETER + " WHERE ai."
                + FIELD_JOB_SKILL_INTERPRETER + " = ?";
        if(new DAOMission().find(idJobSkills) == null){
            throw new NoSuchElementException("[ERROR] There is no jobskills with the id " + idJobSkills + ".");
        }
        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = connection.prepareStatement(query);
            statement.setInt(1, idJobSkills);
            result = statement.executeQuery();

            while(result.next()){
                interpreters.add(getResult(result));
            }
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return interpreters;
    }
}