package be.hers.pi.comprendre_et_parler.DAOs;

import java.sql.*;
import java.util.NoSuchElementException;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;

import javax.xml.crypto.Data;
import java.util.Set;


public abstract class DAO<T> {

    /**
     * Wraps a DAO method in an SQL transaction and single out connection exceptions
     * @param daoMethod the method to call
     * @param param the parameter to pass to the method
     * @param useTransaction specifies whether to run the method within an SQL transaction (default: True)
     * @return the method's return value
     * @param <T> the method's input parameter type
     * @param <R> the method's return type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, R> R call(FunctionWithSQLException<T, R> daoMethod, T param, boolean useTransaction) throws SQLException, ConnectionException {
        try {
            if (useTransaction) {
                DatabaseConnector.getInstance().setAutoCommit(false);
                Savepoint sp = DatabaseConnector.getInstance().setSavepoint();
                try {
                    R ret = daoMethod.apply(param);
                    DatabaseConnector.getInstance().commit();
                    return ret;
                }
                catch (Exception e) {
                    DatabaseConnector.getInstance().rollback(sp);
                    throw e;
                }
                finally {
                    DatabaseConnector.getInstance().setAutoCommit(true);
                    try {
                        DatabaseConnector.getInstance().releaseSavepoint(sp);
                    }
                    catch (SQLFeatureNotSupportedException e) {
                        System.err.println("Warning: " + e.getMessage());
                    }
                }
            }
            else {
                return daoMethod.apply(param);
            }
        }
        catch (SQLException e) {
            if (e.getSQLState().matches("^08")) {
                throw new ConnectionException("Could not connect to database");
            }
            throw e;
        }
    }

    public static <T, R> R call(FunctionWithSQLException<T, R> func, T param) throws SQLException {
        return call(func, param, true);
    }

    public static <T, U, R> R call(BiFunctionWithSQLException<T, U, R> func, T param1, U param2, boolean noSavePoint) {
        throw new UnsupportedOperationException();
    }
    public static <T, U, R> R call(BiFunctionWithSQLException<T, U, R> func, T param1, U param2) {
        return call(func, param1, param2, false);
    }

    public static <T> void call(ConsumerWithSQLException<T> cons, T param, boolean noSavePoint) {
        throw new UnsupportedOperationException();
    }
    public static <T, U> void call(ConsumerWithSQLException<T> cons, T param1, U param2) {
        call(cons, param1, false);
    }

    public static <T, U> void call(BiConsumerWithSQLException<T, U> cons, T param1, U param2, boolean noSavePoint) {
        throw new UnsupportedOperationException();
    }
    public static <P> void call(ConsumerWithSQLException<P> cons, P param) {
        call(cons, param, false);
    }

    /**
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public abstract T find(int id) throws SQLException;

    /**
     * @param objectToInsert an object of type T to add to the database
     * @post objectToInsert has been added to the database, and the change was commited
     * @throws AlreadyExistsException if an object with a different id but otherwise identical fields already exists in database
     * @throws SQLException if the insertion failed for any other reason
     * @post objectToInsert has been added to the database, the object is updated with auto generated id from the database,
     * and the change was commited
     */
    public abstract void create(T objectToInsert) throws AlreadyExistsException, SQLException;

    /**
     * @param objectToUpdate the object to edit in the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws AlreadyExistsException if an object with a different id but otherwise identical fields already exists in database
     * @throws SQLException if the update failed for any other reason
     */
    public abstract void update(T objectToUpdate) throws NoSuchElementException, AlreadyExistsException, SQLException;

    /**
     * @param objectToDelete the object to delete in the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the deletion failed for any other reason
     */
    public abstract void delete(T objectToDelete) throws NoSuchElementException, SQLException;

    /**
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    public abstract Set<T> findAll() throws SQLException;

    /**
     * Check if an object already exists in the database
     * @param object the object to check
     * @return true if the object already exists, else false
     * @throws SQLException if the database could not be reached
     */
    protected abstract boolean checkAlreadyExists(T object) throws SQLException;

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