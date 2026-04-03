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

    /**
     * Search for a Status in the database with the String parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Status find(String id) throws SQLException {
        String query = "SELECT * FROM " + table + " WHERE " + field_id + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        Status ret = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, Integer.parseInt(id));
            result = statement.executeQuery();
            if (result.next()) {
                ret = new Status(
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
        return ret;
    }

    /**
     * Insert a Status object in the database
     * @param objectToInsert an object of type Status to add to the database
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
            statement = DatabaseConnector.getInstance().prepareStatement(query);
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

    /**
     * Update a Status line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws AlreadyExistsException if another object with the same attributes already exists in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Status objectToUpdate) throws NoSuchElementException, AlreadyExistsException, SQLException {
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
            statement = DatabaseConnector.getInstance().prepareStatement(query);
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
     * Delete a Status line in the table in the database
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Status objectToDelete) throws NoSuchElementException, SQLException {
            if (find(String.valueOf(objectToDelete.getId())) == null)
                throw new NoSuchElementException("Object " + objectToDelete.getDesignation() + " was not found in database");

            String query = "DELETE FROM %s WHERE %s = ? AND %s = ? AND %s = ?";
            query = String.format(query, table, field_id, field_designation, field_hourQuota);
            PreparedStatement statement = null;
            try {
                statement = DatabaseConnector.getInstance().prepareStatement(query);
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
     * Return all line of Status table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Status> findAll() throws SQLException {
        String query = "SELECT * FROM " + table;
        PreparedStatement statement = null;
        ResultSet result = null;
        List<Status> ret = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
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
}