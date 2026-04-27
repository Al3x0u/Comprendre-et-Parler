package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.ExceptionalUnavailability;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.SQLException;
import java.util.Set;
import java.util.NoSuchElementException;

public class DAOExceptionalUnavailability implements DAO<ExceptionalUnavailability> {

    /**
     * Search for a ExceptionalUnavailability in the database with the int parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public ExceptionalUnavailability find(int id) throws SQLException {
        return null;
    }

    /**
     * Insert a ExceptionalUnavailability object in the database
     * @param objectToInsert an object of type ExceptionalUnavailability to add to the database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(ExceptionalUnavailability objectToInsert)
            throws AlreadyExistsException, SQLException {
    }

    /**
     * Update a ExceptionalUnavailability line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(ExceptionalUnavailability objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
    }

    /**
     * Delete a ExceptionalUnavailability line in the table in the database
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(ExceptionalUnavailability objectToDelete)
            throws NoSuchElementException, SQLException {
    }

    /**
     * Return all line of ExceptionalUnavailability table in the database in a Set
     * @return every object of the corresponding type present in database (possibly an empty Set)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Set<ExceptionalUnavailability> findAll() throws SQLException {
        return Set.of();
    }

    /**
     * Return all ExceptionalUnavailability of an Interpreter with the given id
     * @param idInterpreter the id of an Interpreter
     * @return a Set of ExceptionalUnavailability instances representing the interpreter’s exceptional unavailability, or an empty Set if none exist
     * @throws NoSuchElementException if there are not an Interpreter with the given id
     */
    public Set<ExceptionalUnavailability> findForInterpreter(String idInterpreter)
            throws NoSuchElementException {
        return null;
    }
}