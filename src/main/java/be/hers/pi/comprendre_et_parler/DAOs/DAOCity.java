package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.City;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOCity implements DAO<City> {
    public final String table = "city";
    public final String fieldID = "id";
    public final String fieldDesignation = "designation";
    public final String fieldPostalCode = "postalCode";


    /**
     * Search for a City in the database with the String parameter
     * @param id : identification of the city
     * @return City object who correspond to the given id else null
     * @throws SQLException if the database couldn't be reached
     */
    @Override
    public City find(String id) throws SQLException {
        String query = "SELECT * FROM " + table + " WHERE " + fieldID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        City city = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, Integer.parseInt(id));
            result = statement.executeQuery();
            if (result.next()) {
                city = new City(
                        result.getInt(fieldID),
                        result.getString(fieldDesignation),
                        result.getInt(fieldPostalCode)
                );
            }
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return city;
    }

    /**
     * Insert a City Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws SQLException if the database could not be reached
     */
    @Override
    public void create(City objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
        // Manage invalid city
        City alreadyPresent = find(String.valueOf(objectToInsert.getId()));
        if (alreadyPresent != null) {
            if (alreadyPresent.equals(objectToInsert))
                throw new AlreadyExistsException("Object already exists in database");
            else
                throw new DuplicatePrimaryKeyException("Object is already present in database under a different primary key");
        }

        // Attempt insertion
        String query = "INSERT INTO %s(%s, %s) VALUES(?, ?)";
        query = String.format(query, table, fieldDesignation, fieldPostalCode);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToInsert.getDesignation());
            statement.setInt(2, objectToInsert.getPostalCode());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Update a City line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     */
    @Override
    public void update(City objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        // Manage invalid city
        List<City> allLines = findAll();
        if (allLines.contains(objectToUpdate))
            return;
        allLines.forEach((City line) -> {
            if (line.getDesignation().equals(objectToUpdate.getDesignation()) && line.getPostalCode() == objectToUpdate.getPostalCode() && line.getId() != objectToUpdate.getId())
                throw new AlreadyExistsException("Object " + objectToUpdate.getDesignation() + " already exists at id " + line.getId());
        });
        if (allLines.stream().noneMatch((City line) -> line.getId() == objectToUpdate.getId())) {
            throw new NoSuchElementException("Object " + objectToUpdate.getDesignation() + " of id " + objectToUpdate.getId() + " could not be found in database");
        }
        // Attempt update
        String query = "UPDATE %s SET %s = ?, %s = ? WHERE %s = ?";
        query = String.format(query, table, fieldDesignation, fieldPostalCode, fieldID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getDesignation());
            statement.setInt(2, objectToUpdate.getPostalCode());
            statement.setInt(3, objectToUpdate.getId());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Delete a line in the City table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the Location object in the database
     * @throws SQLException if we couldn't connect to the database
     */
    @Override
    public void delete(City objectToDelete) throws NoSuchElementException, SQLException {
        if (find(String.valueOf(objectToDelete.getId())) == null)
            throw new NoSuchElementException("Object " + objectToDelete.getDesignation() + " was not found in database");

        String query = "DELETE FROM %s WHERE %s = ? AND %s = ? AND %s = ?";
        query = String.format(query, table, fieldID, fieldDesignation, fieldPostalCode);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            statement.setString(2, objectToDelete.getDesignation());
            statement.setInt(3, objectToDelete.getPostalCode());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Return all line of City table in the database in City Object in a List
     * @return a List who contains City Object
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<City> findAll() throws SQLException {
        String query = "SELECT * FROM " + table;
        PreparedStatement statement = null;
        ResultSet result = null;
        List<City> cities = new ArrayList<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                cities.add(new City(
                        result.getInt(fieldID),
                        result.getString(fieldDesignation),
                        result.getInt(fieldPostalCode)
                ));
            }
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return cities;
    }
}