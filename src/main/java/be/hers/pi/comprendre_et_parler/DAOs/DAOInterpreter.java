package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.domains.Interpreter;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOInterpreter implements DAO<Interpreter>{
    /**
     *
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws ConnectionException if the database could not be reached
     */
    @Override
    public Interpreter find(String id) throws ConnectionException {
        return null;
    }

    /**
     *
     * @param objectToInsert an object of type T to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException       if objectToInsert is already present in database
     * @throws ConnectionException          if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Interpreter objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, ConnectionException {

    }

    /**
     *
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws ConnectionException    if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Interpreter objectToUpdate) throws AlreadyExistsException, NoSuchElementException, ConnectionException {

    }

    /**
     *
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws ConnectionException    if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Interpreter objectToDelete) throws NoSuchElementException, ConnectionException {

    }

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws ConnectionException if the database could not be reached
     */
    @Override
    public List<Interpreter> findAll() throws ConnectionException {
        return List.of();
    }

    /**
     *
     * @param start represent the start of the time that we want the availability
     * @param end   represent the end of the time that we want the availability
     * @param date  represent the date
     * @return  a List of Interpreter who ara available in the given time and date
     */
    public List<Interpreter> findAvailable(LocalTime start, LocalTime end, LocalDate date){
        return null;
    }

    /**
     *
     * @param idAcademicSkills the id of the AcademicSkill
     * @return  a List of Interpreter who have the AcademicSkill having the idAcademicSkills
     * @throws NoSuchElementException if idAcademicSkills doesn't correspond to the id of anu AcademicSkills
     */
    public List<Interpreter> findByAcademicSkills(int idAcademicSkills) throws NoSuchElementException{
        return null;
    }
}
