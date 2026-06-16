package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private static final DAOAcademicSkill daoAcademicSkill = new DAOAcademicSkill();
    private static final DAOJobSkill daoJobSkill = new DAOJobSkill();
    private static final DAOLocation daoLocation = new DAOLocation();
    private static final DAOBaseTimeSlot daoBaseTimeSlot = new DAOBaseTimeSlot();
    private static final DAOPunctualTimeSlot daoPunctualTimeSlot = new DAOPunctualTimeSlot();
    private static final DAOBeneficiary daoBeneficiary = new DAOBeneficiary();

    /**
     * overlap based on type between an existing slot and the new one,
     * both rows of the TimeSlot table. The "DAY" column defines the type:
     * NULL = PunctualTimeSlot (real date in startDateTime); 1-7 = BaseTimeSlot (weekday + validity window).
     */
    private static final String TIMESLOT_OVERLAP =
            "(" +
                    // punctual x punctual : exact datetime overlap
                    " (ts.\"DAY\" IS NULL AND tsNew.\"DAY\" IS NULL" +
                    "  AND ts.startDateTime < tsNew.endDateTime AND ts.endDateTime > tsNew.startDateTime)" +
                    " OR" +
                    // recurring x recurring : same weekday + time-of-day overlap + validity windows overlap
                    " (ts.\"DAY\" IS NOT NULL AND tsNew.\"DAY\" IS NOT NULL" +
                    "  AND ts.\"DAY\" = tsNew.\"DAY\"" +
                    "  AND (ts.startDateTime - TRUNC(ts.startDateTime)) < (tsNew.endDateTime - TRUNC(tsNew.endDateTime))" +
                    "  AND (ts.endDateTime - TRUNC(ts.endDateTime)) > (tsNew.startDateTime - TRUNC(tsNew.startDateTime))" +
                    "  AND TRUNC(ts.startDateTime) <= TRUNC(tsNew.endDateTime)" +
                    "  AND TRUNC(ts.endDateTime) >= TRUNC(tsNew.startDateTime))" +
                    " OR" +
                    // existing punctual x new recurring : punctual date inside window, same weekday, time-of-day overlap
                    " (ts.\"DAY\" IS NULL AND tsNew.\"DAY\" IS NOT NULL" +
                    "  AND TRUNC(ts.startDateTime) BETWEEN TRUNC(tsNew.startDateTime) AND TRUNC(tsNew.endDateTime)" +
                    "  AND (TRUNC(ts.startDateTime) - TRUNC(ts.startDateTime, 'IW') + 1) = tsNew.\"DAY\"" +
                    "  AND (ts.startDateTime - TRUNC(ts.startDateTime)) < (tsNew.endDateTime - TRUNC(tsNew.endDateTime))" +
                    "  AND (ts.endDateTime - TRUNC(ts.endDateTime)) > (tsNew.startDateTime - TRUNC(tsNew.startDateTime)))" +
                    " OR" +
                    // existing recurring x new punctual : symmetric
                    " (ts.\"DAY\" IS NOT NULL AND tsNew.\"DAY\" IS NULL" +
                    "  AND TRUNC(tsNew.startDateTime) BETWEEN TRUNC(ts.startDateTime) AND TRUNC(ts.endDateTime)" +
                    "  AND (TRUNC(tsNew.startDateTime) - TRUNC(tsNew.startDateTime, 'IW') + 1) = ts.\"DAY\"" +
                    "  AND (ts.startDateTime - TRUNC(ts.startDateTime)) < (tsNew.endDateTime - TRUNC(tsNew.endDateTime))" +
                    "  AND (ts.endDateTime - TRUNC(ts.endDateTime)) > (tsNew.startDateTime - TRUNC(tsNew.startDateTime)))" +
                    ")";

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
        completeBeneficiary(mission);

        return mission;
    }

    /**
     * Complete a mission with the beneficiary from the database
     * @param mission the mission to complete
     * @throws SQLException if a database error occurs
     */
    private void completeBeneficiary(Mission mission) throws SQLException {
        if (mission != null && mission.getBeneficiary() != null)
            mission.setBeneficiary(daoBeneficiary.find(mission.getBeneficiary().getId()));
    }

    // Does not update objectToInsert's id when throwing an AlreadyExistException
    @Override
    public void create(Mission objectToInsert) throws AlreadyExistsException, SQLException {
        // Create new TimeSlot if needed
        try {
            if (objectToInsert.getTimeSlot() instanceof PunctualTimeSlot pts)
                daoPunctualTimeSlot.create(pts);
            else if (objectToInsert.getTimeSlot() instanceof BaseTimeSlot bts)
                daoBaseTimeSlot.create(bts);
        } catch (AlreadyExistsException e) {}

        // Check for schedule overlaps with the new timeslot
        int conflictId = checkAlreadyExists(objectToInsert);
        if (conflictId >= 0)
            throw new AlreadyExistsException(String.valueOf(conflictId));

        // Create new Location if needed
        try {
            daoLocation.create(objectToInsert.getLocation());
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
                daoPunctualTimeSlot.create(pts);
            else if (objectToUpdate.getTimeSlot() instanceof BaseTimeSlot bts)
                daoBaseTimeSlot.create(bts);
        }
        catch (AlreadyExistsException e) {}

        // Check for schedule overlaps with the new timeslot
        int idInDB = checkAlreadyExists(objectToUpdate);
        if (idInDB >= 0)
            throw new AlreadyExistsException(String.valueOf(idInDB));

        // Create new Location if needed
        try {
            daoLocation.create(objectToUpdate.getLocation());
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
        }  finally {
            closeResultSet(result);
            closeStatement(statement);
        }

        for (Mission mis : missions)
            completeBeneficiary(mis);

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
        if (mission.getStateOfMission() == MissionState.CANCELED || mission.getStateOfMission() == MissionState.DENIED) {
            return -1;
        }

        boolean hasBeneficiary = mission.getBeneficiary() != null;
        boolean hasInterpreters = mission.getInterpreters() != null && !mission.getInterpreters().isEmpty();
        // Without any actor, nobody can be double-booked
        if (!hasBeneficiary && !hasInterpreters) {
            return -1;
        }

        StringBuilder query = new StringBuilder(
                "SELECT m."+ FIELD_ID +" FROM "+ TABLE +" m "+
                        "JOIN "+ DAOBaseTimeSlot.TABLE +" ts ON m."+ FIELD_TIME_SLOT +" = ts." + DAOBaseTimeSlot.FIELD_ID +
                        " JOIN "+ DAOBaseTimeSlot.TABLE +" tsNew ON tsNew." + DAOBaseTimeSlot.FIELD_ID + " = ?" +
                        // LEFT JOIN so a mission without an interpreter (e.g. a beneficiary-only request) stays a candidate
                        " LEFT JOIN "+ DAOMission.TABLE_INTERPRETER_MISSION +" im ON im."+ DAOMission.INTERPRETER_MISSION_REF_MISSION +" = m."+ FIELD_ID +
                        " WHERE m."+ FIELD_ID +" <> ? ");

        // states allowed to overlap are excluded
        query.append("AND m."+ FIELD_STATE +" <> "+ MissionState.DENIED.getValue() +" AND m."+ FIELD_STATE +" <> "+ MissionState.CANCELED.getValue());
        if (mission.getStateOfMission() == MissionState.PENDING) {
            query.append(" AND m." + FIELD_STATE + " <> " + MissionState.PENDING.getValue());
        }

        // timeslot overlap, type-aware (punctual / recurring / mixed)
        query.append(" AND ").append(TIMESLOT_OVERLAP);

        // shared actor:
        // a shared beneficiary is ALWAYS a conflict (a beneficiary can't be interpreted by two interpreters at once)
        // a shared interpreter is a conflict ONLY if a different place OR different required skills
        // (one interpreter can cover several beneficiaries in the same room for the same course)
        query.append(" AND (");
        if (hasBeneficiary) {
            query.append("m." + FIELD_BENEFICIARY + " = ?");
        }
        if (hasBeneficiary && hasInterpreters) {
            query.append(" OR ");
        }
        if (hasInterpreters) {
            query.append("(im." + DAOMission.INTERPRETER_MISSION_REF_INTERPRETER + " IN ( ?");
            for (int i = 1; i < mission.getInterpreters().size(); i++) {
                query.append(", ?");
            }
            query.append(" )");
            query.append(" AND (m." + FIELD_LOCATION + " <> ?"
                    + " OR NVL(m." + FIELD_JOB_SKILL + ", -1) <> ?"
                    + " OR NVL(m." + FIELD_ACADEMIC_SKILL + ", -1) <> ?))");
        }
        query.append(")");

        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            int field = 1;
            statement = DatabaseConnector.getInstance().prepareStatement(query.toString());
            statement.setInt(field++, mission.getTimeSlot().getId());// tsNew.id = ?
            statement.setInt(field++, mission.getId());// m.id <> ?
            if (hasBeneficiary) {
                statement.setInt(field++, mission.getBeneficiary().getId());
            }
            if (hasInterpreters) {
                for (Interpreter i : mission.getInterpreters()) {
                    statement.setInt(field++, i.getId());
                }
                statement.setInt(field++, mission.getLocation().getId());                                              // m.location <> ?
                statement.setInt(field++, mission.getJobSkill() != null ? mission.getJobSkill().getId() : -1);         // jobSkill
                statement.setInt(field++, mission.getAcademicSkill() != null ? mission.getAcademicSkill().getId() : -1); // academicSkill
            }

            result = statement.executeQuery();
            if (result.next()) {
                return result.getInt(FIELD_ID);
            }
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return -1;
    }

    /**
     * Sum of the weekly hours of the interpreter's recurring (ACCEPTED or REGULAR) missions whose
     * validity window overlaps [windowStart, windowEnd]. One occurrence per week is counted, so this
     * is the interpreter's stable weekly load from the base schedule over that window.
     * @param interpreterId the interpreter id
     * @param windowStart start of the new mission's validity window
     * @param windowEnd end of the new mission's validity window
     * @return the total recurring weekly hours, or 0 if none
     * @throws SQLException if the database could not be reached
     */
    public double getRecurringWeeklyHours(int interpreterId, LocalDate windowStart, LocalDate windowEnd) throws SQLException {
        String query =
                "SELECT NVL(SUM(((ts.endDateTime - TRUNC(ts.endDateTime)) - (ts.startDateTime - TRUNC(ts.startDateTime))) * 24), 0) " +
                        "FROM " + TABLE + " m " +
                        "JOIN " + DAOBaseTimeSlot.TABLE + " ts ON m." + FIELD_TIME_SLOT + " = ts." + DAOBaseTimeSlot.FIELD_ID + " " +
                        "JOIN " + TABLE_INTERPRETER_MISSION + " im ON im." + INTERPRETER_MISSION_REF_MISSION + " = m." + FIELD_ID + " " +
                        "WHERE im." + INTERPRETER_MISSION_REF_INTERPRETER + " = ? " +
                        "AND m." + FIELD_STATE + " IN (" + MissionState.ACCEPTED.getValue() + ", " + MissionState.REGULAR.getValue() + ") " +
                        "AND ts.\"DAY\" IS NOT NULL " +
                        "AND TRUNC(ts.startDateTime) <= ? AND TRUNC(ts.endDateTime) >= ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, interpreterId);
            statement.setDate(2, java.sql.Date.valueOf(windowEnd));
            statement.setDate(3, java.sql.Date.valueOf(windowStart));
            result = statement.executeQuery();
            if (result.next())
                return result.getDouble(1);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return 0;
    }

    @Override
    protected Mission getResult(ResultSet result) throws SQLException {
        BaseTimeSlot baseTimeSlot = daoBaseTimeSlot.find(result.getInt(FIELD_TIME_SLOT));
        PunctualTimeSlot punctualTimeSlot = daoPunctualTimeSlot.find(result.getInt(FIELD_TIME_SLOT));
        TimeSlot timeSlot = (baseTimeSlot != null) ? baseTimeSlot : punctualTimeSlot;

        return new Mission(
                result.getInt(FIELD_ID),
                result.getString(FIELD_SUBJECT),
                MissionState.fromValue(result.getInt(FIELD_STATE)),
                result.getString(FIELD_COMMENTARY),
                timeSlot,
                new Beneficiary(result.getInt(FIELD_BENEFICIARY)),
                daoLocation.find(result.getInt(FIELD_LOCATION)),
                daoJobSkill.find(result.getInt(FIELD_JOB_SKILL)),
                daoAcademicSkill.find(result.getInt(FIELD_ACADEMIC_SKILL)),
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
                "JOIN " + DAOPunctualTimeSlot.TABLE + " ts ON m." + FIELD_TIME_SLOT + " = ts." + DAOPunctualTimeSlot.FIELD_ID +
                " WHERE ( " +
                "(ts." + DAOBaseTimeSlot.FIELD_DAY +" IS NOT NULL) " +
                "OR (ts." + DAOBaseTimeSlot.FIELD_DAY + " IS NULL AND ts." + DAOPunctualTimeSlot.FIELD_START_TIME +" BETWEEN ? AND ?)" +
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

        for (Mission mis : missions)
            completeBeneficiaryLight(mis);

        return missions;
    }

    /**
     * Complete a mission with a lightweight version of the beneficiary from the database
     * @param mission the mission to complete
     * @throws SQLException if a database error occurs
     */
    private void completeBeneficiaryLight(Mission mission) throws SQLException {
        if (mission != null && mission.getBeneficiary() != null)
            mission.setBeneficiary(daoBeneficiary.findLight(mission.getBeneficiary().getId()));
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
            "(m." +FIELD_BENEFICIARY+ " = ? "+ // idUser is assigned as a Beneficiary
                "OR m." +FIELD_ID+ " IN (SELECT " +INTERPRETER_MISSION_REF_MISSION+ " FROM " +TABLE_INTERPRETER_MISSION+
                " WHERE " +INTERPRETER_MISSION_REF_INTERPRETER+ " = ?)" + // idUser is assigned as an Interpreter
        ") AND (");
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
        query.append(" OR (ts." +DAOBaseTimeSlot.FIELD_DAY+ " IS NULL AND ts." +DAOPunctualTimeSlot.FIELD_START_TIME+ " >= ? AND " +DAOPunctualTimeSlot.FIELD_END_TIME+" < ?))");

        PreparedStatement statement = null;
        ResultSet result = null;
        Set<Mission> missions = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query.toString());
            int field = 1;
            statement.setInt(field++, idUser);
            statement.setInt(field++, idUser);
            if (start.plusWeeks(1).isAfter(end)) {
                for (DayOfWeek day = start.getDayOfWeek(); day != end.getDayOfWeek(); day = day.plus(1)) {
                    statement.setInt(field++, day.getValue());
                }
            }
            statement.setDate(field++, java.sql.Date.valueOf(start));
            statement.setDate(field++, java.sql.Date.valueOf(end));
            result = statement.executeQuery();
            while (result.next()) {
                missions.add(getResult(result));
            }
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }

        for (Mission mis : missions)
            completeBeneficiaryLight(mis);

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

        String query = "SELECT m.* FROM " + TABLE + " m " +
                "WHERE m." + FIELD_ID + " IN " +
                "(SELECT " + INTERPRETER_MISSION_REF_MISSION + " FROM " + TABLE_INTERPRETER_MISSION +
                " WHERE " + INTERPRETER_MISSION_REF_INTERPRETER + " = ?)";

        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, interpreterId);
            result = statement.executeQuery();
            while (result.next()) {
                missions.add(getResult(result));
            }
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }

        for (Mission mis : missions)
            completeBeneficiary(mis);

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
                " JOIN " + DAOPunctualTimeSlot.TABLE+ " ts ON ts." + DAOPunctualTimeSlot.FIELD_ID + " = " + TABLE + "." + FIELD_TIME_SLOT +
                " WHERE " + FIELD_BENEFICIARY + " = ?" +
                " AND ts." + DAOPunctualTimeSlot.FIELD_END_TIME + " >= SYSDATE" +
                " AND " + FIELD_STATE + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, beneficiaryId);
            statement.setInt(2, MissionState.ACCEPTED.getValue());

            result = statement.executeQuery();
            return result.next();
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    /**
     * Returns a list of missions filtered according to the given filter.
     * @param filter the filter to apply, each criterion is optional (null means no filter)
     * @return a Set of Mission matching the filter, or an empty Set if none was found
     * @throws SQLException if the database could not be reached
     */
    public Set<Mission> getByFilter(MissionFilter filter, LocalDateTime start, LocalDateTime end) throws SQLException {
        String query = "SELECT * FROM " + TABLE + " m JOIN " +
                DAOPunctualTimeSlot.TABLE + " ts ON ts." + DAOPunctualTimeSlot.FIELD_ID + " = m." + FIELD_TIME_SLOT +
                " %s %s %s " +
                "ts." + DAOPunctualTimeSlot.FIELD_START_TIME + " >= ? " +
                "AND ts." + DAOPunctualTimeSlot.FIELD_END_TIME + " <= ?";
        query = String.format(query,
                filter.getInterpreter() != null && filter.getInterpreter().getId() != -1 ?
                        "JOIN " + TABLE_INTERPRETER_MISSION + " i ON m." + FIELD_ID + " = i." + INTERPRETER_MISSION_REF_MISSION
                                + " WHERE i." + INTERPRETER_MISSION_REF_INTERPRETER + " = ? AND"
                        : "WHERE",
                filter.getBeneficiary() != null && filter.getBeneficiary().getId() != -1 ? FIELD_BENEFICIARY + " = ? AND" : "",
                filter.getStateOfMission() != null ? FIELD_STATE + " = ? AND" : ""
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<Mission> missions = new HashSet<>();

        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            int field = 1;
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            if (filter.getBeneficiary() != null)
                statement.setInt(field++, filter.getBeneficiary().getId());
            if (filter.getStateOfMission() != null)
                statement.setInt(field++, filter.getStateOfMission().getValue());
            if (filter.getInterpreter() != null)
                statement.setInt(field++, filter.getInterpreter().getId());
            statement.setTimestamp(field++, Timestamp.valueOf(start));
            statement.setTimestamp(field, Timestamp.valueOf(end));

            result = statement.executeQuery();
            while (result.next())
                missions.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }

        for (Mission mis : missions)
            completeBeneficiary(mis);

        return missions;
    }
}