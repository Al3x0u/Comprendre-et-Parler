package be.hers.pi.comprendre_et_parler.DAOs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.NoSuchElementException;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.util.Set;

public abstract class DAO<T> {

    /**
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    abstract T find(int id) throws SQLException;

    /**
     * @param objectToInsert an object of type T to add to the database
     * @post objectToInsert has been added to the database, and the change was commited
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the insertion failed for any other reason
     */
    abstract void create(T objectToInsert) throws AlreadyExistsException, SQLException;

    /**
     * @param objectToUpdate the object to edit in the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws AlreadyExistsException if an object with a different id but otherwise identical fields already exists in database
     * @throws SQLException if the update failed for any other reason
     */
    abstract void update(T objectToUpdate) throws NoSuchElementException, AlreadyExistsException, SQLException;

    /**
     *
     * @param objectToDelete the object to delete in the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the deletion failed for any other reason
     */
    abstract void delete(T objectToDelete) throws NoSuchElementException, SQLException;

    /**
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    abstract Set<T> findAll() throws SQLException;

    /**
     * Build an object from a ResultSet
     * @param result the ResultSet to read from
     * @return an object built from the ResultSet
     * @throws SQLException if the database could not be reached
     */
    protected abstract T getResult(ResultSet result) throws SQLException;

    /**
     * Close a Statement
     * @param statement the Statement to close (can be null)
     */
    protected void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Close a ResultSet
     * @param resultSet the ResultSet to close (can be null)
     */
    protected void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}