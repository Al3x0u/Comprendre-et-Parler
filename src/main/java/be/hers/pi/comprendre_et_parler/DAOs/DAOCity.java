package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.City;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

public class DAOCity extends DAO<City> {
    protected static final String TABLE = "city";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_DESIGNATION = "designation";
    protected static final String FIELD_POSTAL_CODE = "postalCode";


    /**
     * Search for a City in the database with the int parameter
     * @param id : identification of the city
     * @return City object who correspond to the given id else null
     * @throws SQLException if the database couldn't be reached
     */
    @Override
    public City find(int id) throws SQLException {
        String query = "SELECT * FROM " + TABLE + " WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        City city = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if (result.next()) {
                city = getResult(result);
            }
        }
        finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return city;
    }

    /**
     * Insert a City Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, the object is updated with auto generated id from the database,
     * and the change was commited
     */
    @Override
    public void create(City objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert))
            throw new AlreadyExistsException("City " + objectToInsert.getDesignation() + " already exists");

        String query = "INSERT INTO %s(%s, %s) VALUES(?, ?)";
        query = String.format(query, TABLE, FIELD_DESIGNATION, FIELD_POSTAL_CODE);
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query, new String[]{FIELD_ID});
            statement.setString(1, objectToInsert.getDesignation());
            statement.setInt(2, objectToInsert.getPostalCode());
            statement.executeUpdate();

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next())
                objectToInsert.setId(generatedKeys.getInt(1));
        }
        finally {
            closeResultSet(generatedKeys);
            closeStatement(statement);
        }
    }

    /**
     * Update a City line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes,
     * and the change was commited
     */
    @Override
    public void update(City objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (checkAlreadyExists(objectToUpdate))
            throw new AlreadyExistsException("City " + objectToUpdate.getDesignation() + " already exists");

        String query = "UPDATE %s SET %s = ?, %s = ? WHERE %s = ?";
        query = String.format(query, TABLE, FIELD_DESIGNATION, FIELD_POSTAL_CODE, FIELD_ID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getDesignation());
            statement.setInt(2, objectToUpdate.getPostalCode());
            statement.setInt(3, objectToUpdate.getId());
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("City " + objectToUpdate.getDesignation() + " of id " + objectToUpdate.getId() + " could not be found in database");
        }
        finally {
            closeStatement(statement);
        }
    }

    /**
     * Delete a line in the City table in the database
     * @param idObjectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the City object in the database
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
                throw new NoSuchElementException("City " + idObjectToDelete + " was not found in database");
        } finally {
            closeStatement(statement);
        }
    }

    /**
     * Return all line of City table in the database in City Object in a Set
     * @return a Set who contains City Objects, or an empty Set if none was found
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Set<City> findAll() throws SQLException {
        String query = "SELECT * FROM " + TABLE;
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<City> cities = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                cities.add(getResult(result));
            }
        }
        finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return cities;
    }

    /**
     * Check if a City already exists in the database
     * @param city the city to check
     * @return true if the city already exists, else false
     * @throws SQLException if the database could not be reached
     */
    @Override
    protected boolean checkAlreadyExists(City city) throws SQLException {
        String query = "SELECT 1 FROM " + TABLE +
                " WHERE " + FIELD_DESIGNATION + " = ? AND " + FIELD_POSTAL_CODE + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, city.getDesignation());
            statement.setInt(2, city.getPostalCode());
            result = statement.executeQuery();
            return result.next();
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    /**
     * Build a City object from a ResultSet
     * @param result the ResultSet to read from
     * @return a City object built from the ResultSet
     * @throws SQLException if the database could not be reached
     */
    @Override
    protected City getResult(ResultSet result) throws SQLException {
        return new City(
                result.getInt(FIELD_ID),
                result.getString(FIELD_DESIGNATION),
                result.getInt(FIELD_POSTAL_CODE)
        );
    }
}