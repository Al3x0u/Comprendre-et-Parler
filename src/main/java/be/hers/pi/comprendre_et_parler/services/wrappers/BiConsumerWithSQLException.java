package be.hers.pi.comprendre_et_parler.services.wrappers;

import java.sql.SQLException;

@FunctionalInterface
public interface BiConsumerWithSQLException<T, U> {
    void accept(T param1, U param2) throws SQLException;
}