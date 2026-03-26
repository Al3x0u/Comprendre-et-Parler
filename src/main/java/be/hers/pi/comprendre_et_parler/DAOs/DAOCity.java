package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.*;

import java.util.List;

public class DAOCity implements DAO<City> {

    /*
    Search for a city in the database with the String parameter
    @param id : identification of the city
    @return City object who correspond to the given id else null
    @throws ConnectionException if the database couldnot be reached
     */
    @Override
    public City find(String id) throws ConnectionException {
        return null;
    }

    /*
    Insert a City Object in the database
    @param objectToInsert : Object that we gonna insert
    @return TRUE if the insertion is completed else FALSE
    @throws AlreadyExistException if there are already a line with there information
    @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
    @throws ConnectionException if the database could not be reached
     */
    @Override
    public void create(City objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, ConnectionException {

    }

    /*
    Update a City line who already exist in the database
    @param objectToUpdate : object with the news information
    @return TRUE if the modification was a success else FALSE
    @throws AlreadyExistException if there are already a line with there information
     */
    @Override
    public void update(City objectToUpdate) throws AlreadyExistsException {
    }

    /*
    Delete a line in the City table in the database
    @param objectToDelete : object with the information of the line who need to be deleted
    @return TRUE if the removal was a success else FALSE
     */
    @Override
    public void delete(City objectToDelete) {

    }

    /*
    Return all line of City table in the database in City Object in a List
    @return a List who contains City Object
     */
    @Override
    public List<City> findAll() {
        return List.of();
    }
}