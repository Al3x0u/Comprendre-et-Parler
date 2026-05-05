package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.Status;

import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DAOStatus extends DAO<Status> {
    protected static final String TABLE = "status";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_DESIGNATION = "designation";
    protected static final String FIELD_HOUR_QUOTA = "hourquota";

    @Override
    public Status find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        Status ret = null;
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
    public void create(Status objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert) >= 0)
            throw new AlreadyExistsException("Status" + objectToInsert.getDesignation() +  " already exists");

        String query = String.format("INSERT INTO %s VALUES (NULL, ?, ?)", TABLE);
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query, new String[]{FIELD_ID});
            statement.setString(1, objectToInsert.getDesignation());
            statement.setInt(2, objectToInsert.getHourQuota());

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
    public void update(Status objectToUpdate) throws NoSuchElementException, AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToUpdate) >= 0)
            throw new AlreadyExistsException("Status " + objectToUpdate.getDesignation() + " already exists");

        String query = String.format(
                "UPDATE %s SET %s = ?, %s = ? WHERE %s = ?",
                TABLE, FIELD_DESIGNATION, FIELD_HOUR_QUOTA, FIELD_ID
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getDesignation());
            statement.setInt(2, objectToUpdate.getHourQuota());
            statement.setInt(3, objectToUpdate.getId());

            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no Status with the id " + objectToUpdate.getId());
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
                throw new NoSuchElementException("[ERROR] There is no Status with the id " + idObjectToDelete);
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public Set<Status> findAll() throws SQLException {
        String query = String.format("SELECT * FROM %s", TABLE);
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<Status> ret = new HashSet<Status>();
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
    protected int checkAlreadyExists(Status object) throws SQLException {
        String query = String.format(
                "SELECT 1 FROM %s WHERE %s = ? AND %s = ? ",
                TABLE, FIELD_DESIGNATION, FIELD_HOUR_QUOTA
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, object.getDesignation());
            statement.setInt(2, object.getHourQuota());

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
    protected Status getResult(ResultSet result) throws SQLException {
        return new Status(
                result.getInt(FIELD_ID),
                result.getString(FIELD_DESIGNATION),
                result.getInt(FIELD_HOUR_QUOTA)
        );
    }
}