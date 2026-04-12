package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Location;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOLocation implements DAO<Location> {

    /**
     * Search for a location in the database with the int parameter
     * @param id : identification of the location
     * @return Location object who correspond to the given id else null
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Location find(int id) throws SQLException {
        return null;
    }

    /**
     * Insert a Location Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws SQLException if we couldn't connect to the database
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Location objectToInsert)
            throws AlreadyExistsException, SQLException {
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
    public void update(Location objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
    }

    /**
     * Delete a line in the Location table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the Location object in the database
     * @throws SQLException if we couldn't connect to the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Location objectToDelete)
            throws NoSuchElementException, SQLException {
    }

    /**
     * Return all line of Location table in the database in Location Object in a List
     * @return a List who contains Location Object, if database is empty, an empty list
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Location> findAll() throws SQLException {
        return List.of();
    }
}