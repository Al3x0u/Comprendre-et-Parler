package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;

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
    protected static final String TABLE = "Interpreter";
    protected static final String TABLE_ACADEMIC_SKILL_INTERPRETER = "AcademicSkillInterpreter";
    protected static final String TABLE_JOB_SKILL_INTERPRETER = "JobSkillInterpreter";
    protected static final String TABLE_AVAILABILITY = "Availability";
    protected static final String TABLE_INTERPRETER_MISSION = "InterpreterMission";
    protected static final String FIELD_SKILL = "skill";
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
    protected static final String AVAILABILITY_REF_INTERPRETER = "interpreter";
    protected static final String AVAILABILITY_REF_TIMESLOT = "baseTimeSlot";
    protected static final String AVAILABILITY_REF_DAY = "baseTimeSlotDay";



    @Override
    public Interpreter find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        Interpreter interpreter = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);

            result = statement.executeQuery();
            if(result.next())
               interpreter = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return interpreter;
    }

    /**
     * Search for an Interpreter in the database with the String parameter
     * @param login the login of the Interpreter to find in database
     * @return the Interpreter identified by login in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Interpreter find(String login) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_LOGIN
        );
        Interpreter interpreter = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, login);

            result = statement.executeQuery();
            if (result.next())
                interpreter = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return interpreter;
    }

    @Override
    public void create(Interpreter objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert) >= 0)
            throw new AlreadyExistsException("The interpreter already exists in the database");

        String query = String.format(
                "INSERT INTO %s VALUES (NULL, NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                TABLE
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToInsert.getFirstName());
            statement.setString(2, objectToInsert.getLastName());
            statement.setDate(3, Date.valueOf(objectToInsert.getBirthDate()));
            statement.setString(4, objectToInsert.getHashedPassword());
            statement.setString(5, objectToInsert.getEmail());
            statement.setString(6, objectToInsert.getPhoneNumber());
            statement.setInt(7, objectToInsert.getHourQuotaWeek());
            statement.setInt(8, objectToInsert.getHourQuotaYear());
            statement.setString(9, objectToInsert.getTransportMode());
            statement.setInt(10, objectToInsert.getLocation().getId());

            statement.executeUpdate();
            getNewAttributes(objectToInsert);
        } finally {
            closeStatement(statement);
        }

        if (objectToInsert.getAvailability() != null) {
            for (BaseTimeSlot av : objectToInsert.getAvailability()) {
                createAvailability(objectToInsert, av);
            }
        }
    }


    /**
     * Update the login and the id of the new object inserted in the database
     * @param newObject the new object inserted in the database
     * @throws SQLException if the database could not be reached
     */
    private void getNewAttributes(Interpreter newObject) throws SQLException {
        String query = String.format(
                "SELECT %s, %s FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ?",
                FIELD_ID, FIELD_LOGIN, TABLE, FIELD_FIRST_NAME, FIELD_LAST_NAME,FIELD_BIRTH_DATE,
                FIELD_HASHED_PASSWORD, FIELD_EMAIL, FIELD_PHONE_NUMBER
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, newObject.getFirstName());
            statement.setString(2, newObject.getLastName());
            statement.setDate(3, Date.valueOf(newObject.getBirthDate()));
            statement.setString(4, newObject.getHashedPassword());
            statement.setString(5, newObject.getEmail());
            statement.setString(6, newObject.getPhoneNumber());

            result = statement.executeQuery();
            if (result.next()) {
                newObject.setId(result.getInt(FIELD_ID));
                newObject.setLogin(result.getString(FIELD_LOGIN));
            }
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    @Override
    public void update(Interpreter objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (checkAlreadyExists(objectToUpdate) >= 0)
            throw new AlreadyExistsException("The interpreter already exists in database.");

        String query = String.format(
                "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                TABLE, FIELD_FIRST_NAME, FIELD_LAST_NAME,FIELD_BIRTH_DATE, FIELD_HASHED_PASSWORD,
                FIELD_EMAIL, FIELD_PHONE_NUMBER, FIELD_WEEK_QUOTA, FIELD_YEAR_QUOTA,
                FIELD_TRANSPORT_MODE, FIELD_LOCATION, FIELD_ID
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getFirstName());
            statement.setString(2, objectToUpdate.getLastName());
            statement.setDate(3, Date.valueOf(objectToUpdate.getBirthDate()));
            statement.setString(4, objectToUpdate.getHashedPassword());
            statement.setString(5, objectToUpdate.getEmail());
            statement.setString(6, objectToUpdate.getPhoneNumber());
            statement.setInt(7, objectToUpdate.getHourQuotaWeek());
            statement.setInt(8, objectToUpdate.getHourQuotaYear());
            statement.setString(9, objectToUpdate.getTransportMode());
            statement.setInt(10, objectToUpdate.getLocation().getId());
            statement.setInt(11, objectToUpdate.getId());

            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no Interpreter with the id " + objectToUpdate.getId());
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public void delete(int idObjectToDelete) throws NoSuchElementException, SQLException {
        String query = String.format(
                "DELETE FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idObjectToDelete);

            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no Interpreter with the id " + idObjectToDelete);
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public Set<Interpreter> findAll() throws SQLException {
        String query = String.format("SELECT * FROM %s", TABLE);
        Set<Interpreter> interpreters = new HashSet<>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();

            while (result.next())
                interpreters.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return interpreters;
    }

    @Override
    protected int checkAlreadyExists(Interpreter objectToCheck) throws SQLException {
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? " +
                        "AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ?",
                FIELD_ID, TABLE, FIELD_FIRST_NAME, FIELD_LAST_NAME, FIELD_BIRTH_DATE, FIELD_HASHED_PASSWORD, FIELD_EMAIL,
                FIELD_PHONE_NUMBER, FIELD_WEEK_QUOTA, FIELD_YEAR_QUOTA, FIELD_TRANSPORT_MODE, FIELD_LOCATION
        );
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToCheck.getFirstName());
            statement.setString(2, objectToCheck.getLastName());
            statement.setDate(3, Date.valueOf(objectToCheck.getBirthDate()));
            statement.setString(4, objectToCheck.getHashedPassword());
            statement.setString(5, objectToCheck.getEmail());
            statement.setString(6, objectToCheck.getPhoneNumber());
            statement.setInt(7, objectToCheck.getHourQuotaWeek());
            statement.setInt(8, objectToCheck.getHourQuotaYear());
            statement.setString(9, objectToCheck.getTransportMode());
            statement.setInt(10, objectToCheck.getLocation().getId());

            result = statement.executeQuery();
            if(result.next())
                return result.getInt(FIELD_ID);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return -1;
    }

    @Override
    public Interpreter getResult(ResultSet result) throws SQLException {
        int id = result.getInt(FIELD_ID);
        return new Interpreter(
                id,
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
                new DAOAcademicSkill().getAcademicSkillOfAnInterpreter(id),
                new DAOJobSkill().getJobSkillOfAnInterpreter(id),
                new DAOLocation().find(result.getInt(FIELD_LOCATION)),
                new DAOBaseTimeSlot().findAvailabilities(id)
        );
    }

    public boolean availabilityExists(Interpreter interpreter, BaseTimeSlot slot) throws SQLException {
        String query = String.format(
                "SELECT 1 FROM %s WHERE %s = ? AND %s = ? AND %s = ?",
                TABLE_AVAILABILITY, AVAILABILITY_REF_INTERPRETER, AVAILABILITY_REF_TIMESLOT, AVAILABILITY_REF_DAY
        );
        ResultSet result = null;
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, interpreter.getId());
            statement.setInt(2, slot.getId());
            statement.setInt(3, slot.getDay().getValue());

            result = statement.executeQuery();
            return result.next();
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    /**
     * Create an availability in DB if it doesn't exist yet
     * @param interpreter the interpreter for whom to add an availability slot in DB
     * @param slot the BaseTimeSlot representing the availability
     * @throws SQLException if a database error occurs
     */
    public void createAvailability(Interpreter interpreter, BaseTimeSlot slot) throws SQLException {
        if (availabilityExists(interpreter, slot))
            return;

        int interpreterRef = checkAlreadyExists(interpreter);
        if (interpreterRef < 0) {
            create(interpreter);
            interpreterRef = interpreter.getId();
        }

        DAOBaseTimeSlot daoSlot = new DAOBaseTimeSlot();
        int timeSlotRef = daoSlot.checkAlreadyExists(slot);
        if (timeSlotRef < 0 ) {
            daoSlot.create(slot);
            timeSlotRef = slot.getId();
        }

        String query = String.format("INSERT INTO %s(%s, %s, %s) VALUES(?, ?, ?)",
                TABLE_AVAILABILITY, AVAILABILITY_REF_INTERPRETER, AVAILABILITY_REF_TIMESLOT, AVAILABILITY_REF_DAY
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, interpreterRef);
            statement.setInt(2, timeSlotRef);
            statement.setInt(3, slot.getDay().getValue());

            statement.executeUpdate();
        }
        finally {
            closeStatement(statement);
        }
    }

    /**
     * Delete an availability in DB
     * @param interpreter the interpreter for whom to delete an availability in DB
     * @param slot the BaseTimeSlot representing the availability
     * @throws NoSuchElementException if the availability does not exist in DB
     * @throws SQLException if a database error occurs
     */
    public void deleteAvailability(Interpreter interpreter, BaseTimeSlot slot) throws SQLException {
        String query = String.format(
                "DELETE FROM %s WHERE %s = ? AND %s = ? AND %s = ?",
                TABLE_AVAILABILITY, AVAILABILITY_REF_INTERPRETER, AVAILABILITY_REF_TIMESLOT, AVAILABILITY_REF_DAY
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, interpreter.getId());
            statement.setInt(2, slot.getId());
            statement.setInt(3, slot.getDay().getValue());

            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] Availability (" + interpreter.getId() + ", " + slot.getId()
                        + ", " + slot.getDay().getValue() + " does not exist in DB.");
        } finally {
            closeStatement(statement);
        }
    }

    /**
     * finds all the interpreter who have the same mission
     * @param idMission the id of the Mission
     * @return the set of the interpreter who have the mission with the idMission for id or an empty set
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     */
    public Set<Interpreter> findAllByMissionId(int idMission) throws SQLException, NoSuchElementException {
        if (new DAOMission().find(idMission) == null)
            throw new NoSuchElementException("[ERROR] There is no Mission with the id " + idMission);

        String query = String.format(
                "SELECT i.* FROM %s i JOIN %s im ON i.%s = im.%s WHERE im.%s = ?",
                TABLE, TABLE_INTERPRETER_MISSION, FIELD_ID, FIELD_INTERPRETER, FIELD_MISSION
        );
        Set<Interpreter> interpreters = new HashSet<>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idMission);

            result = statement.executeQuery();
            while (result.next())
                interpreters.add(getResult(result));
        } finally {
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
    public Set<Interpreter> findAvailable(LocalTime start, LocalTime end, LocalDate date) throws SQLException {
        String query = String.format(
                "SELECT i.* FROM %s i JOIN %s av ON i.%s = av.%s JOIN %s t ON av.%s = t.%S " +
                        "WHERE t.%S = ? AND t.%s = ? AND TRUNC(t.%s) = ?",
                TABLE, TABLE_AVAILABILITY, FIELD_ID, FIELD_INTERPRETER, DAOBaseTimeSlot.TABLE,
                DAOBaseTimeSlot.TABLE, DAOBaseTimeSlot.FIELD_ID, DAOPunctualTimeSlot.FIELD_START_TIME,
                DAOPunctualTimeSlot.FIELD_END_TIME, DAOPunctualTimeSlot.FIELD_START_TIME
        );
        Set<Interpreter> interpreters = new HashSet<>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(date, start)));
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(date, end)));
            statement.setDate(3, Date.valueOf(date));

            result = statement.executeQuery();
            while (result.next())
                interpreters.add(getResult(result));
        } finally {
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
        if (new DAOMission().find(idAcademicSkills) == null)
            throw new NoSuchElementException("[ERROR] There is no AcademicSkill with the id " + idAcademicSkills);

        String query = String.format(
                "SELECT i.* FROM %s i JOIN %s ai ON i.%s = ai.%s WHERE ai.%s = ?",
                TABLE, TABLE_ACADEMIC_SKILL_INTERPRETER, FIELD_ID, FIELD_INTERPRETER, FIELD_SKILL
        );
        Set<Interpreter> interpreters = new HashSet<>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idAcademicSkills);

            result = statement.executeQuery();
            while (result.next())
                interpreters.add(getResult(result));
        } finally {
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
        if (new DAOMission().find(idJobSkills) == null)
            throw new NoSuchElementException("[ERROR] There is no JobSkill with the id " + idJobSkills);

        String query = String.format(
                "SELECT i.* FROM %s i JOIN %s ai ON i.%s = ai.%s WHERE ai.%s = ?",
                TABLE, TABLE_JOB_SKILL_INTERPRETER, FIELD_ID, FIELD_INTERPRETER, FIELD_SKILL
        );
        Set<Interpreter> interpreters = new HashSet<>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idJobSkills);

            result = statement.executeQuery();
            while (result.next())
                interpreters.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return interpreters;
    }
}