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

    protected static final String TABLE_INTERPRETER_MISSION = "interpreterMission";
    protected static final String INTERPRETER_MISSION_REF_MISSION = "mission";
    protected static final String INTERPRETER_MISSION_REF_INTERPRETER = "interpreter";


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
                mission = getResult(result);
            }
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }

        // Complete Beneficiary objects
        if (mission != null && mission.getBeneficiary() != null)
            mission.setBeneficiary(new DAOBeneficiary().find(mission.getBeneficiary().getId()));

        return mission;
    }

    // Does not update objectToInsert's id when throwing an AlreadyExistException
    @Override
    public void create(Mission objectToInsert) throws AlreadyExistsException, SQLException {
        // Create new TimeSlot if needed
        try {
            if (objectToInsert.getTimeSlot() instanceof PunctualTimeSlot pts)
                new DAOPunctualTimeSlot().create(pts);
            else if (objectToInsert.getTimeSlot() instanceof BaseTimeSlot bts)
                new DAOBaseTimeSlot().create(bts);
        } catch (AlreadyExistsException e) {}

        // Check for schedule overlaps with the new timeslot
        if (checkAlreadyExists(objectToInsert) >= 0)
            throw new AlreadyExistsException("Mission overlaps with an existing mission");

        // Create new Location if needed
        try {
            new DAOLocation().create(objectToInsert.getLocation());
        } catch (AlreadyExistsException e) {}

        // Create Mission
        String query = "INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        query = String.format(query, TABLE, FIELD_SUBJECT, FIELD_STATE, FIELD_COMMENTARY, FIELD_TIME_SLOT, FIELD_BENEFICIARY,
                FIELD_LOCATION, FIELD_ROOM, FIELD_JOB_SKILL, FIELD_ACADEMIC_SKILL, FIELD_IMPORTANCE);
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query, new String[]{FIELD_ID});
            statement.setString(1, objectToInsert.getSubject());
            statement.setInt(2, objectToInsert.getStateOfMission().getValue());
            statement.setString(3, objectToInsert.getCommentary());
            statement.setInt(4, objectToInsert.getTimeSlot().getId());

            if (objectToInsert.getBeneficiary() == null)
                statement.setNull(5, Types.INTEGER);
            else
                statement.setInt(5, objectToInsert.getBeneficiary().getId());

            statement.setInt(6, objectToInsert.getLocation().getId());
            statement.setString(7, objectToInsert.getRoom());

            if(objectToInsert.getJobSkill() == null)
                statement.setNull(8, Types.INTEGER);
            else
                statement.setInt(8, objectToInsert.getJobSkill().getId());
            if (objectToInsert.getAcademicSkill() == null)
                statement.setNull(9, Types.INTEGER);
            else
                statement.setInt(9, objectToInsert.getAcademicSkill().getId());

            statement.setInt(10, objectToInsert.getImportance());

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
            throw new NoSuchElementException("Mission " + objectToUpdate.getSubject() + " of id " + objectToUpdate.getId() + " could not be found in database");

        // Create new TimeSlot if needed
        try {
            if (objectToUpdate.getTimeSlot() instanceof PunctualTimeSlot pts)
                new DAOPunctualTimeSlot().create(pts);
            else if (objectToUpdate.getTimeSlot() instanceof BaseTimeSlot bts)
                new DAOBaseTimeSlot().create(bts);
        }
        catch (AlreadyExistsException e) {}

        // Check for schedule overlaps with the new timeslot
        int idInDB = checkAlreadyExists(objectToUpdate);
        if (idInDB != objectToUpdate.getId() && idInDB >= 0)
            throw new AlreadyExistsException("Mission overlaps with an existing mission");

        // Create new Location if needed
        try {
            new DAOLocation().create(objectToUpdate.getLocation());
        } catch (AlreadyExistsException e) {}

        // Update Mission
        String query = "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?";
        query = String.format(query, TABLE, FIELD_SUBJECT, FIELD_STATE, FIELD_COMMENTARY, FIELD_TIME_SLOT, FIELD_LOCATION,
                FIELD_ROOM, FIELD_BENEFICIARY, FIELD_JOB_SKILL, FIELD_ACADEMIC_SKILL, FIELD_IMPORTANCE, FIELD_ID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getSubject());
            statement.setInt(2, objectToUpdate.getStateOfMission().getValue());
            statement.setString(3, objectToUpdate.getCommentary());
            statement.setInt(4, objectToUpdate.getTimeSlot().getId());
            statement.setInt(5, objectToUpdate.getLocation().getId());
            statement.setString(6, objectToUpdate.getRoom());

            if(objectToUpdate.getBeneficiary() == null)
                statement.setNull(7, Types.INTEGER);
            else
                statement.setInt(7, objectToUpdate.getBeneficiary().getId());
            if(objectToUpdate.getJobSkill() == null)
                statement.setNull(8, Types.INTEGER);
            else
                statement.setInt(8, objectToUpdate.getJobSkill().getId());
            if (objectToUpdate.getAcademicSkill() == null)
                statement.setNull(9, Types.INTEGER);
            else
                statement.setInt(9, objectToUpdate.getAcademicSkill().getId());

            statement.setInt(10, objectToUpdate.getImportance());
            statement.setInt(11, objectToUpdate.getId());

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
    public void delete(int objectToDelete) throws NoSuchElementException, SQLException {
        String query = "DELETE FROM %s WHERE %s = ?";
        query = String.format(query, TABLE, FIELD_ID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToDelete);
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("Mission " + objectToDelete + " was not found in database");
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public Set<Mission> findAll() throws SQLException {
        String query = "SELECT * FROM " + TABLE;
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<Mission> missions = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                missions.add(getResult(result));
            }
        }
        finally {
            closeResultSet(result);
            closeStatement(statement);
        }

        // Complete Beneficiary objects
        for (Mission mis : missions) {
            if (mis.getBeneficiary() != null)
                mis.setBeneficiary(new DAOBeneficiary().find(mis.getBeneficiary().getId()));
        }

        return missions;
    }

    /**
     * Check if an overlapping mission exists in the database. <br>
     * Missions overlap if they share an interpreter or beneficiary AND their timeslots overlap AND their MissionStates are not allowed to overlap. <br>
     * CANCELED or DENIED missions can overlap with anything. <br>
     * PENDING missions can overlap other PENDING missions. <br>
     * Missions cannot overlap with themselves.
     * @param mission the object to check.
     * @return the id of the object found in DB, or -1 if none was found
     * @throws SQLException if the database could not be reached
     */
    @Override
    protected int checkAlreadyExists(Mission mission) throws SQLException {
        if (mission.getStateOfMission() != MissionState.CANCELED && mission.getStateOfMission() != MissionState.DENIED)
            return -1;

        // Find Missions in states that are not allowed to overlap, sharing an interpreter or beneficiary with mission, and for which timeslots overlap
        StringBuilder query = new StringBuilder(
            "SELECT m."+ FIELD_ID +" FROM "+ TABLE +" m "+
            "JOIN "+ DAOBaseTimeSlot.TABLE +" ts ON m."+ FIELD_TIME_SLOT +" = ts." + DAOBaseTimeSlot.FIELD_ID +
            " JOIN "+ DAOBaseTimeSlot.TABLE +" tsNew ON tsNew." + DAOBaseTimeSlot.FIELD_ID + " = ?" +
            " JOIN "+ DAOMission.TABLE_INTERPRETER_MISSION +" im ON im."+ DAOMission.INTERPRETER_MISSION_REF_MISSION +" = m."+ FIELD_ID +
            " WHERE "+

            // mission is not the one we're checking against
            "m."+ FIELD_ID +" <> ? ");

            // state is not allowed to overlap
            query.append("AND m."+ FIELD_STATE +" <> "+ MissionState.DENIED.getValue() +" AND m."+ FIELD_STATE +" <> "+ MissionState.CANCELED.getValue());
            if (mission.getStateOfMission() == MissionState.PENDING)
                // exclude PENDING missions since they can overlap with each other
                query.append(" AND m."+ FIELD_STATE +" <> "+ MissionState.PENDING.getValue());


            // timeslots overlap
            // TODO : handle BaseTimeSlots (check for day and truncate date from time fields)
            query.append(
            " AND ts."+ DAOBaseTimeSlot.FIELD_START_TIME +" < tsNew." + DAOBaseTimeSlot.FIELD_END_TIME +
            " AND ts."+ DAOBaseTimeSlot.FIELD_END_TIME +" > tsNew." + DAOBaseTimeSlot.FIELD_START_TIME +
            " AND (" +
                // beneficiary is shared (if there is one assigned)
                "m." + FIELD_BENEFICIARY);
                query.append((mission.getBeneficiary() == null ? " IS NULL " : " = ? "));

                // any interpreter is shared
                if (mission.getInterpreters() != null && !mission.getInterpreters().isEmpty()) {
                    query.append("OR im."+ DAOMission.INTERPRETER_MISSION_REF_INTERPRETER + " IN ( ?");
                    for(int i = 1; i < mission.getInterpreters().size(); i++){
                        query.append(", ?");
                    }
                    query.append(" )");
                }
            query.append(")");

        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            int field = 1; // variable number of fields depending on assigned interpreters and beneficiary
            statement = DatabaseConnector.getInstance().prepareStatement(query.toString());
            statement.setInt(field++, mission.getTimeSlot().getId());
            statement.setInt(field++, mission.getId());
            if (mission.getBeneficiary() != null)
                statement.setInt(field++, mission.getBeneficiary().getId());
            if (mission.getInterpreters() != null) {
                for (Interpreter i : mission.getInterpreters()) {
                    statement.setInt(field++, i.getId());
                }
            }
            System.out.println(query);
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
        BaseTimeSlot baseTimeSlot = new DAOBaseTimeSlot().find(result.getInt(FIELD_TIME_SLOT));
        PunctualTimeSlot punctualTimeSlot = new DAOPunctualTimeSlot().find(result.getInt(FIELD_TIME_SLOT));
        TimeSlot timeSlot = (baseTimeSlot != null) ? baseTimeSlot : punctualTimeSlot;

        return new Mission(
                result.getInt(FIELD_ID),
                result.getString(FIELD_SUBJECT),
                MissionState.fromValue(result.getInt(FIELD_STATE)),
                result.getString(FIELD_COMMENTARY),
                timeSlot,
                new Beneficiary(result.getInt(FIELD_BENEFICIARY)),
                new DAOLocation().find(result.getInt(FIELD_LOCATION)),
                new DAOJobSkill().find(result.getInt(FIELD_JOB_SKILL)),
                new DAOAcademicSkill().find(result.getInt(FIELD_ACADEMIC_SKILL)),
                result.getString(FIELD_ROOM),
                result.getInt(FIELD_IMPORTANCE)
        );
    }

    /**
     * Return the schedule for a specific week
     * Beneficiary data is partial and only includes id, firstName and lastName
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
                "(ts.day IS NOT NULL) " +
                "OR (ts.day IS NULL AND ts." + DAOPunctualTimeSlot.FIELD_START_TIME +" BETWEEN ? AND ?)" +
                ")";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setDate(1, java.sql.Date.valueOf(start));
            statement.setDate(2, java.sql.Date.valueOf(end.plusDays(1)));

            result = statement.executeQuery();
            while (result.next())
                missions.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }

        // Add id and name to Beneficiary objects
        for (Mission mis : missions) {
            if (mis.getBeneficiary() != null)
                mis.setBeneficiary(new DAOBeneficiary().findLight(mis.getBeneficiary().getId()));
        }
        return missions;
    }

    /**
     * Return the schedule of the user with the given id for a specific time frame <br>
     * Beneficiary data is partial and only includes id, firstName and lastName <br>
     * @param idUser the id of the user for whom we want the schedule
     * @param start the lower boundary, included
     * @param end the upper boundary, excluded
     * @return a Set of Mission which compose the schedule of the idUser during [start, end[, or an empty Set if none was found
     * @throws SQLException if a database error occurs
     */
    public Set<Mission> getScheduleBetween(int idUser, LocalDate start, LocalDate end) throws SQLException {
        StringBuilder query = new StringBuilder(
        "SELECT m.* FROM " +TABLE+ " m " +
        "JOIN " +DAOPunctualTimeSlot.TABLE+ " ts ON m." +FIELD_TIME_SLOT+ " = ts." +DAOPunctualTimeSlot.FIELD_ID+
        " WHERE " +
            // Mission is assigned to idUser
            "(m." +FIELD_ID+ " IN (" +
                "SELECT " +INTERPRETER_MISSION_REF_MISSION+ " FROM " +TABLE_INTERPRETER_MISSION+
                " WHERE " +INTERPRETER_MISSION_REF_INTERPRETER+ " = ?)" + // idUser is assigned as an Interpreter
            "OR m." +FIELD_BENEFICIARY+ " = ?) " + // idUser is assigned as a Beneficiary
        "AND ");
        // Mission is base and happens between start and end dates
        if (!start.plusWeeks(1).isAfter(end)) { // If the range covers a whole week, include all BaseTimeSlots
            query.append("ts." +DAOBaseTimeSlot.FIELD_DAY+ " IS NOT NULL");
        }
        else {
            query.append("ts." +DAOBaseTimeSlot.FIELD_DAY+ " IN ( ?");
            for (DayOfWeek day = start.getDayOfWeek().plus(1); day != end.getDayOfWeek(); day = day.plus(1)) {
                query.append(", ?");
            }
            query.append(")");
        }
        // OR Mission is punctual and happens between start and end dates
        query.append(" OR (ts." +DAOBaseTimeSlot.FIELD_DAY+ " IS NULL AND ts." +DAOPunctualTimeSlot.FIELD_START_TIME+ " >= ? AND " +DAOPunctualTimeSlot.FIELD_END_TIME+" < ?)");

        System.out.println(query);

        PreparedStatement statement = null;
        ResultSet result = null;
        Set<Mission> missions = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query.toString());
            statement.setDate(1, java.sql.Date.valueOf(start));
            statement.setDate(2, java.sql.Date.valueOf(end));
            statement.setInt(3, idUser);
            statement.setInt(4, idUser);
            result = statement.executeQuery();
            while (result.next()) {
                missions.add(getResult(result));
            }
        }
        finally {
            closeResultSet(result);
            closeStatement(statement);
        }

        // Add id and name to Beneficiary objects
        for (Mission mis : missions) {
            if (mis.getBeneficiary() != null)
                mis.setBeneficiary(new DAOBeneficiary().findLight(mis.getBeneficiary().getId()));
        }

        return missions;
    }

    /**
     * Return the schedule of the user with the given id for a specific day
     * Beneficiary data is partial and only includes id, firstName and lastName
     * @param idUser represent the id of the user which we want the schedule
     * @param date represent the specific day
     * @return a Set of Mission which compose the schedule of the idUser for the given day, or an empty Set if none was found
     * @throws SQLException if the database could not be reached
     */
    public Set<Mission> getScheduleForDay(int idUser, LocalDate date) throws SQLException {
        return getScheduleBetween(idUser, date, date.plusDays(1));
    }

    /**
     * Return the schedule of the user with the given id for a specific week
     * Beneficiary data is partial and only includes id, firstName and lastName
     * @param idUser represent the id of the user which we want the schedule
     * @param year represent the year of the week
     * @param weekNumber represent the week number in the year (1-52)
     * @return a Set of Mission which compose the schedule of the idUser for the given week, or an empty Set if none was found
     * @throws SQLException if the database could not be reached
     */
    public Set<Mission> getScheduleForWeek(int idUser, int year, int weekNumber) throws SQLException {
        LocalDate monday = LocalDate.ofYearDay(year, 1)
                .with(WeekFields.ISO.weekOfYear(), weekNumber)
                .with(DayOfWeek.MONDAY);

        return getScheduleBetween(idUser, monday, monday.plusWeeks(1));
    }

    /**
     * Return all missions assigned to the interpreter with the given id
     * @param interpreterId the id of the interpreter
     * @return a Set of Mission assigned to the interpreter, or an empty Set if none was found
     * @throws SQLException if the database could not be reached
     */
    public Set<Mission> findByInterpreter(int interpreterId) throws SQLException {
        Set<Mission> missions = new HashSet<>();

        String query = "SELECT m.id FROM " + TABLE + " m " +
                "WHERE m.id IN " +
                "(SELECT " + INTERPRETER_MISSION_REF_MISSION + " FROM " + TABLE_INTERPRETER_MISSION +
                " WHERE " + INTERPRETER_MISSION_REF_INTERPRETER + " = ?)";

        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, interpreterId);
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
     * @param missionId : id of the mission
     * @param interpreterId : id of the interpreter
     * @throws AlreadyExistsException if the interpreter is already linked to the mission
     * @throws SQLException if the database could not be reached
     * @post the interpreter is linked to the mission in the database
     */
    public void addInterpreterToMission(int missionId, int interpreterId) throws SQLException, AlreadyExistsException {
        String checkQuery = "SELECT * FROM "+ TABLE_INTERPRETER_MISSION +" WHERE "+ INTERPRETER_MISSION_REF_MISSION +" = ? AND "+ INTERPRETER_MISSION_REF_INTERPRETER +" = ?";
        String insertQuery = "INSERT INTO "+ TABLE_INTERPRETER_MISSION +"("+ INTERPRETER_MISSION_REF_MISSION +", "+ INTERPRETER_MISSION_REF_INTERPRETER +") VALUES(?, ?)";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(checkQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, interpreterId);
            result = statement.executeQuery();
            if (result.next()) throw new AlreadyExistsException("This interpreter is already linked to the mission");

            closeStatement(statement);
            statement = DatabaseConnector.getInstance().prepareStatement(insertQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, interpreterId);
            statement.executeUpdate();
        }
        finally {
            closeResultSet(result);
            closeStatement(statement);
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
        String deleteQuery = "DELETE FROM "+ TABLE_INTERPRETER_MISSION +" WHERE "+ INTERPRETER_MISSION_REF_MISSION +" = ? AND "+ INTERPRETER_MISSION_REF_INTERPRETER +" = ?";
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(deleteQuery);
            statement.setInt(1, missionId);
            statement.setInt(2, interpreterId);
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("This interpreter is not linked to the mission");
        }
        finally {
            closeStatement(statement);
        }
    }

    /**
     * Delete all interpreters linked to a mission in the InterpreterMission table
     * @param missionId : id of the mission
     * @throws SQLException if the database could not be reached
     * @post all interpreters linked to the mission have been deleted from the database
     */
    private void deleteAllInterpretersFromMission(int missionId) throws SQLException {
        String query = "DELETE FROM "+ TABLE_INTERPRETER_MISSION +" WHERE "+ INTERPRETER_MISSION_REF_MISSION +" = ?";
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, missionId);
            statement.executeUpdate();
        }
        finally {
            closeStatement(statement);
        }
    }

    /**
     * Check if a Beneficiary has active missions.
     * @param beneficiaryId the unique identifier of the beneficiary to retrieve
     * @throws SQLException if a database access error occurs
     * @return True if the beneficiary with the given ID has active missions false otherwise
     */
    public boolean hasActiveMissions(int beneficiaryId) throws SQLException {
        String query = "SELECT 1 FROM " + TABLE +
                " JOIN TimeSlot ts ON ts.id = " + TABLE + "." + FIELD_TIME_SLOT +
                " WHERE " + FIELD_BENEFICIARY + " = ?" +
                " AND TRUNC(ts.startDateTime, 'IW') = TRUNC(SYSDATE, 'IW')";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, beneficiaryId);
            result = statement.executeQuery();
            return result.next();
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }
}