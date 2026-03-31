package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Mission;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOMission implements DAO<Mission> {
    public final String table = "mission";
    public final String fieldID = "id";
    public final String fieldSubject = "subject";
    public final String fieldState = "stateOfMission";
    public final String fieldCommentary = "commentary";
    public final String fieldTimeSlot = "timeSlot";
    public final String fieldBeneficiaries = "beneficiaries";
    public final String fieldInterpreters = "interpreters";
    public final String fieldLocation = "location";
    public final String fieldJobSkill = "jobSkill";
    public final String fieldAcademicSkill = "AcademicSkill";

    /**
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Mission find(String id) throws SQLException {
        return null;
    }

    /**
     * @param objectToInsert an object of type T to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Mission objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
    }

    /**
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Mission objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
    }

    /**
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Mission objectToDelete)
            throws NoSuchElementException, SQLException {
    }

    /**
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Mission> findAll() throws SQLException {
        return List.of();
    }

    /**
     * @param idUser represent the id of the user which we want the schedule
     * @return a list of Mission which compose the schedule of the idUser
     * @throws NoSuchElementException if the given idUser doesn't correspond to an existent id
     */
    public List<Mission> getSchedule(String idUser) throws NoSuchElementException {
        return null;
    }
}