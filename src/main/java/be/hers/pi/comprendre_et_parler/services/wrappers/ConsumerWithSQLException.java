package be.hers.pi.comprendre_et_parler.services.wrappers;

import java.sql.SQLException;

@FunctionalInterface
public interface ConsumerWithSQLException<T> {
    void accept(T arg) throws SQLException;
}
