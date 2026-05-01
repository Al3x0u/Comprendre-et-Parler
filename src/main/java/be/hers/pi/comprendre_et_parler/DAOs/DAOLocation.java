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

    /**
     * Search for a location in the database with the int parameter
     * @param id : identification of the location
     * @return Location object who correspond to the given id else null
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Location find(int id) throws SQLException {
        String query = "SELECT * FROM " + TABLE + " WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        Location location = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if (result.next()) {
                location = getResult(result);
            }
        }
        finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return location;
    }

    /**
     * Insert a Location Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws SQLException if we couldn't connect to the database
     * @post objectToInsert has been added to the database, the object is updated with auto generated id from the database,
     * and the change was commited
     */
    @Override
    public void create(Location objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert))
            throw new AlreadyExistsException("Location " + objectToInsert.getDesignation() + " already exists");

        String query = "INSERT INTO %s(%s, %s, %s, %s, %s) VALUES(?, ?, ?, ?, ?)";
        query = String.format(query, TABLE, FIELD_DESIGNATION, FIELD_CITY, FIELD_STREET, FIELD_STREET_NUMBER, FIELD_BOX);
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

    /**
     * Update a Location line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes,
     * and the change was commited
     */
    @Override
    public void update(Location objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (checkAlreadyExists(objectToUpdate))
            throw new AlreadyExistsException("Location " + objectToUpdate.getDesignation() + " already exists");

        String query = "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?";
        query = String.format(query, TABLE, FIELD_DESIGNATION, FIELD_CITY, FIELD_STREET, FIELD_STREET_NUMBER, FIELD_BOX, FIELD_ID);
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
                throw new NoSuchElementException("Location " + objectToUpdate.getDesignation() + " of id " + objectToUpdate.getId() + " could not be found in database");
        } finally {
            closeStatement(statement);
        }
    }

    /**
     * Delete a line in the Location table in the database
     * @param idObjectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the Location object in the database
     * @throws SQLException if we couldn't connect to the database
     * @post the object ID matching objectToDelete has been deleted from the database,
     * and the change was commited
     */
    @Override
    public void delete(int idObjectToDelete) throws NoSuchElementException, SQLException {
        String query = "DELETE FROM %s WHERE %s = ?";
        query = String.format(query, TABLE, FIELD_ID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idObjectToDelete);
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("Location " + idObjectToDelete + " was not found in database");
        } finally {
            closeStatement(statement);
        }
    }

    /**
     * Return all line of Location table in the database in Location Object in a Set
     * @return a Set who contains Location Objects, if database is empty, an empty Set
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Set<Location> findAll() throws SQLException {
        String query = "SELECT * FROM " + TABLE;
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<Location> locations = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                locations.add(getResult(result));
            }
        }
        finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return locations;
    }

    /**
     * Check if a Location already exists in the database
     * @param location the location to check
     * @return true if the location already exists, else false
     * @throws SQLException if the database could not be reached
     */
    @Override
    protected boolean checkAlreadyExists(Location location) throws SQLException {
        String query = "SELECT 1 FROM " + TABLE +
                " WHERE " + FIELD_DESIGNATION + " = ? AND " + FIELD_CITY + " = ? AND " +
                FIELD_STREET + " = ? AND " + FIELD_STREET_NUMBER + " = ? AND " + FIELD_BOX + " = ?";
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

    /**
     * Build a Location object from a ResultSet
     * @param result the ResultSet to read from
     * @return a Location object built from the ResultSet
     * @throws SQLException if the database could not be reached
     */
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