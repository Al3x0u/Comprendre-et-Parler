package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.AcademicSkill;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DAOAcademicSkill implements DAO<AcademicSkill> {
    public final String table = "academicskill";
    public final String field_id = "id";
    public final String field_designation = "designation";

    /**
     * Search for a AcademicSkill in the database with the String parameter
     * @param id : identification of the AcademicSkill
     * @return AcademicSkill object who correspond to the given id else null
     * @throws SQLException if the database could not be reached
     */
    @Override
    public AcademicSkill find(String id) throws SQLException {
        String query = "SELECT * FROM " + table + " WHERE " + field_id + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        AcademicSkill ret = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, id);
            result = statement.executeQuery();
            if (result.next()) {
                ret = new AcademicSkill(
                        result.getString(field_id),
                        result.getString(field_designation)
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
     * Insert a AcademicSkill object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws DuplicatePrimaryKeyException if the given id already used in the database
     * @throws SQLException if we couldn't connect to the database
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(AcademicSkill objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
        AcademicSkill alreadyPresent = find(objectToInsert.getId());
        if (alreadyPresent != null) {
            if (alreadyPresent.getDesignation().equals(objectToInsert.getDesignation()))
                throw new AlreadyExistsException("Object already exists in database");
            else
                throw new DuplicatePrimaryKeyException("Object is already present in database under a different primary key");
        }

        String query = "INSERT INTO %s(%s, %s) VALUES(?, ?)";
        query = String.format(query, table, field_id, field_designation);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToInsert.getId());
            statement.setString(2, objectToInsert.getDesignation());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Update a AcademicSkill line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(AcademicSkill objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
        List<AcademicSkill> allLines = findAll();

        allLines.forEach((AcademicSkill line) -> {
            if (line.getDesignation().equals(objectToUpdate.getDesignation()) && !line.getId().equals(objectToUpdate.getId()))
                throw new AlreadyExistsException("Object " + objectToUpdate.getDesignation() + " already exists at id " + line.getId());
        });

        if (allLines.stream().noneMatch((AcademicSkill line) -> line.getId().equals(objectToUpdate.getId())))
            throw new NoSuchElementException("Object " + objectToUpdate.getDesignation() + " of id " + objectToUpdate.getId() + " could not be found in database");

        String query = "UPDATE %s SET %s = ? WHERE %s = ?";
        query = String.format(query, table, field_designation, field_id);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getDesignation());
            statement.setString(2, objectToUpdate.getId());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Delete a AcademicSkill line in the table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the AcademicSkill object in the database
     * @throws SQLException if we couldn't connect to the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(AcademicSkill objectToDelete)
            throws NoSuchElementException, SQLException {
        if (find(objectToDelete.getId()) == null)
            throw new NoSuchElementException("Object " + objectToDelete.getDesignation() + " was not found in database");

        String query = "DELETE FROM %s WHERE %s = ? AND %s = ?";
        query = String.format(query, table, field_id, field_designation);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToDelete.getId());
            statement.setString(2, objectToDelete.getDesignation());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Return all line of AcademicSkill table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<AcademicSkill> findAll() throws SQLException {
        String query = "SELECT * FROM " + table;
        PreparedStatement statement = null;
        ResultSet result = null;
        List<AcademicSkill> ret = new ArrayList<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                ret.add(new AcademicSkill(
                        result.getString(field_id),
                        result.getString(field_designation)
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