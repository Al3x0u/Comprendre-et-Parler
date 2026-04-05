package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.City;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOCity implements DAO<City> {

    /**
     * Search for a City in the database with the int parameter
     * @param id : identification of the city
     * @return City object who correspond to the given id else null
     * @throws SQLException if the database couldn't be reached
     */
    @Override
    public City find(int id) throws SQLException {
        return null;
    }

    /**
     * Insert a City Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws SQLException if the database could not be reached
     */
    @Override
    public void create(City objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {

    }

    /**
     * Update a City line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     */
    @Override
    public void update(City objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {

    }

    /**
     * Delete a line in the City table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the Location object in the database
     * @throws SQLException if we couldn't connect to the database
     */
    @Override
    public void delete(City objectToDelete)
            throws NoSuchElementException, SQLException {

    }

    /**
     * Return all line of City table in the database in City Object in a List
     * @return a List who contains City Object, if database is empty, an empty list
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<City> findAll() throws SQLException {
        return List.of();
    }
}