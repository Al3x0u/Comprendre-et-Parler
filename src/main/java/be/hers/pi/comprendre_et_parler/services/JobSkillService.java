package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOJobSkill;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.JobSkill;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;

@Service
public class JobSkillService {
    private final static DAOJobSkill daoJobSkill = new DAOJobSkill();

    /**
     * Retrieve all JobSkill from the database.
     * @return every JobSkill present in database, sorted by their compareTo()
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public List<JobSkill> getAllJobSkills() throws ConnectionException, SQLException {
        List<JobSkill> allJobSkills = new ArrayList<>(SQLWrap.call(daoJobSkill::findAll));
        allJobSkills.sort(JobSkill::compareTo);
        return allJobSkills;
    }

    /**
     * Creates a new JobSkill in database
     * @param newSkill the skill to create
     * @throws AlreadyExistsException if the skill already exists in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void createJobSkill(JobSkill newSkill) throws AlreadyExistsException, ConnectionException, SQLException {
        SQLWrap.callTransaction(daoJobSkill::create, newSkill);
    }

    /**
     * Updates an JobSkill in database
     * @param oldSkillId the id of the skill to modify
     * @param newSkill the new version of the skill oldSkill must be updated to. It's id will be updated to match oldSkillId.
     * @throws NoSuchElementException if oldSkill does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void updateJobSkill(int oldSkillId, JobSkill newSkill)  throws NoSuchElementException, ConnectionException, SQLException {
        newSkill.setId(oldSkillId);
        SQLWrap.callTransaction(daoJobSkill::update, newSkill);
    }

    /**
     * Deletes a JobSkill from database
     * @param id the id of the skill to delete
     * @throws NoSuchElementException if skill does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void deleteJobSkill(int id) throws NoSuchElementException, ConnectionException, SQLException {
        SQLWrap.callTransaction(daoJobSkill::delete, id);
    }
}