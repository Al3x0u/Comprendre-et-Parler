package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Manager;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOManager implements DAO<Manager> {

    /**
     * Search for a Manager in the database with the int parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Manager find(int id) throws SQLException {
        return null;
    }

    /**
     * Search for a Manager in the database with the String parameter
     * @param login the login of the object to find in database
     * @return the object identified by login in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Manager find(String login) throws SQLException {
        return null;
    }

    /**
     * Insert a Manager object in the database
     * @param objectToInsert an object of type Manager to add to the database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Manager objectToInsert)
            throws AlreadyExistsException, SQLException {
    }

    /**
     * Update a Manager line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Manager objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
    }

    /**
     * Delete a Manager line in the table in the database
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Manager objectToDelete)
            throws NoSuchElementException, SQLException {
    }

    /**
     * Return all line of Manager table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Manager> findAll() throws SQLException {
        return List.of();
    }

    /**
     * Return all Manager who are available in the given time and date
     * @param start represent the start of the time that we want the availability
     * @param end represent the end of the time that we want the availability
     * @param date represent the date
     * @return a List of Manager who are available in the given time and date, or an empty List if no Manager is available
     */
    public List<Manager> findAvailable(LocalTime start, LocalTime end, LocalDate date) {
        return null;
    }

    /**
     * Return all Manager who have the AcademicSkill having the given id
     * @param idAcademicSkills the id of the AcademicSkill
     * @return a List of Manager who have the AcademicSkill having the idAcademicSkills, or an empty List if no Manager have this AcademicSkill
     * @throws NoSuchElementException if idAcademicSkills doesn't correspond to the id of any AcademicSkill
     */
    public List<Manager> findByAcademicSkills(int idAcademicSkills)
            throws NoSuchElementException {
        return null;
    }

    /**
     * Return all Manager who have the JobSkill having the given id
     * @param idJobSkills the id of the JobSkill
     * @return a List of Manager who have the JobSkill having the idJobSkills, or an empty List if no Manager have this JobSkill
     * @throws NoSuchElementException if idJobSkills doesn't correspond to the id of any JobSkill
     */
    public List<Manager> findByJobSkills(int idJobSkills)
            throws NoSuchElementException {
        return null;
    }
}