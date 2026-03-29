package be.hers.pi.comprendre_et_parler.DAOs;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;
import be.hers.pi.comprendre_et_parler.model.Status;

import java.util.List;
import java.util.NoSuchElementException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOStatus implements DAO<Status> {
    public final static String table = "Status";
    public final static String field_id = "id";
    public final static String field_designation = "designation";
    public final static  String field_hourQuota = "hourQuota";

    /**
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public Status find(String id) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        String query = "SELECT * FROM " + table + " WHERE " + field_id + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        Status status = null;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, Integer.parseInt(id));
            result = statement.executeQuery();
            if (result.next()) {
                status = new Status(
                        result.getInt(field_id),
                        result.getString(field_designation),
                        result.getInt(field_hourQuota)
                );
            }
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return status;
    }

    /**
     * @param objectToInsert an object of type T to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Status objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
        // Manage invalid states
        Status alreadyPresent = find(String.valueOf(objectToInsert.getId()));
        if (alreadyPresent != null) {
            if (alreadyPresent.equals(objectToInsert))
                throw new AlreadyExistsException("Object already exists in database");
            else
                throw new DuplicatePrimaryKeyException("Object is already present in database under a different primary key");
        }

        // Attempt insertion
        String query = "INSERT INTO %s(%s, %s) VALUES(?, ?)";
        query = String.format(query, table, field_designation, field_hourQuota);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getConnection().prepareStatement(query);
            statement.setString(1, objectToInsert.getDesignation());
            statement.setInt(2, objectToInsert.getHourQuota());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    @Override
    public void update(Status objectToUpdate) throws NoSuchElementException, AlreadyExistsException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        // Manage invalid states
        List<Status> allLines = findAll();
        if (allLines.contains(objectToUpdate))
            return;
        allLines.forEach((Status line) -> {
            if (line.getDesignation().equals(objectToUpdate.getDesignation()) && line.getHourQuota() == objectToUpdate.getHourQuota() && line.getId() != objectToUpdate.getId())
                throw new AlreadyExistsException("Object " + objectToUpdate.getDesignation() + "already exists at id " + line.getId());
        });
        if (allLines.stream().anyMatch((Status line) -> line.getId() == objectToUpdate.getId()) == false)
            throw new NoSuchElementException("Object " + objectToUpdate.getDesignation() + "of id " + objectToUpdate.getId() + "could not be found in database");

        // Attempt update
        String query = "UPDATE %s SET %s = ?, %s = ? WHERE %s = ?";
        query = String.format(query, table, field_designation, field_hourQuota, field_id);
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, objectToUpdate.getDesignation());
            statement.setInt(2, objectToUpdate.getHourQuota());
            statement.setInt(3, objectToUpdate.getId());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Status objectToDelete) throws NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getConnection();

        if (find(String.valueOf(objectToDelete.getId())) == null)
            throw new NoSuchElementException("Object " + objectToDelete.getDesignation() + " was not found in database");

        String query = "DELETE FROM %s WHERE %s = ? AND %s = ? AND %s = ?";
        query = String.format(query, table, field_id, field_designation, field_hourQuota);
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            statement.setString(2, objectToDelete.getDesignation());
            statement.setInt(3, objectToDelete.getHourQuota());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Status> findAll() throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        String query = "SELECT * FROM " + table;
        PreparedStatement statement = null;
        ResultSet result = null;
        List<Status> ret = null;
        try {
            statement = connection.prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                ret.add(new Status(
                        result.getInt(field_id),
                        result.getString(field_designation),
                        result.getInt(field_hourQuota)
                ));
            }
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return ret;
    }

    /**
     * @param id the id of the status in database
     * @return the Status identified by idStatus, or null if none was found
     * @throws SQLException if the database could not be reached
     */
    public Status findById(int id) throws SQLException, NoSuchElementException {
        Connection connection = DatabaseConnector.getConnection();
        Status status;
        String query = "SELECT %s FROM %s WHERE %s = ?";
        query = String.format(query, table, field_id, field_designation, field_hourQuota);

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()){
                status = new Status(
                        rs.getInt("id"),
                        rs.getString("designation"),
                        rs.getInt("hourQuota")
                );
            }else{
                throw new NoSuchElementException();
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return status;
    }

}
