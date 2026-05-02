package be.hers.pi.comprendre_et_parler.services.wrappers;

import java.sql.SQLException;

@FunctionalInterface
public interface SupplierWithSQLException<R> {
    R get() throws SQLException;
}