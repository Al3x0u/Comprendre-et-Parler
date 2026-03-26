package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.domains.AcademicSkillCompetence;
import be.hers.pi.comprendre_et_parler.domains.BusinessSkillCompetence;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.util.List;
import java.util.NoSuchElementException;

public class DAOBusinessSkillCompetence implements DAO<BusinessSkillCompetence>{

    /**
       Search for a BusinessSkillCompetence in the database with the String parameter
       @param id : identification of the BusinessSkillCompetence
       @return BusinessSkillCompetence object who correspond to the given id else null
    */
    @Override
    public BusinessSkillCompetence find(String id) throws ConnectionException {
        return null;
    }

    /**
        Insert a BusinessSkillCompetence Object in the database
        @param objectToInsert : Object that we gonna insert
        @throws AlreadyExistsException if there are already a line with there information
        @throws DuplicatePrimaryKeyException if the given id already used in the database
        @throws ConnectionException if we couldn't connect to the database
     */
    @Override
    public void create(BusinessSkillCompetence objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, ConnectionException {

    }

    /**
        Update a BusinessSkillCompetence line who already exist in the database
        @param objectToUpdate : object with the news information
        @throws AlreadyExistsException if there are already a line with there information
        @throws NoSuchElementException if there are not the element to update in the database
        @throws ConnectionException if there are an error during the connection to the database
     */
    @Override
    public void update(BusinessSkillCompetence objectToUpdate) throws AlreadyExistsException, NoSuchElementException, ConnectionException {

    }

    /**
        Delete a BusinessSkillCompetence line in the  table in the database
        @param objectToDelete : object with the information of the line who need to be deleted
        @throws NoSuchElementException if we couldn't find the Location object in the database
        @throws ConnectionException if we couldn't connect to the database
     */
    @Override
    public void delete(BusinessSkillCompetence objectToDelete) throws NoSuchElementException, ConnectionException {

    }

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws ConnectionException if the database could not be reached
     */
    @Override
    public List<BusinessSkillCompetence> findAll() throws ConnectionException {
        return List.of();
    }
}
