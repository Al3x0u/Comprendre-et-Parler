package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOBeneficiary;
import be.hers.pi.comprendre_et_parler.DAOs.DAOInterpreter;
import be.hers.pi.comprendre_et_parler.DAOs.DAOMission;
import be.hers.pi.comprendre_et_parler.DAOs.DAOStatus;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.Interpreter;
import be.hers.pi.comprendre_et_parler.models.Status;
import be.hers.pi.comprendre_et_parler.services.wrappers.FunctionWithSQLException;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class StatusService {

    private final DAOStatus daoStatus;

    public StatusService() {
        this.daoStatus = new DAOStatus();
    }

    /**
     * Return all existing status
     */
    public List<Status> findAll() throws SQLException, ConnectionException {
        return new ArrayList<>(SQLWrap.call(daoStatus::findAll));
    }

    /**
     * Search for an interpreter in the database.
     * @return one interpreter present in database
     * @param id the id of the interpreter to find
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error occurs
     */
    public Status getOneInterpreter(int id) throws SQLException, ConnectionException {
        return SQLWrap.call(daoStatus::find, id);
    }

    /**
     * Creates a new status in database
     * @param status the status to create
     * @throws AlreadyExistsException if the status already exists in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error
     */
    public void createStatus(Status status) throws AlreadyExistsException, SQLException, ConnectionException {
        SQLWrap.callTransaction(daoStatus::create, status);
    }

    /**
     * Updates a Status in database
     * @param oldStatus the status as it exists in database
     * @param newStatus the new version of the status
     * @throws NoSuchElementException if status does not exist in database
     * @throws AlreadyExistsException if updated status already exists
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error
     */
    public void updateStatus(Status oldStatus, Status newStatus) throws NoSuchElementException, AlreadyExistsException, SQLException, ConnectionException {
        if (oldStatus.equals(newStatus))
            return;

        updateStatus(oldStatus.getId(), newStatus);
    }

    /**
     * Updates a Status in database
     * @param oldStatusId the id of the status to modify
     * @param newStatus the new version of the status. Its id will be updated to match oldStatusId.
     * @throws NoSuchElementException if status does not exist in database
     * @throws AlreadyExistsException if updated status already exists
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error
     */
    public void updateStatus(int oldStatusId, Status newStatus) throws NoSuchElementException, AlreadyExistsException, SQLException, ConnectionException {
        newStatus.setId(oldStatusId);
        SQLWrap.callTransaction(daoStatus::update, newStatus);
    }

    /**
     * Deletes a Status from database
     * @param status the status to delete
     * @throws NoSuchElementException if status does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error
     */
    public void deleteStatus(Status status) throws NoSuchElementException, SQLException, ConnectionException {
        SQLWrap.callTransaction(daoStatus::delete, status.getId());
    }

    /**
     * Deletes a Status from database
     * @param id the id of the status to delete
     * @throws NoSuchElementException if status does not exist in database
     * @throws ConnectionException if the database could not be reached
     * @throws SQLException if any other database error
     */
    public void deleteStatus(int id) throws NoSuchElementException, SQLException, ConnectionException {
        SQLWrap.callTransaction(daoStatus::delete, id);
    }
}