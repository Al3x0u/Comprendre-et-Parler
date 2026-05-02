package be.hers.pi.comprendre_et_parler.services.wrappers;

import be.hers.pi.comprendre_et_parler.DAOs.DatabaseConnector;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Savepoint;

public class SQLWrap {
    /**
     * Wraps a method in an SQL transaction and single out connection exceptions
     * @param function the method to call. Must match a FunctionWithSQLException with 1 parameter
     * @param param the parameter to pass to the method
     * @return the method's return value
     * @param <T> the method's input parameter type
     * @param <R> the method's return type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, R> R callTransaction(FunctionWithSQLException<T, R> function, T param) throws SQLException, ConnectionException {
        return call((p) -> performTransaction(function, p), param);
    }

    /**
     * Wraps a method and single out connection exceptions
     * @param function the method to call. Must match a FunctionWithSQLException with 1 parameter
     * @param param the parameter to pass to the method
     * @return the method's return value
     * @param <T> the method's input parameter type
     * @param <R> the method's return type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, R> R call(FunctionWithSQLException<T, R> function, T param) throws SQLException, ConnectionException {
        try {
            return function.apply(param);
        }
        catch (SQLException e) {
            if (e.getSQLState().matches("^08")) {
                throw new ConnectionException("Could not connect to database");
            }
            throw e;
        }
    }

    /**
     * Wraps a method in an SQL transaction
     * @param function the method to call. Must match a FunctionWithSQLException with 1 parameter
     * @param param the parameter to pass to the method
     * @return the method's return value
     * @param <T> the method's input parameter type
     * @param <R> the method's return type
     * @throws SQLException if any other error occurred
     */
    private static <T, R> R performTransaction(FunctionWithSQLException<T, R> function, T param) throws SQLException {
        DatabaseConnector.getInstance().setAutoCommit(false);
        Savepoint sp = DatabaseConnector.getInstance().setSavepoint();
        try {
            R ret = function.apply(param);
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
}
