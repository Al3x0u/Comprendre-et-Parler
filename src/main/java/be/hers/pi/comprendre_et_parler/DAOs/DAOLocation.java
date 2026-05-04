package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Location;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

public class DAOLocation extends DAO<Location> {
    protected static final String TABLE = "location";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_DESIGNATION = "designation";
    protected static final String FIELD_CITY = "city";
    protected static final String FIELD_STREET = "street";
    protected static final String FIELD_STREET_NUMBER = "streetNumber";
    protected static final String FIELD_BOX = "box";

    @Override
    public Location find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        Location location = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);

            result = statement.executeQuery();
            if (result.next())
                location = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return location;
    }

    @Override
    public void create(Location objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert))
            throw new AlreadyExistsException("Location " + objectToInsert.getDesignation() + " already exists");

        String query = String.format("INSERT INTO %s VALUES(NULL, ?, ?, ?, ?, ?)", TABLE);
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query, new String[]{FIELD_ID});
            statement.setString(1, objectToInsert.getDesignation());
            statement.setInt(2, objectToInsert.getCity().getId());
            statement.setString(3, objectToInsert.getStreet());
            statement.setString(4, objectToInsert.getStreetNumber());
            statement.setInt(5, objectToInsert.getBox());

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
    public void update(Location objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (checkAlreadyExists(objectToUpdate))
            throw new AlreadyExistsException("Location " + objectToUpdate.getDesignation() + " already exists");

        String query = String.format(
                "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
                TABLE, FIELD_DESIGNATION, FIELD_CITY, FIELD_STREET, FIELD_STREET_NUMBER, FIELD_BOX, FIELD_ID
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getDesignation());
            statement.setInt(2, objectToUpdate.getCity().getId());
            statement.setString(3, objectToUpdate.getStreet());
            statement.setString(4, objectToUpdate.getStreetNumber());
            statement.setInt(5, objectToUpdate.getBox());
            statement.setInt(6, objectToUpdate.getId());

            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no Location with the id " + objectToUpdate.getId());
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
                throw new NoSuchElementException("[ERROR] There is no Location with the id " + idObjectToDelete);
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public Set<Location> findAll() throws SQLException {
        String query = String.format("SELECT * FROM %s", TABLE);
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<Location> locations = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);

            result = statement.executeQuery();
            while (result.next())
                locations.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return locations;
    }

    @Override
    protected boolean checkAlreadyExists(Location location) throws SQLException {
        String query = String.format(
                "SELECT 1 FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ?",
                TABLE, FIELD_DESIGNATION, FIELD_CITY, FIELD_STREET, FIELD_STREET_NUMBER, FIELD_BOX
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, location.getDesignation());
            statement.setInt(2, location.getCity().getId());
            statement.setString(3, location.getStreet());
            statement.setString(4, location.getStreetNumber());
            statement.setInt(5, location.getBox());

            result = statement.executeQuery();
            return result.next();
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    @Override
    protected Location getResult(ResultSet result) throws SQLException {
        return new Location(
                result.getInt(FIELD_ID),
                result.getString(FIELD_DESIGNATION),
                new DAOCity().find(result.getInt(FIELD_CITY)),
                result.getString(FIELD_STREET),
                result.getString(FIELD_STREET_NUMBER),
                result.getInt(FIELD_BOX)
        );
    }
}