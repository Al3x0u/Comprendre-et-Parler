package be.hers.pi.comprendre_et_parler.DAOs;

import java.sql.SQLException;

@FunctionalInterface
public interface BiConsumerWithSQLException<T, U> {
    void apply(T param1, U param2) throws SQLException;
}