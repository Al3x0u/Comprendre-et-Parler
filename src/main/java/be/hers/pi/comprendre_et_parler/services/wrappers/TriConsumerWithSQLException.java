package be.hers.pi.comprendre_et_parler.services.wrappers;

import java.sql.SQLException;

@FunctionalInterface
public interface TriConsumerWithSQLException<T, U, V> {
    void accept(T param1, U param2, V param3) throws SQLException;
}