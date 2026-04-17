package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.City;
import be.hers.pi.comprendre_et_parler.models.Location;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOLocation implements DAO<Location> {
    public final String TABLE = "location";
    public final String FIELD_ID = "id";
    public final String FIELD_DESIGNATION = "designation";
    public final String FIELD_CITY = "city";
    public final String FIELD_STREET = "street";
    public final String FIELD_STREET_NUMBER = "streetNumber";
    public final String FIELD_BOX = "box";

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
                location = new Location(
                        id,
                        result.getString(FIELD_DESIGNATION),
                        new DAOCity().find(result.getInt(FIELD_CITY)),
                        result.getString(FIELD_STREET),
                        result.getString(FIELD_STREET_NUMBER),
                        result.getInt(FIELD_BOX)
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
        return location;
    }

    /**
     * Insert a Location Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws SQLException if we couldn't connect to the database
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Location objectToInsert) throws AlreadyExistsException, SQLException {
        // Manage invalid Location
        List<Location> locations = findAll();
        for (Location line : locations) {
            if (line.equals(objectToInsert))
                throw new AlreadyExistsException("Location " + objectToInsert.getDesignation() + " already exists at id " + line.getId());
        }

        // Attempt insertion
        String query = "INSERT INTO %s(%s, %s, %s, %s, %s) VALUES(?, ?, ?, ?, ?)";
        query = String.format(query, TABLE, FIELD_DESIGNATION, FIELD_CITY, FIELD_STREET, FIELD_STREET_NUMBER, FIELD_BOX);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToInsert.getDesignation());
            statement.setInt(2, objectToInsert.getCity().getId());
            statement.setString(3, objectToInsert.getStreet());
            statement.setString(4, objectToInsert.getStreetNumber());
            statement.setInt(5, objectToInsert.getBox());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Update a Location line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Location objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        List<Location> locations = findAll();
        for (Location line : locations) {
            if (line.equals(objectToUpdate))
                throw new AlreadyExistsException("Location " + objectToUpdate.getDesignation() + " already exists at id " + line.getId());
        }

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
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Delete a line in the Location table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the Location object in the database
     * @throws SQLException if we couldn't connect to the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Location objectToDelete) throws NoSuchElementException, SQLException {
        String query = "DELETE FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ?";
        query = String.format(query, TABLE, FIELD_ID, FIELD_DESIGNATION, FIELD_CITY, FIELD_STREET, FIELD_STREET_NUMBER, FIELD_BOX);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            statement.setString(2, objectToDelete.getDesignation());
            statement.setInt(3, objectToDelete.getCity().getId());
            statement.setString(4, objectToDelete.getStreet());
            statement.setString(5, objectToDelete.getStreetNumber());
            statement.setInt(6, objectToDelete.getBox());
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("Location " + objectToDelete.getDesignation() + " was not found in database");
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Return all line of Location table in the database in Location Object in a List
     * @return a List who contains Location Objects, if database is empty, an empty list
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Location> findAll() throws SQLException {
        String query = "SELECT * FROM " + TABLE;
        PreparedStatement statement = null;
        ResultSet result = null;
        List<Location> locations = new ArrayList<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                locations.add(new Location(
                        result.getInt(FIELD_ID),
                        result.getString(FIELD_DESIGNATION),
                        new DAOCity().find(result.getInt(FIELD_CITY)),
                        result.getString(FIELD_STREET),
                        result.getString(FIELD_STREET_NUMBER),
                        result.getInt(FIELD_BOX)
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
        return locations;
    }

    /**
     * Get the location of a mission via MissionLocation table
     * @param missionId : id of the mission
     * @return Location object or null
     * @throws SQLException if the database could not be reached
     */
    public Location getMissionLocation(int missionId) throws SQLException {
        String query = "SELECT location FROM MissionLocation WHERE mission = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, missionId);
            result = statement.executeQuery();
            if (result.next())
                return find(result.getInt("location"));
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return null;
    }

    /**
     * Get the room of a mission via MissionLocation table
     * @param missionId : id of the mission
     * @return room as String or null if none was found
     * @throws SQLException if the database could not be reached
     */
    public String getMissionRoom(int missionId) throws SQLException {
        String query = "SELECT room FROM MissionLocation WHERE mission = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, missionId);
            result = statement.executeQuery();
            if (result.next())
                return result.getString("room");
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return null;
    }
}