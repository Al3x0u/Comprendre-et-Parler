package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.domains.AcademicSkill;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOAcademicSkill implements DAO<AcademicSkill> {

    /**
     Search for a AcademicSkill in the database with the String parameter
     @param id : identification of the AcademicSkill
     @return AcademicSkill object who correspond to the given id else null
     */
    @Override
    public AcademicSkill find(String id) throws SQLException {
        return null;
    }

    /**
     Insert a BusinessSkillCompetence Object in the database
     @param objectToInsert : Object that we gonna insert
     @throws AlreadyExistsException if there are already a line with there information
     @throws DuplicatePrimaryKeyException if the given id already used in the database
     @throws SQLException if we couldn't connect to the database
     */
    @Override
    public void create(AcademicSkill objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {

    }

    /**
     Update a AcademicSkill line who already exist in the database
     @param objectToUpdate : object with the news information
     @throws AlreadyExistsException if there are already a line with there information
     @throws NoSuchElementException if there are not the element to update in the database
     @throws SQLException if there are an error during the connection to the database
     */
    @Override
    public void update(AcademicSkill objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {

    }

    /**
     Delete a AcademicSkill line in the table in the database
     @param objectToDelete : object with the information of the line who need to be deleted
     @throws NoSuchElementException if we couldn't find the Location object in the database
     @throws SQLException if we couldn't connect to the database
     */
    @Override
    public void delete(AcademicSkill objectToDelete)
            throws NoSuchElementException, SQLException {

    }

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<AcademicSkill> findAll() throws SQLException {
        return List.of();
    }
}