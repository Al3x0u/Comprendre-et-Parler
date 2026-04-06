package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Beneficiary;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOBeneficiary implements DAO<Beneficiary> {

    /**
     * Search for a Beneficiary in the database with the int parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Beneficiary find(int id) throws SQLException {
        return null;
    }

    /**
     * Search for a Beneficiary in the database with the String parameter
     * @param login the login of the object to find in database
     * @return the object identified by login in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Beneficiary find(String login) throws SQLException {
        return null;
    }

    /**
     * Insert a Beneficiary object in the database
     * @param objectToInsert an object of type Beneficiary to add to the database
     * @throws AlreadyExistsException       if objectToInsert is already present in database
     * @throws SQLException          if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Beneficiary objectToInsert)
            throws AlreadyExistsException, SQLException {

    }

    /**
     * Update a Beneficiary line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Beneficiary objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {

    }

    /**
     * Delete a Beneficiary line in the table in the database
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Beneficiary objectToDelete)
            throws NoSuchElementException, SQLException {

    }

    /**
     * Return all line of Beneficiary table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Beneficiary> findAll() throws SQLException {
        return List.of();
    }

    /**
     * Return all Beneficiary referenced by the interpreter with the given id
     * @param idInterpreter represent the id of the interpreter which we want the beneficiary
     * @return a List of Beneficiary which are referenced by the interpreter who have the idInterpreter, or null if no beneficiaries
     * @throws NoSuchElementException if the idInterpreter doesn't correspond to a existent interpreter
     */
    public List<Beneficiary> getReferenced(String idInterpreter) throws NoSuchElementException {
        return null;
    }

    /**
     * Return all Beneficiary having the given status
     * @param idStatus represent the id of the status
     * @return a List of Beneficiary who have the id having the given idStatus,or null if no or null if no beneficiaries having this Status
     * @throws NoSuchElementException if the idStatus doesn't correspond to a existent Status
     */
    public List<Beneficiary> getByStatus(int idStatus) throws NoSuchElementException {
        return null;
    }
}