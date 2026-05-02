package be.hers.pi.comprendre_et_parler.services.wrappers;

import java.sql.SQLException;

@FunctionalInterface
public interface ConsumerWithSQLException<T> {
    void apply(T arg) throws SQLException;
}
