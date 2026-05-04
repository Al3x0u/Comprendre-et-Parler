package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.BaseTimeSlot;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

public class DAOBaseTimeSlot extends DAO<BaseTimeSlot> {
    protected static final String TABLE = "timeslot";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_START_TIME = "startDateTime";
    protected static final String FIELD_END_TIME = "endDateTime";
    protected static final String FIELD_DAY = "day";

    @Override
    public BaseTimeSlot find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s IS NOT NULL AND %s = ?",
                TABLE, FIELD_DAY, FIELD_ID
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
        if (checkAlreadyExists(objectToInsert))
            throw new AlreadyExistsException("BaseTimeSlot" + objectToInsert.getDay() + " already exists");

        String query = String.format("INSERT INTO %s VALUES(NULL, ?, ?, ?)", TABLE);
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query, new String[]{FIELD_ID});
            statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(objectToInsert.getStartDate(), objectToInsert.getStartTime())));
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(objectToInsert.getEndDate(), objectToInsert.getEndTime())));
            statement.setInt(3, objectToInsert.getDay().getValue());

            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next())
                objectToInsert.setId(generatedKeys.getInt(1));
        } finally {
            closeResultSet(generatedKeys);
            closeStatement(statement);
        }
    }

    @Override
    public void update(BaseTimeSlot objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (checkAlreadyExists(objectToUpdate))
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
                "DELETE FROM %s WHERE %s = ? AND %s IS NOT NULL",
                TABLE, FIELD_ID, FIELD_DAY
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
        String query = String.format(
                "SELECT * FROM %s WHERE %s IS NOT NULL",
                TABLE, FIELD_DAY
        );
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

    @Override
    protected boolean checkAlreadyExists(BaseTimeSlot objectToCheck) throws SQLException {
        String query = String.format(
                "SELECT 1 FROM %s WHERE %s IS NOT NULL AND %s = ? AND %s = ? AND %s = ?",
                TABLE, FIELD_DAY, FIELD_DAY, FIELD_START_TIME, FIELD_END_TIME
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToCheck.getDay().getValue());
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(objectToCheck.getStartDate(), objectToCheck.getStartTime())));
            statement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.of(objectToCheck.getEndDate(), objectToCheck.getEndTime())));

            result = statement.executeQuery();
            return result.next();
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
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

    /**
     * Return all BaseTimeSlot of An Interpreter
     * @param idInterpreter represent the id of the interpreter that we want the BaseTimeSlot
     * @return  a Set who represent the BaseTimeSlot of the interpreter
     * @throws SQLException if the database could not be reached
     */
    public Set<BaseTimeSlot> findForInterpreter(int idInterpreter) throws SQLException, NoSuchElementException {
        return new HashSet<>();
    }
}