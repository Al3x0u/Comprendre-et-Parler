package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOAcademicSkill;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.AcademicSkill;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service
public class AcademicSkillService {
    private final static DAOAcademicSkill daoAcademicSkill = new DAOAcademicSkill();

    /**
     * Retrieve all AcademicSkill from the database.
     * @return every AcademicSkill present in database, sorted by their compareTo()
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public List<AcademicSkill> getAllAcademicSkills() throws ConnectionException, SQLException {
        List<AcademicSkill> allAcademicSkills = new ArrayList<>(SQLWrap.call(daoAcademicSkill::findAll));
        allAcademicSkills.sort(AcademicSkill::compareTo);
        return allAcademicSkills;
    }

    /**
     * Creates a new AcademicSkill in database
     * @param newSkill the skill to create
     * @throws AlreadyExistsException if the skill already exists in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void createAcademicSkill(AcademicSkill newSkill) throws AlreadyExistsException, ConnectionException, SQLException {
        SQLWrap.callTransaction(daoAcademicSkill::create, newSkill);
    }

    /**
     * Updates an AcademicSkill in database
     * @param oldSkillId the id of the skill to modify
     * @param newSkill the new version of the skill oldSkill must be updated to. It's id will be updated to match oldSkillId.
     * @throws NoSuchElementException if oldSkill does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void updateAcademicSkill(int oldSkillId, AcademicSkill newSkill)  throws NoSuchElementException, ConnectionException, SQLException {
        newSkill.setId(oldSkillId);
        SQLWrap.callTransaction(daoAcademicSkill::update, newSkill);
    }

    /**
     * Deletes an AcademicSkill from database
     * @param id the id of the skill to delete
     * @throws NoSuchElementException if skill does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void deleteAcademicSkill(int id) throws NoSuchElementException, ConnectionException, SQLException {
        SQLWrap.callTransaction(daoAcademicSkill::delete, id);
    }
}