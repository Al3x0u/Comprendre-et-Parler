package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOAcademicSkill;
import be.hers.pi.comprendre_et_parler.DAOs.DAOInterpreter;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.AcademicSkill;
import be.hers.pi.comprendre_et_parler.models.Interpreter;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class AcademicSkillService {
    /**
     * @return every AcademicSkill present in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public Set<AcademicSkill> findAll() throws ConnectionException, SQLException {
        return SQLWrap.call(new DAOAcademicSkill()::findAll);
    }

    /**
     * Creates a new AcademicSkill in database
     * @param newSkill the skill to create
     * @throws AlreadyExistsException if the skill already exists in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void createAcademicSkill(AcademicSkill newSkill) throws AlreadyExistsException, ConnectionException, SQLException {
        SQLWrap.callTransaction(new DAOAcademicSkill()::create, newSkill);
    }

    /**
     * Updates an AcademicSkill in database
     * @param oldSkill the skill as it exists in database
     * @param newSkill the new version of the skill oldSkill must be updated to. It's id will be updated to match with oldSkill's.
     * @throws NoSuchElementException if oldSkill does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void updateAcademicSkill(AcademicSkill oldSkill, AcademicSkill newSkill) throws NoSuchElementException, ConnectionException, SQLException {
        if (Objects.equals(oldSkill, newSkill))
            return;

        updateAcademicSkill(oldSkill.getId(), newSkill);
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
        SQLWrap.callTransaction(new DAOAcademicSkill()::update, newSkill);
    }

    /**
     * Deletes an AcademicSkill from database
     * @param skill the skill to delete
     * @throws NoSuchElementException if skill does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void deleteAcademicSkill(AcademicSkill skill) throws NoSuchElementException, ConnectionException, SQLException {
        SQLWrap.callTransaction(new DAOAcademicSkill()::delete, skill.getId());
    }

    /**
     * Deletes an AcademicSkill from database
     * @param id the id of the skill to delete
     * @throws NoSuchElementException if skill does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void deleteAcademicSkill(int id) throws NoSuchElementException, ConnectionException, SQLException {
        SQLWrap.callTransaction(new DAOAcademicSkill()::delete, id);
    }

    /**
     * Registers an AcademicSkill to an Interpreter in database, and creates the AcademicSkill if it wasn't already present
     * @param skill the skill to register
     * @param interpreter the interpreter to register the skill to. The object will be updated with the new skill to match the change in database
     * @throws NoSuchElementException if interpreter does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void addAcademicSkillToInterpreter(AcademicSkill skill, Interpreter interpreter) throws NoSuchElementException, ConnectionException, SQLException {
        SQLWrap.callTransaction(new DAOInterpreter()::createAcademicSkillLink, interpreter, skill);
        interpreter.addAcademicSkill(skill);
    }

    /**
     * Removes an AcademicSkill from an interpreter in database.
     * @param skill the skill to remove
     * @param interpreter the interpreter to remove the skill from. The object will be updated with the skill removed to match the change in databse
     * @throws NoSuchElementException if either skill or interpreter does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public void removeAcademicSkillFromInterpreter(AcademicSkill skill, Interpreter interpreter) throws NoSuchElementException, ConnectionException, SQLException {
        SQLWrap.callTransaction(new DAOInterpreter()::deleteAcademicSkillLink, interpreter, skill);
        interpreter.removeAcademicSkill(skill);
    }
}
