package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.City;
import be.hers.pi.comprendre_et_parler.models.Location;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOLocation implements DAO<Location> {
    public final String table = "location";
    public final String fieldID = "id";
    public final String fieldDesignation = "designation";
    public final String fieldCity = "city";
    public final String fieldStreet = "street";
    public final String fieldStreetNumber = "streetNumber";
    public final String fieldBox = "box";

    /**
     * Search for a location in the database with the String parameter
     * @param id : identification of the location
     * @return Location object who correspond to the given id else null
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Location find(String id) throws SQLException {
        String query = "SELECT * FROM " + table + " WHERE " + fieldID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        Location location = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, Integer.parseInt(id));
            result = statement.executeQuery();
            if (result.next()) {
                location = new Location(
                        result.getInt(fieldID),
                        result.getString(fieldDesignation),
                        new DAOCity().find(String.valueOf(result.getInt(fieldCity))),
                        result.getString(fieldStreet),
                        result.getString(fieldStreetNumber),
                        result.getInt(fieldBox)
                );
            }
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return location;
    }

    /**
     * Insert a Location Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws DuplicatePrimaryKeyException if the given id already used in the database
     * @throws SQLException if we couldn't connect to the database
     */
    @Override
    public void create(Location objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
        // Manage invalid Location
        Location alreadyPresent = find(String.valueOf(objectToInsert.getId()));
        if (alreadyPresent != null) {
            if (alreadyPresent.equals(objectToInsert))
                throw new AlreadyExistsException("Object already exists in database");
            else
                throw new DuplicatePrimaryKeyException("Object is already present in database under a different primary key");
        }

        // Attempt insertion
        String query = "INSERT INTO %s(%s, %s, %s, %s, %s) VALUES(?, ?, ?, ?, ?)";
        query = String.format(query, table, fieldDesignation, fieldCity, fieldStreet, fieldStreetNumber, fieldBox);
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
                statement.close();
            }
        }
    }

    /**
     * Update a Location line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     */
    @Override
    public void update(Location objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        // Manage invalid Location
        List<Location> allLines = findAll();
        if (allLines.contains(objectToUpdate))
            return;
        allLines.forEach((Location line) -> {
            if (line.getDesignation().equals(objectToUpdate.getDesignation()) && line.getCity().equals(objectToUpdate.getCity())
                    && line.getStreet().equals(objectToUpdate.getStreet()) && line.getStreetNumber().equals(objectToUpdate.getStreetNumber())
                    && line.getBox() == objectToUpdate.getBox() && line.getId() != objectToUpdate.getId())
                throw new AlreadyExistsException("Object " + objectToUpdate.getDesignation() + " already exists at id " + line.getId());
        });
        if (allLines.stream().noneMatch((Location line) -> line.getId() == objectToUpdate.getId())) {
            throw new NoSuchElementException("Object " + objectToUpdate.getDesignation() + " of id " + objectToUpdate.getId() + " could not be found in database");
        }
        // Attempt update
        String query = "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?";
        query = String.format(query, table, fieldDesignation, fieldCity, fieldStreet, fieldStreetNumber, fieldBox, fieldID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getDesignation());
            statement.setInt(2, objectToUpdate.getCity().getId());
            statement.setString(3, objectToUpdate.getStreet());
            statement.setString(4, objectToUpdate.getStreetNumber());
            statement.setInt(5, objectToUpdate.getBox());
            statement.setInt(6, objectToUpdate.getId());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Delete a line in the Location table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the Location object in the database
     * @throws SQLException if we couldn't connect to the database
     */
    @Override
    public void delete(Location objectToDelete) throws NoSuchElementException, SQLException {
        if (find(String.valueOf(objectToDelete.getId())) == null)
            throw new NoSuchElementException("Object " + objectToDelete.getDesignation() + " was not found in database");

        String query = "DELETE FROM %s WHERE %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ? AND %s = ?";
        query = String.format(query, table, fieldID, fieldDesignation, fieldCity, fieldStreet, fieldStreetNumber, fieldBox);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            statement.setString(2, objectToDelete.getDesignation());
            statement.setInt(3, objectToDelete.getCity().getId());
            statement.setString(4, objectToDelete.getStreet());
            statement.setString(5, objectToDelete.getStreetNumber());
            statement.setInt(6, objectToDelete.getBox());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Return all line of Location table in the database in Location Object in a List
     * @return a List who contains Location Object
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Location> findAll() throws SQLException {
        String query = "SELECT * FROM " + table;
        PreparedStatement statement = null;
        ResultSet result = null;
        List<Location> locations = new ArrayList<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                locations.add(new Location(
                        result.getInt(fieldID),
                        result.getString(fieldDesignation),
                        new DAOCity().find(String.valueOf(result.getInt(fieldCity))),
                        result.getString(fieldStreet),
                        result.getString(fieldStreetNumber),
                        result.getInt(fieldBox)
                ));
            }
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
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
                return new DAOLocation().find(String.valueOf(result.getInt("location")));
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return null;
    }
}