package be.hers.pi.comprendre_et_parler.services.wrappers;

import java.sql.SQLException;

@FunctionalInterface
public interface TriFunctionWithSQLException<T, U, V, R> {
    R apply(T param1, U param2, V param3) throws SQLException;
}
