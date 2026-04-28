package be.hers.pi.comprendre_et_parler.DAOs;

import java.sql.SQLException;

@FunctionalInterface
public interface FunctionWithSQLException<T, R> {
    R apply(T arg) throws SQLException;
}
