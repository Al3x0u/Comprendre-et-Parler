package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.AppliUser;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOAppliUser implements DAO<AppliUser> {

    /**
     * Search for a AppliUser in the database with the int parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public AppliUser find(int id) throws SQLException {
        return null;
    }

    /**
     * Search for a AppliUser in the database with the String parameter
     * @param login the login of the object to find in database
     * @return the object identified by login in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public AppliUser find(String login) throws SQLException {
        return null;
    }

    /**
     * Insert a AppliUser object in the database
     * @param objectToInsert an object of type AppliUser to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException       if objectToInsert is already present in database
     * @throws SQLException          if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(AppliUser objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {

    }

    /**
     * Update a AppliUser line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(AppliUser objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {

    }

    /**
     * Delete a AppliUser line in the table in the database
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(AppliUser objectToDelete)
            throws NoSuchElementException, SQLException {

    }

    /**
     * Return all line of AppliUser table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<AppliUser> findAll() throws SQLException {
        return List.of();
    }
}