package be.hers.pi.comprendre_et_parler.services.wrappers;

import be.hers.pi.comprendre_et_parler.DAOs.DatabaseConnector;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Savepoint;

public class SQLWrap {
    static boolean unimplementedReleaseSavepointWarning = true;
    /**
     * Wraps a method in an SQL transaction and single out connection exceptions
     * @param supplier the method to call. Must match a SupplierWithSQLException
     * @return the method's return value
     * @param <R> the method's return type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <R> R callTransaction(SupplierWithSQLException<R> supplier) throws SQLException, ConnectionException {
        return call(() -> performTransaction(supplier));
    }

    /**
     * Wraps a method in an SQL transaction and single out connection exceptions
     * @param function the method to call. Must match a FunctionWithSQLException
     * @param param the parameter to pass to the method
     * @return the method's return value
     * @param <T> the method's input parameter type
     * @param <R> the method's return type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, R> R callTransaction(FunctionWithSQLException<T, R> function, T param) throws SQLException, ConnectionException {
        return call((T p) -> performTransaction(function, p), param);
    }

    /**
     * Wraps a method in an SQL transaction and single out connection exceptions
     * @param function the method to call. Must match a BiFunctionWithSQLException
     * @param param1 the first parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @return the method's return value
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @param <R> the method's return type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, U, R> R callTransaction(BiFunctionWithSQLException<T, U, R> function, T param1, U param2) throws SQLException, ConnectionException {
        return call((T p1, U p2) -> performTransaction(function, p1, p2), param1, param2);
    }

    /**
     * Wraps a method in an SQL transaction and single out connection exceptions
     * @param function the method to call. Must match a TriFunctionWithSQLException
     * @param param1 the first parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @param param3 the third parameter to pass to the method
     * @return the method's return value
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @param <V> the method's third input parameter type
     * @param <R> the method's return type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, U, V, R> R callTransaction(TriFunctionWithSQLException<T, U, V, R> function, T param1, U param2, V param3) throws SQLException, ConnectionException {
        return call((T p1, U p2, V p3) -> performTransaction(function, p1, p2, p3), param1, param2, param3);
    }

    /**
     * Wraps a method in an SQL transaction and single out connection exceptions
     * @param consumer the method to call. Must match a ConsumerWithSQLException
     * @param param the parameter to pass to the method
     * @param <T> the method's input parameter type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T> void callTransaction(ConsumerWithSQLException<T> consumer, T param) throws SQLException, ConnectionException {
        call((ConsumerWithSQLException<T>) (p -> performTransaction(consumer, p)), param);
    }

    /**
     * Wraps a method in an SQL transaction and single out connection exceptions
     * @param consumer the method to call. Must match a BiConsumerWithSQLException
     * @param param1 the first parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, U> void callTransaction(BiConsumerWithSQLException<T, U> consumer, T param1, U param2) throws SQLException, ConnectionException {
        call((BiConsumerWithSQLException<T, U>)(p1, p2) -> performTransaction(consumer, p1, p2), param1, param2);
    }

    /**
     * Wraps a method in an SQL transaction and single out connection exceptions
     * @param consumer the method to call. Must match a TriConsumerWithSQLException
     * @param param1 the first parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, U, V> void callTransaction(TriConsumerWithSQLException<T, U, V> consumer, T param1, U param2, V param3) throws SQLException, ConnectionException {
        call((TriConsumerWithSQLException<T, U, V>)(p1, p2, p3) -> performTransaction(consumer, p1, p2, p3), param1, param2, param3);
    }

    /**
     * Wraps a method and single out connection exceptions
     * @param supplier the method to call. Must match a FunctionWithSQLException
     * @return the method's return value
     * @param <R> the method's return type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <R> R call(SupplierWithSQLException<R> supplier) throws SQLException, ConnectionException {
        try {
            return supplier.get();
        }
        catch (SQLException e) {
            if (e.getSQLState().matches("^08")) {
                throw new ConnectionException("Could not connect to database");
            }
            throw e;
        }
    }

    /**
     * Wraps a method and single out connection exceptions
     * @param function the method to call. Must match a FunctionWithSQLException
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
     * Wraps a method and single out connection exceptions
     * @param function the method to call. Must match a BiFunctionWithSQLException
     * @param param1 the first parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @return the method's return value
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @param <R> the method's return type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, U, R> R call(BiFunctionWithSQLException<T, U, R> function, T param1, U param2) throws SQLException, ConnectionException {
        try {
            return function.apply(param1, param2);
        }
        catch (SQLException e) {
            if (e.getSQLState().matches("^08")) {
                throw new ConnectionException("Could not connect to database");
            }
            throw e;
        }
    }

    /**
     * Wraps a method and single out connection exceptions
     * @param function the method to call. Must match a TriFunctionWithSQLException
     * @param param1 the first parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @param param3 the third parameter to pass to the method
     * @return the method's return value
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @param <V> the method's third input parameter type
     * @param <R> the method's return type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, U, V, R> R call(TriFunctionWithSQLException<T, U, V, R> function, T param1, U param2, V param3) throws SQLException, ConnectionException {
        try {
            return function.apply(param1, param2, param3);
        }
        catch (SQLException e) {
            if (e.getSQLState().matches("^08")) {
                throw new ConnectionException("Could not connect to database");
            }
            throw e;
        }
    }

    /**
     * Wraps a method and single out connection exceptions
     * @param consumer the method to call. Must match a ConsumerWithSQLException
     * @param param the parameter to pass to the method
     * @param <T> the method's input parameter type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T> void call(ConsumerWithSQLException<T> consumer, T param) throws SQLException, ConnectionException {
        try {
            consumer.accept(param);
        }
        catch (SQLException e) {
            if (e.getSQLState().matches("^08")) {
                throw new ConnectionException("Could not connect to database");
            }
            throw e;
        }
    }

    /**
     * Wraps a method and single out connection exceptions
     * @param consumer the method to call. Must match a BiConsumerWithSQLException
     * @param param1 the first parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, U> void call(BiConsumerWithSQLException<T, U> consumer, T param1, U param2) throws SQLException, ConnectionException {
        try {
            consumer.accept(param1, param2);
        }
        catch (SQLException e) {
            if (e.getSQLState().matches("^08")) {
                throw new ConnectionException("Could not connect to database");
            }
            throw e;
        }
    }

    /**
     * Wraps a method and single out connection exceptions
     * @param consumer the method to call. Must match a TriConsumerWithSQLException
     * @param param1 the first parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @param param3 the third parameter to pass to the method
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @param <V> the method's third input parameter type
     * @throws ConnectionException if a connection error occurred (SQL state 08xxx)
     * @throws SQLException if any other error occurred
     */
    public static <T, U, V> void call(TriConsumerWithSQLException<T, U, V> consumer, T param1, U param2, V param3) throws SQLException, ConnectionException {
        try {
            consumer.accept(param1, param2, param3);
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
     * @param supplier the method to call. Must match a FunctionWithSQLException
     * @return the method's return value
     * @param <R> the method's return type
     * @throws SQLException if any other error occurred
     */
    private static <R> R performTransaction(SupplierWithSQLException<R> supplier) throws SQLException {
        DatabaseConnector.getInstance().setAutoCommit(false);
        Savepoint sp = DatabaseConnector.getInstance().setSavepoint();
        try {
            R ret = supplier.get();
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
                if (unimplementedReleaseSavepointWarning) {
                    System.err.println("Warning: " + e.getMessage());
                    unimplementedReleaseSavepointWarning = false;
                }
            }
        }
    }

    /**
     * Wraps a method in an SQL transaction
     * @param function the method to call. Must match a FunctionWithSQLException
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
                if (unimplementedReleaseSavepointWarning) {
                    System.err.println("Warning: " + e.getMessage());
                    unimplementedReleaseSavepointWarning = false;
                }
            }
        }
    }

    /**
     * Wraps a method in an SQL transaction
     * @param function the method to call. Must match a BiFunctionWithSQLException
     * @param param1 the first parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @return the method's return value
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @param <R> the method's return type
     * @throws SQLException if any other error occurred
     */
    private static <T, U, R> R performTransaction(BiFunctionWithSQLException<T, U, R> function, T param1, U param2) throws SQLException {
        DatabaseConnector.getInstance().setAutoCommit(false);
        Savepoint sp = DatabaseConnector.getInstance().setSavepoint();
        try {
            R ret = function.apply(param1, param2);
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
                if (unimplementedReleaseSavepointWarning) {
                    System.err.println("Warning: " + e.getMessage());
                    unimplementedReleaseSavepointWarning = false;
                }            }
        }
    }

    /**
     * Wraps a method in an SQL transaction
     * @param function the method to call. Must match a TriFunctionWithSQLException
     * @param param1 the first parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @param param3 the third parameter to pass to the method
     * @return the method's return value
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @param <V> the method's third input parameter type
     * @param <R> the method's return type
     * @throws SQLException if any other error occurred
     */
    private static <T, U, V, R> R performTransaction(TriFunctionWithSQLException<T, U, V, R> function, T param1, U param2, V param3) throws SQLException {
        DatabaseConnector.getInstance().setAutoCommit(false);
        Savepoint sp = DatabaseConnector.getInstance().setSavepoint();
        try {
            R ret = function.apply(param1, param2, param3);
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
                if (unimplementedReleaseSavepointWarning) {
                    System.err.println("Warning: " + e.getMessage());
                    unimplementedReleaseSavepointWarning = false;
                }            }
        }
    }

    /**
     * Wraps a method in an SQL transaction
     * @param consumer the method to call. Must match a ConsumerWithSQLException
     * @param param the parameter to pass to the method
     * @param <T> the method's input parameter type
     * @throws SQLException if any other error occurred
     */
    private static <T> void performTransaction(ConsumerWithSQLException<T> consumer, T param) throws SQLException {
        DatabaseConnector.getInstance().setAutoCommit(false);
        Savepoint sp = DatabaseConnector.getInstance().setSavepoint();
        try {
            consumer.accept(param);
            DatabaseConnector.getInstance().commit();
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
                if (unimplementedReleaseSavepointWarning) {
                    System.err.println("Warning: " + e.getMessage());
                    unimplementedReleaseSavepointWarning = false;
                }            }
        }
    }

    /**
     * Wraps a method in an SQL transaction
     * @param consumer the method to call. Must match a BiConsumerWithSQLException
     * @param param1 the parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @throws SQLException if any other error occurred
     */
    private static <T, U> void performTransaction(BiConsumerWithSQLException<T, U> consumer, T param1, U param2) throws SQLException {
        DatabaseConnector.getInstance().setAutoCommit(false);
        Savepoint sp = DatabaseConnector.getInstance().setSavepoint();
        try {
            consumer.accept(param1, param2);
            DatabaseConnector.getInstance().commit();
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
                if (unimplementedReleaseSavepointWarning) {
                    System.err.println("Warning: " + e.getMessage());
                    unimplementedReleaseSavepointWarning = false;
                }            }
        }
    }

    /**
     * Wraps a method in an SQL transaction
     * @param consumer the method to call. Must match a TriConsumerWithSQLException
     * @param param1 the parameter to pass to the method
     * @param param2 the second parameter to pass to the method
     * @param <T> the method's first input parameter type
     * @param <U> the method's second input parameter type
     * @throws SQLException if any other error occurred
     */
    private static <T, U, V> void performTransaction(TriConsumerWithSQLException<T, U, V> consumer, T param1, U param2, V param3) throws SQLException {
        DatabaseConnector.getInstance().setAutoCommit(false);
        Savepoint sp = DatabaseConnector.getInstance().setSavepoint();
        try {
            consumer.accept(param1, param2, param3);
            DatabaseConnector.getInstance().commit();
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
                if (unimplementedReleaseSavepointWarning) {
                    System.err.println("Warning: " + e.getMessage());
                    unimplementedReleaseSavepointWarning = false;
                }            }
        }
    }
}
