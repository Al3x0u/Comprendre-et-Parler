package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.domains.Beneficiary;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOBeneficiary implements DAO<Beneficiary> {

    /**
     *
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Beneficiary find(String id) throws SQLException {
        return null;
    }

    /**
     *
     * @param objectToInsert an object of type T to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException       if objectToInsert is already present in database
     * @throws SQLException          if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Beneficiary objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {

    }

    /**
     *
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
     *
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
     *
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Beneficiary> findAll() throws SQLException {
        return List.of();
    }

    /**
     *
     * @param idInterpreter represent the id of the interpreter which we want the beneficiary
     * @return a List of Beneficiary which are referenced by the interpreter who have the idInterpreter
     * @throws NoSuchElementException if the idInterpreter doesn't correspond to a existent interpreter
     */
    public List<Beneficiary> getReferenced(String idInterpreter) throws NoSuchElementException {
        return null;
    }

    /**
     *
     * @param idStatus represent the id of the status
     * @return a List of Beneficiary who have the id having the given idStatus
     * @throws NoSuchElementException if the idStatus doesn't correspond to a existent Status
     */
    public List<Beneficiary> getByStatus(int idStatus) throws NoSuchElementException {
        return null;
    }
}