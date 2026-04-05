package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.JobSkill;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOJobSkill implements DAO<JobSkill> {
    public final String table = "jobskill";
    public final String field_id = "id";
    public final String field_designation = "designation";

    /**
     * Search for a JobSkill in the database with the String parameter
     * @param id identification of the JobSkill
     * @return JobSkill object which correspond to the given id else null
     * @throws SQLException if the database could not be reached
     */
    @Override
    public JobSkill find(String id) throws SQLException {
        return null;
    }

    /**
     * Insert a JobSkill Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws DuplicatePrimaryKeyException if the given id already used in the database
     * @throws SQLException if we couldn't connect to the database
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(JobSkill objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {

    }

    /**
     * Update a JobSkill line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(JobSkill objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {

    }

    /**
     * Delete a JobSkill line in the table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the JobSkill object in the database
     * @throws SQLException if we couldn't connect to the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(JobSkill objectToDelete) throws NoSuchElementException, SQLException {

    }

    /**
     * Return all line of JobSkill table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<JobSkill> findAll() throws SQLException {
        return null;
    }
}