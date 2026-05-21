package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOJobSkill;
import be.hers.pi.comprendre_et_parler.DAOs.DAOInterpreter;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.Interpreter;
import be.hers.pi.comprendre_et_parler.models.JobSkill;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class JobSkillService {
    /**
     * @return every JobSkill present in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public Set<JobSkill> findAll() throws ConnectionException, SQLException {
        return SQLWrap.call(new DAOJobSkill()::findAll);
    }

    /**
     * Creates a new JobSkill in database
     * @param newSkill the skill to create
     * @throws AlreadyExistsException if the skill already exists in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void createJobSkill(JobSkill newSkill) throws AlreadyExistsException, ConnectionException, SQLException {
        SQLWrap.callTransaction(new DAOJobSkill()::create, newSkill);
    }

    /**
     * Updates an JobSkill in database
     * @param oldSkill the skill as it exists in database
     * @param newSkill the new version of the skill oldSkill must be updated to. It's id will be updated to match with oldSkill's.
     * @throws NoSuchElementException if oldSkill does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void updateJobSkill(JobSkill oldSkill, JobSkill newSkill) throws NoSuchElementException, ConnectionException, SQLException {
        if (Objects.equals(oldSkill, newSkill))
            return;

        updateJobSkill(oldSkill.getId(), newSkill);
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
        SQLWrap.callTransaction(new DAOJobSkill()::update, newSkill);
    }

    /**
     * Deletes an JobSkill from database
     * @param skill the skill to delete
     * @throws NoSuchElementException if skill does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void deleteJobSkill(JobSkill skill) throws NoSuchElementException, ConnectionException, SQLException {
        SQLWrap.callTransaction(new DAOJobSkill()::delete, skill.getId());
    }

    /**
     * Registers an JobSkill to an Interpreter in database, and creates the JobSkill if it wasn't already present
     * @param skill the skill to register
     * @param interpreter the interpreter to register the skill to. The object will be updated with the new skill to match the change in database
     * @throws NoSuchElementException if interpreter does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void addJobSkillToInterpreter(JobSkill skill, Interpreter interpreter) throws NoSuchElementException, ConnectionException, SQLException {
        SQLWrap.callTransaction(new DAOInterpreter()::createJobSkillLink, interpreter, skill);
        interpreter.addJobSkill(skill);
    }

    /**
     * Removes an JobSkill from an interpreter in database.
     * @param skill the skill to remove
     * @param interpreter the interpreter to remove the skill from. The object will be updated with the skill removed to match the change in databse
     * @throws NoSuchElementException if either skill or interpreter does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void removeJobSkillFromInterpreter(JobSkill skill, Interpreter interpreter) throws NoSuchElementException, ConnectionException, SQLException {
        SQLWrap.callTransaction(new DAOInterpreter()::deleteJobSkillLink, interpreter, skill);
        interpreter.removeJobSkill(skill);
    }
}
