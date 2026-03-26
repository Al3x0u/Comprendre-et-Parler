package be.hers.pi.comprendre_et_parler.DAOs;

import java.util.List;
import java.util.NoSuchElementException;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

public interface DAO<T> {

    /**
     *
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws ConnectionException if the database could not be reached
     */
    T find(String id) throws ConnectionException;

    /**
     *
     * @param objectToInsert an object of type T to add to the database
     * @post objectToInsert has been added to the database, and the change was commited
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws ConnectionException if the database could not be reached
     */
    void create(T objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, ConnectionException;

    /**
     *
     * @param objectToUpdate the object to edit in the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws ConnectionException if the database could not be reached
     */
    void update(T objectToUpdate) throws AlreadyExistsException,NoSuchElementException, ConnectionException;

    /**
     *
     * @param objectToDelete the object to delete in the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws ConnectionException if the database could not be reached
     */
    void delete(T objectToDelete) throws NoSuchElementException, ConnectionException;

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws ConnectionException if the database could not be reached
     */
    List<T> findAll() throws ConnectionException;

}

