package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.BaseTimeSlot;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.Interpreter;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

public class DAOBaseTimeSlot extends DAO<BaseTimeSlot> {
    protected static final String TABLE = "BaseTimeSlotView";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_TIME_SLOT = "timeSLot";
    protected static final String FIELD_START_TIME = "startDateTime";
    protected static final String FIELD_END_TIME = "endDateTime";
    protected static final String FIELD_DAY = "day";

    @Override
    public BaseTimeSlot find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        BaseTimeSlot ret = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);

            result = statement.executeQuery();
            if (result.next())
                ret = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return ret;
    }

    @Override
    public void create(BaseTimeSlot objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert) >= 0)
            throw new AlreadyExistsException("BaseTimeSlot " + objectToInsert.getDay() + " already exists");

        String query = String.format("INSERT INTO %s VALUES(NULL, ?, ?, ?)", TABLE);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(objectToInsert.getStartDate(), objectToInsert.getStartTime())));
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(objectToInsert.getEndDate(), objectToInsert.getEndTime())));
            statement.setInt(3, objectToInsert.getDay().getValue());

            statement.executeUpdate();
            getNewAttributes(objectToInsert);
        } finally {
            closeStatement(statement);
        }
    }

    /**
     * Update the id of the new object inserted in the database
     * @param newObject the new object inserted in the database
     * @throws SQLException if the database could not be reached
     */
    private void getNewAttributes(BaseTimeSlot newObject) throws SQLException {
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ? AND %s = ? AND %s = ?",
                FIELD_ID, TABLE, FIELD_START_TIME, FIELD_END_TIME, FIELD_DAY
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(newObject.getStartDate(), newObject.getStartTime())));
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(newObject.getEndDate(), newObject.getEndTime())));
            statement.setInt(3, newObject.getDay().getValue());

            result = statement.executeQuery();
            if (result.next())
                newObject.setId(result.getInt(FIELD_ID));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    @Override
    public void update(BaseTimeSlot objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (checkAlreadyExists(objectToUpdate) >= 0)
            throw new AlreadyExistsException("This BaseTimeSlot already exists");

        String query = String.format(
                "UPDATE %s SET %s = ?, %s = ?, %s = ? WHERE %s = ?",
                TABLE, FIELD_START_TIME, FIELD_END_TIME, FIELD_DAY, FIELD_ID
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(objectToUpdate.getStartDate(), objectToUpdate.getStartTime())));
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(objectToUpdate.getEndDate(), objectToUpdate.getEndTime())));
            statement.setInt(3, objectToUpdate.getDay().getValue());
            statement.setInt(4, objectToUpdate.getId());

            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no BaseTimeSlot with the id " + objectToUpdate.getId());
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
                throw new NoSuchElementException("[ERROR] There is no BaseTimeSlot with the id " + idObjectToDelete);
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public Set<BaseTimeSlot> findAll() throws SQLException {
        String query = String.format("SELECT * FROM %s", TABLE);
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<BaseTimeSlot> ret = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);

            result = statement.executeQuery();
            while (result.next())
                ret.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return ret;
    }

    /**
     * @param  id the id of the interpreter to retrieve the availabilities of
     * @return a list of time slots during which interpreter is normally available
     * @throws IllegalArgumentException if interpreter's id does not match anything in database
     * @throws SQLException if a database error occurs
     */
    public Set<BaseTimeSlot> findAvailabilities(int id) throws IllegalArgumentException, SQLException {
        if (id < 0 || find(id) == null)
            throw new IllegalArgumentException("No object of id " + id + " could be found in database.");

        Set<BaseTimeSlot> ret = new HashSet<>();
        String query = "SELECT ts.%s, ts.%s, ts.%s, ts.%s " +
                "FROM %s i, %s av, %s ts " +
                "WHERE i.%s = ? AND i.%s = av.%s " +
                "AND av.%s = ts.%s";
        query = String.format(query, FIELD_ID, FIELD_START_TIME, FIELD_END_TIME, FIELD_DAY,
                DAOInterpreter.TABLE, DAOInterpreter.TABLE_AVAILABILITY, TABLE,
                DAOInterpreter.FIELD_ID, DAOInterpreter.FIELD_ID, DAOInterpreter.AVAILABILITY_REF_INTERPRETER,
                DAOInterpreter.AVAILABILITY_REF_TIMESLOT, FIELD_ID);
        try (PreparedStatement statement = DatabaseConnector.getInstance().prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    ret.add(new BaseTimeSlot(
                            result.getInt(FIELD_ID),
                            result.getDate(FIELD_START_TIME).toLocalDate(),
                            result.getDate(FIELD_END_TIME).toLocalDate(),
                            result.getTime(FIELD_START_TIME).toLocalTime(),
                            result.getTime(FIELD_END_TIME).toLocalTime(),
                            DayOfWeek.of(result.getInt(FIELD_DAY))
                    ));
                }
            }
        }
        return ret;
    }


    @Override
    protected int checkAlreadyExists(BaseTimeSlot objectToCheck) throws SQLException {
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ? AND %s = ? AND %s = ?",
                FIELD_ID, TABLE, FIELD_DAY, FIELD_START_TIME, FIELD_END_TIME
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToCheck.getDay().getValue());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(objectToCheck.getStartDate(), objectToCheck.getStartTime())));
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.of(objectToCheck.getEndDate(), objectToCheck.getEndTime())));

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
    protected BaseTimeSlot getResult(ResultSet result) throws SQLException {
        return new BaseTimeSlot(
                result.getInt(FIELD_ID),
                result.getDate(FIELD_START_TIME).toLocalDate(),
                result.getDate(FIELD_END_TIME).toLocalDate(),
                result.getTime(FIELD_START_TIME).toLocalTime(),
                result.getTime(FIELD_END_TIME).toLocalTime(),
                DayOfWeek.of(result.getInt(FIELD_DAY))
        );
    }
}