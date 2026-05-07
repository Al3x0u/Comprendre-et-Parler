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

    @Override
    public City find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        City city = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);

            result = statement.executeQuery();
            if (result.next())
                city = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return city;
    }

    @Override
    public void create(City objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert) >= 0)
            throw new AlreadyExistsException("City " + objectToInsert.getDesignation() + " already exists");

        String query = String.format("INSERT INTO %s VALUES(NULL, ?, ?)", TABLE);
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
        } finally {
            closeResultSet(generatedKeys);
            closeStatement(statement);
        }
    }

    @Override
    public void update(City objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (find(objectToUpdate.getId()) == null)
            throw new NoSuchElementException("[ERROR] There is no City with the id " + objectToUpdate.getId());

        int idInDB = checkAlreadyExists(objectToUpdate);
        if (idInDB != objectToUpdate.getId() && idInDB >= 0)
            throw new AlreadyExistsException("City " + objectToUpdate.getDesignation() + " already exists");

        String query = String.format(
                "UPDATE %s SET %s = ?, %s = ? WHERE %s = ?",
                TABLE, FIELD_DESIGNATION, FIELD_POSTAL_CODE, FIELD_ID
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getDesignation());
            statement.setInt(2, objectToUpdate.getPostalCode());
            statement.setInt(3, objectToUpdate.getId());
            statement.executeUpdate();
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
                throw new NoSuchElementException("[ERROR] There is no City with the id " + idObjectToDelete);
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public Set<City> findAll() throws SQLException {
        String query = String.format("SELECT * FROM %s", TABLE);
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<City> cities = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);

            result = statement.executeQuery();
            while (result.next())
                cities.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return cities;
    }

    @Override
    protected int checkAlreadyExists(City objectToCheck) throws SQLException {
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ? AND %s = ?",
                FIELD_ID, TABLE, FIELD_DESIGNATION, FIELD_POSTAL_CODE
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToCheck.getDesignation());
            statement.setInt(2, objectToCheck.getPostalCode());

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
    protected City getResult(ResultSet result) throws SQLException {
        return new City(
                result.getInt(FIELD_ID),
                result.getString(FIELD_DESIGNATION),
                result.getInt(FIELD_POSTAL_CODE)
        );
    }
}