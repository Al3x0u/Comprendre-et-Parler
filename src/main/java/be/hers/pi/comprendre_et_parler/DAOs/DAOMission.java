package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOMission implements DAO<Mission> {
    public static final String TABLE = "mission";
    public static final String FIELD_ID = "id";
    public static final String FIELD_SUBJECT = "subject";
    public static final String FIELD_STATE = "stateOfMission";
    public static final String FIELD_COMMENTARY = "commentary";
    public static final String FIELD_BENEFICIARY = "beneficiary";
    public static final String FIELD_TIME_SLOT = "timeSlot";
    public static final String FIELD_JOB_SKILL = "jobSkill";
    public static final String FIELD_ACADEMIC_SKILL = "academicSkill";
    public static final String FIELD_IMPORTANCE = "importance";

    /**
     * Search for a Mission in the database with the int parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Mission find(int id) throws SQLException {
        String query = "SELECT * FROM " + TABLE + " WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        Mission mission = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if (result.next()) {
                MissionState state = MissionState.valueOf(result.getString(FIELD_STATE));
                TimeSlot timeSlot;
                if (state == MissionState.REGULAR) {
                    timeSlot = new DAOBaseTimeSlot().find(result.getInt(FIELD_TIME_SLOT));
                } else {
                    timeSlot = new DAOPunctualTimeSlot().find(result.getInt(FIELD_TIME_SLOT));
                }
                mission = new Mission(
                        id,
                        result.getString(FIELD_SUBJECT),
                        state,
                        result.getString(FIELD_COMMENTARY),
                        timeSlot,
                        result.getObject(FIELD_BENEFICIARY, Beneficiary.class),
                        new DAOLocation().getMissionLocation(id),
                        new DAOJobSkill().find(result.getInt(FIELD_JOB_SKILL)),
                        new DAOAcademicSkill().find(result.getInt(FIELD_ACADEMIC_SKILL)),
                        new DAOLocation().getMissionRoom(id),
                        result.getInt(FIELD_IMPORTANCE)
                );
            }
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return mission;
    }

    /**
     * Insert a Mission object in the database
     * @param objectToInsert an object of type Mission to add to the database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the id was updated with auto generated id
     */
    @Override
    public void create(Mission objectToInsert) throws AlreadyExistsException, SQLException {
        List<Mission> missions = findAll();
        for (Mission line : missions) {
            if (line.equals(objectToInsert))
                throw new AlreadyExistsException("Mission " + objectToInsert.getSubject() + " already exists at id " + line.getId());
        }

        String query = "INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        query = String.format(query, TABLE, FIELD_SUBJECT, FIELD_STATE, FIELD_COMMENTARY, FIELD_BENEFICIARY, FIELD_TIME_SLOT, FIELD_JOB_SKILL, FIELD_ACADEMIC_SKILL, FIELD_IMPORTANCE);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query, new String[]{FIELD_ID});
            statement.setString(1, objectToInsert.getSubject());
            statement.setString(2, objectToInsert.getStateOfMission().toString());
            statement.setString(3, objectToInsert.getCommentary());
            statement.setInt(4, objectToInsert.getTimeSlot().getId());
            statement.setInt(5, objectToInsert.getBeneficiary().getId());
            statement.setInt(6, objectToInsert.getJobSkill().getId());
            statement.setInt(7, objectToInsert.getAcademicSkill().getId());
            statement.setInt(8, objectToInsert.getImportance());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next())
                objectToInsert.setId(generatedKeys.getInt(1));

            for (Interpreter interpreter : objectToInsert.getInterpreters())
                addInterpreterToMission(objectToInsert.getId(), interpreter.getId());
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Update a Mission line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Mission objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        List<Mission> missions = findAll();
        for (Mission line : missions) {
            if (line.equals(objectToUpdate))
                throw new AlreadyExistsException("Mission " + objectToUpdate.getSubject() + " already exists at id " + line.getId());
        }

        String query = "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?";
        query = String.format(query, TABLE, FIELD_SUBJECT, FIELD_STATE, FIELD_COMMENTARY, FIELD_TIME_SLOT, FIELD_JOB_SKILL, FIELD_ACADEMIC_SKILL, FIELD_IMPORTANCE, FIELD_ID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getSubject());
            statement.setString(2, objectToUpdate.getStateOfMission().toString());
            statement.setString(3, objectToUpdate.getCommentary());
            statement.setInt(4, objectToUpdate.getTimeSlot().getId());
            statement.setInt(5, objectToUpdate.getJobSkill().getId());
            statement.setInt(6, objectToUpdate.getAcademicSkill().getId());
            statement.setInt(7, objectToUpdate.getImportance());
            statement.setInt(8, objectToUpdate.getId());
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("Mission " + objectToUpdate.getSubject() + " of id " + objectToUpdate.getId() + " could not be found in database");
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Delete a Mission line in the table in the database
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Mission objectToDelete) throws NoSuchElementException, SQLException {
        String query = "DELETE FROM %s WHERE %s = ?";
        query = String.format(query, TABLE, FIELD_ID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("Mission " + objectToDelete.getSubject() + " was not found in database");
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Return all line of Mission table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Mission> findAll() throws SQLException {
        String query = "SELECT * FROM " + TABLE;
        PreparedStatement statement = null;
        ResultSet result = null;
        List<Mission> missions = new ArrayList<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                int missionId = result.getInt(FIELD_ID);
                MissionState state = MissionState.valueOf(result.getString(FIELD_STATE));
                TimeSlot timeSlot;
                if (state == MissionState.REGULAR) {
                    timeSlot = new DAOBaseTimeSlot().find(result.getInt(FIELD_TIME_SLOT));
                } else {
                    timeSlot = new DAOPunctualTimeSlot().find(result.getInt(FIELD_TIME_SLOT));
                }
                missions.add(new Mission(
                        missionId,
                        result.getString(FIELD_SUBJECT),
                        state,
                        result.getString(FIELD_COMMENTARY),
                        timeSlot,
                        result.getObject(FIELD_BENEFICIARY, Beneficiary.class),
                        new DAOLocation().getMissionLocation(missionId),
                        new DAOJobSkill().find(result.getInt(FIELD_JOB_SKILL)),
                        new DAOAcademicSkill().find(result.getInt(FIELD_ACADEMIC_SKILL)),
                        new DAOLocation().getMissionRoom(missionId),
                        result.getInt(FIELD_IMPORTANCE)
                ));
            }
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return missions;
    }

    /**
     * Return the schedule of the user with the given id for a specific week
     * @param idUser represent the id of the user which we want the schedule
     * @param year represent the year of the week
     * @param weekNumber represent the week number in the year (1-52)
     * @return a list of Mission which compose the schedule of the idUser for the given week, or an empty List if none was found
     * @throws SQLException if the database could not be reached
     */
    public List<Mission> getScheduleForWeek(int idUser, int year, int weekNumber) throws SQLException {
        LocalDate date = LocalDate.ofYearDay(year, 1)
                .with(WeekFields.ISO.weekOfYear(), weekNumber)
                .with(DayOfWeek.MONDAY);
        List<Mission> missions = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            missions.addAll(getScheduleForDay(idUser, date.plusDays(i)));
        }
        return missions;
    }

    /**
     * Return the schedule of the user with the given id for a specific day
     * @param idUser represent the id of the user which we want the schedule
     * @param date represent the specific day
     * @return a list of Mission which compose the schedule of the idUser for the given day, or an empty List if none was found
     * @throws SQLException if the database could not be reached
     */
    public List<Mission> getScheduleForDay(int idUser, LocalDate date) throws SQLException {
        List<Mission> missions = new ArrayList<>();
        String query = "SELECT m.id FROM " + TABLE + " m " +
                "JOIN TimeSlot ts ON m." + FIELD_TIME_SLOT + " = ts.id " +
                "WHERE (ts.day = ? " +
                "OR (ts.day IS NULL AND TRUNC(ts.startTime) = ?)) " +
                "AND (m.id IN (SELECT mission FROM InterpreterMission WHERE interpreter = ?) " +
                "OR m.id IN (SELECT mission FROM BeneficiaryMission WHERE beneficiary = ?))";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, date.getDayOfWeek().getValue() - 1);
            statement.setDate(2, java.sql.Date.valueOf(date));
            statement.setInt(3, idUser);
            statement.setInt(4, idUser);
            result = statement.executeQuery();
            while (result.next()) {
                Mission mission = find(result.getInt("id"));
                if (mission != null)
                    missions.add(mission);
            }
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return missions;
    }

    /**
     * Add an interpreter to a mission in the InterpreterMission table
     * @param missionId : id of the mission
     * @param interpreterId : id of the interpreter
     * @throws AlreadyExistsException if the interpreter is already linked to the mission
     * @throws SQLException if the database could not be reached
     * @post the interpreter is linked to the mission in the database
     */
    public void addInterpreterToMission(int missionId, int interpreterId) throws SQLException, AlreadyExistsException {
        String checkQuery = "SELECT * FROM InterpreterMission WHERE mission = ? AND interpreter = ?";
        String insertQuery = "INSERT INTO InterpreterMission(mission, interpreter) VALUES(?, ?)";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(checkQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, interpreterId);
            result = statement.executeQuery();
            if (result.next()) throw new AlreadyExistsException("This interpreter is already linked to the mission");

            statement = DatabaseConnector.getInstance().prepareStatement(insertQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, interpreterId);
            statement.executeUpdate();
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Remove an interpreter from a mission in the InterpreterMission table
     * @param missionId : id of the mission
     * @param interpreterId : id of the interpreter
     * @throws NoSuchElementException if the interpreter is not linked to the mission
     * @throws SQLException if the database could not be reached
     * @post the interpreter is no longer linked to the mission in the database
     */
    public void removeInterpreterFromMission(int missionId, int interpreterId) throws SQLException, NoSuchElementException {
        String deleteQuery = "DELETE FROM InterpreterMission WHERE mission = ? AND interpreter = ?";
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(deleteQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, interpreterId);
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("This interpreter is not linked to the mission");
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}