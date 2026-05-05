package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

public class DAOMission extends DAO<Mission> {
    protected static final String TABLE = "mission";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_SUBJECT = "subject";
    protected static final String FIELD_STATE = "stateOfMission";
    protected static final String FIELD_COMMENTARY = "commentary";
    protected static final String FIELD_BENEFICIARY = "beneficiary";
    protected static final String FIELD_LOCATION = "location";
    protected static final String FIELD_ROOM = "room";
    protected static final String FIELD_TIME_SLOT = "timeSlot";
    protected static final String FIELD_JOB_SKILL = "jobSkill";
    protected static final String FIELD_ACADEMIC_SKILL = "academicSkill";
    protected static final String FIELD_IMPORTANCE = "importance";
    
    @Override
    public Mission find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        Mission mission = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            
            result = statement.executeQuery();
            if (result.next())
                mission = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return mission;
    }
    
    @Override
    public void create(Mission objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert) >= 0)
            throw new AlreadyExistsException("Mission overlaps with an existing mission");

        String query = String.format("INSERT INTO %s VALUES (NULL, ?, ?, ?, ?, ?)", TABLE);
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query, new String[]{FIELD_ID});
            statement.setInt(1, objectToInsert.getImportance());
            statement.setString(2, objectToInsert.getSubject());
            statement.setString(3, objectToInsert.getCommentary());
            statement.setInt(4, objectToInsert.getLocation().getId());
            statement.setString(5, objectToInsert.getRoom());

            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next())
                objectToInsert.setId(generatedKeys.getInt(1));

            if (objectToInsert.getInterpreters() != null) {
                for (Interpreter interpreter : objectToInsert.getInterpreters())
                    addInterpreterToMission(objectToInsert.getId(), interpreter.getId());
            }
        } finally {
            closeResultSet(generatedKeys);
            closeStatement(statement);
        }
    }
    
    @Override
    public void update(Mission objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (find(objectToUpdate.getId()) == null)
            throw new NoSuchElementException("[ERROR] There is no Mission with the id " + objectToUpdate.getId());
        
        if (checkAlreadyExists(objectToUpdate) >= 0)
            throw new AlreadyExistsException("Mission overlaps with an existing mission");
        
        String query = String.format(
                "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                TABLE, FIELD_SUBJECT, FIELD_COMMENTARY, FIELD_LOCATION,
                FIELD_ROOM, FIELD_IMPORTANCE, FIELD_ID
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getSubject());
            statement.setString(2, objectToUpdate.getCommentary());
            statement.setInt(3, objectToUpdate.getLocation().getId());
            statement.setString(4, objectToUpdate.getRoom());
            statement.setInt(5, objectToUpdate.getImportance());
            statement.setInt(6, objectToUpdate.getId());

            statement.executeUpdate();
            if (objectToUpdate.getInterpreters() != null) {
                deleteAllInterpretersFromMission(objectToUpdate.getId());
                for (Interpreter interpreter : objectToUpdate.getInterpreters())
                    addInterpreterToMission(objectToUpdate.getId(), interpreter.getId());
            }
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
                throw new NoSuchElementException("[ERROR] There is no Mission with the id " + idObjectToDelete);
        } finally {
            closeStatement(statement);
        }
    }
    
    @Override
    public Set<Mission> findAll() throws SQLException {
        String query = String.format("SELECT * FROM %s", TABLE);
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<Mission> missions = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);

            result = statement.executeQuery();
            while (result.next())
                missions.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return missions;
    }

    @Override
    protected int checkAlreadyExists(Mission objectToCheck) throws SQLException {
        String query = String.format(
                "SELECT 1 FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ?",
                TABLE, FIELD_SUBJECT, FIELD_COMMENTARY,
                FIELD_LOCATION, FIELD_ROOM, FIELD_IMPORTANCE
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToCheck.getSubject());
            statement.setString(2, objectToCheck.getCommentary());
            statement.setInt(3, objectToCheck.getLocation().getId());
            statement.setString(4, objectToCheck.getRoom());
            statement.setInt(5, objectToCheck.getImportance());

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
    protected Mission getResult(ResultSet result) throws SQLException {
        int id = result.getInt(FIELD_ID);
        return new Mission(
                id,
                result.getString(FIELD_SUBJECT),
                result.getString(FIELD_COMMENTARY),
                findTimeSlot(id),
                null,
                new DAOLocation().find(result.getInt(FIELD_LOCATION)),
                null,
                null,
                result.getString(FIELD_ROOM),
                result.getInt(FIELD_IMPORTANCE)
        );
    }

    /**
     * Get the set of time slots for a mission
     * @param idMission the mission for which to have the time slots
     * @return the set of time slots for the mission
     * @throws SQLException if the database could not be reached
     */
    private Set<TimeSlot> findTimeSlot(int idMission) throws SQLException {
        String queryPunctualTimeSlot = "SELECT * FROM PunctualMission WHERE mission = ?";
        String queryBaseTimeSLot = "SELECT * FROM RegularMission WHERE mission = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<TimeSlot> timeSlots = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(queryPunctualTimeSlot);
            statement.setInt(1, idMission);
            result = statement.executeQuery();
            while (result.next()) {
                PunctualTimeSlot p = new DAOPunctualTimeSlot().find(result.getInt("punctualTimeSlot"));
                timeSlots.add(new RegularMission(p, MissionState.fromValue(result.getInt(FIELD_STATE))));
            }

            statement = DatabaseConnector.getInstance().prepareStatement(queryBaseTimeSLot);
            statement.setInt(1, idMission);
            result = statement.executeQuery();
            while (result.next())
                timeSlots.add(new DAOBaseTimeSlot().find(result.getInt("baseTimeSlot")));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return timeSlots;
    }


    /**
     * Return the schedule for a specific week
     * @param year represent the year of the week
     * @param weekNumber represent the week number in the year (1-52)
     * @return a Set of Mission which compose the schedule OF the given week, or an empty Set if none was found
     * @throws SQLException if the database could not be reached
     */
    public Set<Mission> getAllMissionsForWeek(int year, int weekNumber) throws SQLException {

        LocalDate start = LocalDate.ofYearDay(year, 1).with(WeekFields.ISO.weekOfYear(), weekNumber).with(DayOfWeek.MONDAY);

        LocalDate end = start.plusDays(6);

        Set<Mission> missions = new HashSet<>();

        String query = "SELECT m.* FROM " + TABLE + " m " +
                "JOIN TimeSlot ts ON m." + FIELD_TIME_SLOT + " = ts.id " +
                "WHERE ( " +
                "(ts.day IS NOT NULL)" +
                "OR (ts.day IS NULL AND ts.startDateTime BETWEEN ? AND ?)" +
                ")";

        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setDate(1, java.sql.Date.valueOf(start));
            statement.setDate(2, java.sql.Date.valueOf(end));

            result = statement.executeQuery();

            while (result.next()) {
                missions.add(getResult(result));
            }

        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }

        return missions;
    }


    /**
     * Return the schedule of the user with the given id for a specific week
     * @param idUser represent the id of the user which we want the schedule
     * @param year represent the year of the week
     * @param weekNumber represent the week number in the year (1-52)
     * @return a Set of Mission which compose the schedule of the idUser for the given week, or an empty Set if none was found
     * @throws SQLException if the database could not be reached
     */
    public Set<Mission> getScheduleForWeek(int idUser, int year, int weekNumber) throws SQLException {
        LocalDate date = LocalDate.ofYearDay(year, 1)
                .with(WeekFields.ISO.weekOfYear(), weekNumber)
                .with(DayOfWeek.MONDAY);
        Set<Mission> missions = new HashSet<>();
        for (int i = 0; i < 7; i++)
            missions.addAll(getScheduleForDay(idUser, date.plusDays(i)));
        return missions;
    }

    /**
     * Return the schedule of the user with the given id for a specific day
     * @param idUser represent the id of the user which we want the schedule
     * @param date represent the specific day
     * @return a Set of Mission which compose the schedule of the idUser for the given day, or an empty Set if none was found
     * @throws SQLException if the database could not be reached
     */
    public Set<Mission> getScheduleForDay(int idUser, LocalDate date) throws SQLException {
        String query = String.format(
                "SELECT m.id FROM %s m " +
                "JOIN TimeSlot ts ON m.%s = ts.id " +
                "WHERE (ts.day = ? " +
                "OR (ts.day IS NULL AND TRUNC(ts.startTime) = ?)) " +
                "AND (m.id IN (SELECT mission FROM InterpreterMission WHERE interpreter = ?) " +
                "OR m.%s = ?)",
                TABLE, FIELD_TIME_SLOT, FIELD_BENEFICIARY
        );
        Set<Mission> missions = new HashSet<>();
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, date.getDayOfWeek().getValue());
            statement.setDate(2, java.sql.Date.valueOf(date));
            statement.setInt(3, idUser);
            statement.setInt(4, idUser);
            
            result = statement.executeQuery();
            while (result.next()) {
                Mission mission = find(result.getInt("id"));
                if (mission != null)
                    missions.add(mission);
            }
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return missions;
    }

    /**
     * Add an interpreter to a mission in the InterpreterMission table
     * @param idMission : id of the mission
     * @param idInterpreter : id of the interpreter
     * @throws AlreadyExistsException if the interpreter is already linked to the mission
     * @throws SQLException if the database could not be reached
     * @post the interpreter is linked to the mission in the database
     */
    public void addInterpreterToMission(int idMission, int idInterpreter) throws SQLException, AlreadyExistsException {
        String checkQuery = "SELECT * FROM InterpreterMission WHERE mission = ? AND interpreter = ?";
        String insertQuery = "INSERT INTO InterpreterMission(mission, interpreter) VALUES(?, ?)";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(checkQuery);
            statement.setInt(1, idMission);
            statement.setInt(2, idInterpreter);
            
            result = statement.executeQuery();
            if (result.next())
                throw new AlreadyExistsException("This interpreter is already linked to the mission");

            statement = DatabaseConnector.getInstance().prepareStatement(insertQuery);
            statement.setInt(1, idMission);
            statement.setInt(2, idInterpreter);
            
            statement.executeUpdate();
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    /**
     * Remove an interpreter from a mission in the InterpreterMission table
     * @param idMission : id of the mission
     * @param idInterpreter : id of the interpreter
     * @throws NoSuchElementException if the interpreter is not linked to the mission
     * @throws SQLException if the database could not be reached
     * @post the interpreter is no longer linked to the mission in the database
     */
    public void removeInterpreterFromMission(int idMission, int idInterpreter) throws SQLException, NoSuchElementException {
        String deleteQuery = "DELETE FROM InterpreterMission WHERE mission = ? AND interpreter = ?";
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(deleteQuery);
            statement.setInt(1, idMission);
            statement.setInt(2, idInterpreter);
            
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] The Interpreter " + idInterpreter + " is not linked to the Mission " + idMission);
        } finally {
            closeStatement(statement);
        }
    }

    /**
     * Delete all interpreters linked to a mission in the InterpreterMission table
     * @param idMission : id of the mission
     * @throws SQLException if the database could not be reached
     * @post all interpreters linked to the mission have been deleted from the database
     */
    private void deleteAllInterpretersFromMission(int idMission) throws SQLException {
        String query = "DELETE FROM InterpreterMission WHERE mission = ?";
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idMission);
            
            statement.executeUpdate();
        } finally {
            closeStatement(statement);
        }
    }
}