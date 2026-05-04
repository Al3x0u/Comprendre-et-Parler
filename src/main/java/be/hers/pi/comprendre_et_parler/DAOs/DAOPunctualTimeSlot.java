package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.PunctualTimeSlot;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

public class DAOPunctualTimeSlot extends DAO<PunctualTimeSlot> {

    protected static final String TABLE = "timeslot";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_START_TIME = "startDateTime";
    protected static final String FIELD_END_TIME = "endDateTime";

    @Override
    public PunctualTimeSlot find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE,  FIELD_ID
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        PunctualTimeSlot ret = null;
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
    public void create(PunctualTimeSlot objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert))
            throw new AlreadyExistsException("PunctualTimeSlot" + objectToInsert.getStartDate() + " to " + objectToInsert.getEndDate() +  " already exists");

        String query = String.format("INSERT INTO %s VALUES(NULL, ?, ?)", TABLE);
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query, new String[]{FIELD_ID});
            statement.setTimestamp(1, Timestamp.valueOf(objectToInsert.getStartDate()));
            statement.setTimestamp(2, Timestamp.valueOf(objectToInsert.getEndDate()));

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
    public void update(PunctualTimeSlot objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (checkAlreadyExists(objectToUpdate))
            throw new AlreadyExistsException("PunctualTimeSlot" + objectToUpdate.getStartDate() + " to " + objectToUpdate.getEndDate() +  " already exists");

        String query = String.format(
                "UPDATE %s SET %s = ?, %s = ? WHERE %s = ?",
                TABLE, FIELD_START_TIME, FIELD_END_TIME, FIELD_ID
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setTimestamp(1, Timestamp.valueOf(objectToUpdate.getStartDate()));
            statement.setTimestamp(2, Timestamp.valueOf(objectToUpdate.getEndDate()));
            statement.setInt(3, objectToUpdate.getId());

            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no PunctualTimeSlot with the id " + objectToUpdate.getId());
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
                throw new NoSuchElementException("[ERROR] There is no PunctualTimeSlot with the id " + idObjectToDelete);
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public Set<PunctualTimeSlot> findAll() throws SQLException {
        String query = String.format("SELECT * FROM %s", TABLE);
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<PunctualTimeSlot> ret = new HashSet<>();
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
    protected boolean checkAlreadyExists(PunctualTimeSlot object) throws SQLException {
        String query = String.format(
                "SELECT 1 FROM %s WHERE %s = ? AND %s = ?",
                TABLE, FIELD_START_TIME, FIELD_END_TIME
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setTimestamp(1, Timestamp.valueOf(object.getStartDate()));
            statement.setTimestamp(2, Timestamp.valueOf(object.getEndDate()));

            result = statement.executeQuery();
            return result.next();
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    @Override
    protected PunctualTimeSlot getResult(ResultSet result) throws SQLException {
        return new PunctualTimeSlot(
                result.getInt(FIELD_ID),
                result.getTimestamp(FIELD_START_TIME).toLocalDateTime(),
                result.getTimestamp(FIELD_END_TIME).toLocalDateTime()
        );
    }
}