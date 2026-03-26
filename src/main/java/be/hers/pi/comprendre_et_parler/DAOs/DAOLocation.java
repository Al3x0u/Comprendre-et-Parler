package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.*;



import java.util.List;
import java.util.NoSuchElementException;

public class DAOLocation implements DAO<Location> {

    /*
    Search for a location in the database with the String parameter
    @param id : identification of the location
    @return Location object who correspond to the given id else null
     */
    @Override
    public Location find(String id) {
        return null;
    }

    /*
    Insert a Location Object in the database
    @param objectToInsert : Object that we gonna insert
    @return TRUE if the insertion is completed else FALSE
    @throws AlreadyExistException if there are already a line with there information
     */
    @Override
    public void create(Location objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, ConnectionException {
    }

    /*
    Update a Location line who already exist in the database
    @param objectToUpdate : object with the news information
    @return TRUE if the modification wa a success else FALSE
    @throws AlreadyExistException if there are already a line with there information
     */
    @Override
    public void update(Location objectToUpdate) throws AlreadyExistsException, NoSuchElementException,ConnectionException {
    }

    /*
    Delete a line in the Location table in the database
    @param objectToDelete : object with the information of the line who need to be deleted
    @return TRUE if the removal was a success else FALSE
     */
    @Override
    public void delete(Location objectToDelete) {

    }

    /*
    Return all line of Location table in the database in Location Object in a List
    @return a List who contains Location Object
     */
    @Override
    public List<Location> findAll() {

        return List.of();
    }


}
