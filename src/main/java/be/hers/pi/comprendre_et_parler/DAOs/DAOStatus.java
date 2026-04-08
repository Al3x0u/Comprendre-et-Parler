package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;
import be.hers.pi.comprendre_et_parler.models.Status;

import java.util.List;
import java.util.NoSuchElementException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DAOStatus implements DAO<Status> {
    public final String table = "status";
    public final String field_id = "id";
    public final String field_designation = "designation";
    public final String field_hourQuota = "hourquota";

    @Override
    public Status find(int id) throws SQLException {
        return null;
    }

    @Override
    public void create(Status objectToInsert) throws AlreadyExistsException, SQLException {
    }

    @Override
    public void update(Status objectToUpdate) throws NoSuchElementException, AlreadyExistsException, SQLException {
    }

    @Override
    public void delete(Status objectToDelete) throws NoSuchElementException, SQLException {
    }

    @Override
    public List<Status> findAll() throws SQLException {
        return null;
    }
}