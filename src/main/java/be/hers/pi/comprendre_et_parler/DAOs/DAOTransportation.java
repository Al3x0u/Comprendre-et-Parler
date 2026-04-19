package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.Transportation;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOTransportation implements DAO<Transportation> {
    static final String TABLE = "transportation";
    static final String FIELD_ID = "id";
    static final String FIELD_DESIGNATION = "designation";

    /**
     * Search for a Transportation in the database with the int parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Transportation find(int id) throws SQLException {
        return null;
    }

    /**
     * Insert a Transportation Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws SQLException if we couldn't connect to the database
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Transportation objectToInsert) throws AlreadyExistsException, SQLException {

    }

    /**
     * Update a Transportation line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Transportation objectToUpdate) throws NoSuchElementException, AlreadyExistsException, SQLException {

    }

    /**
     * Delete a Transportation line in the table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the Transportation object in the database
     * @throws SQLException if we couldn't connect to the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Transportation objectToDelete) throws NoSuchElementException, SQLException {

    }

    /**
     * Return all line of Transportation table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Transportation> findAll() throws SQLException {
        return List.of();
    }
}
