package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Interpreter;
import be.hers.pi.comprendre_et_parler.models.JobSkill;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.NoSuchElementException;

public class DAOJobSkill extends DAO<JobSkill> {
    protected final String TABLE = "jobskill";
    protected final String FIELD_ID = "id";
    protected final String FIELD_DESIGNATION = "designation";

    /**
     * Search for a JobSkill in the database with the int parameter
     * @param id identification of the JobSkill
     * @return JobSkill object which correspond to the given id else null
     * @throws SQLException if the database could not be reached
     */
    @Override
    public JobSkill find(int id) throws SQLException {
        String query = "SELECT * FROM " + TABLE + " WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        JobSkill ret = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if (result.next()) {
                ret = getResult(result);
            }
        }
        finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return ret;
    }

    /**
     * Insert a JobSkill Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws SQLException if we couldn't connect to the database
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(JobSkill objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert)) {
            throw new AlreadyExistsException("JobSkill " + objectToInsert.getDesignation() + " already exists" );
        }

        String query = "INSERT INTO %s(%s) VALUES(?)";
        query = String.format(query, TABLE, FIELD_DESIGNATION);
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query, new String[]{FIELD_ID});
            statement.setString(1, objectToInsert.getDesignation());
            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next())
                objectToInsert.setId(generatedKeys.getInt(1));
        }
        finally {
            closeResultSet(generatedKeys);
            closeStatement(statement);
        }
    }

    /**
     * Update a JobSkill line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(JobSkill objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (checkAlreadyExists(objectToUpdate)) {
            throw new AlreadyExistsException("JobSkill " + objectToUpdate.getDesignation() + " already exists" );
        }

        String query = "UPDATE %s SET %s = ? WHERE %s = ?";
        query = String.format(query, TABLE, FIELD_DESIGNATION, FIELD_ID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getDesignation());
            statement.setInt(2, objectToUpdate.getId());
            if(statement.executeUpdate() == 0){
                throw new NoSuchElementException("JobSkill " + objectToUpdate.getDesignation() + " was not found in database");
            }
        }
        finally {
            closeStatement(statement);
        }
    }

    /**
     * Delete a JobSkill line in the table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the JobSkill object in the database
     * @throws SQLException if we couldn't connect to the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(JobSkill objectToDelete) throws NoSuchElementException, SQLException {
        String query = "DELETE FROM %s WHERE %s = ? AND %s = ?";
        query = String.format(query, TABLE, FIELD_ID, FIELD_DESIGNATION);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            statement.setString(2, objectToDelete.getDesignation());
            if(statement.executeUpdate() == 0){
                throw new NoSuchElementException("JobSkill " + objectToDelete.getDesignation() + " was not found in database");
            }
        }
        finally {
            closeStatement(statement);
        }
    }

    /**
     * Return all line of JobSkill table in the database in a Set
     * @return every object of the corresponding type present in database (possibly an empty Set)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Set<JobSkill> findAll() throws SQLException {
        String query = "SELECT * FROM " + TABLE;
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<JobSkill> ret = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                ret.add(getResult(result));
            }
        }
        finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return ret;
    }

    /**
     * Check if an object already exists in the database
     *
     * @param object the object to check
     * @return true if the object already exists, else false
     * @throws SQLException if the database could not be reached
     */
    @Override
    protected boolean checkAlreadyExists(JobSkill object) throws SQLException {
        String query = "SELECT * FROM %s WHERE %s = ?";
        query = String.format(query, TABLE, FIELD_DESIGNATION);
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, object.getDesignation());
            result = statement.executeQuery();
            if (result.next()) {
                return true;
            }
            return false;
        }finally {
            closeResultSet(result);
            closeStatement(statement);
        }
    }

    /**
     * Build an object from a ResultSet
     *
     * @param result the ResultSet to read from
     * @return an object built from the ResultSet
     * @throws SQLException if the database could not be reached
     */
    @Override
    protected JobSkill getResult(ResultSet result) throws SQLException {
        return new JobSkill(
                result.getInt(FIELD_ID),
                result.getString(FIELD_DESIGNATION)
        );
    }

    /**
     * Return all Job Skill of An Interpreter
     * @param idInterpreter represent the id of the interpreter that we want the Job Skill
     * @return  a Set who represent the Job Skill of the interpreter
     * @throws SQLException if the database could not be reached
     */
    public Set<JobSkill> getJobSkillOfAnInterpreter(int idInterpreter) throws SQLException{
        String query = "SELECT a." + FIELD_ID +", a."+ FIELD_DESIGNATION+" FROM " + TABLE
                + " a JOIN JobSkillInterpreter asi ON a."+ FIELD_ID+" = asi.skill WHERE " +
                "asi.interpreter = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<JobSkill> ret = new HashSet<>();

        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idInterpreter);
            result = statement.executeQuery();

            while (result.next()) {
                ret.add(getResult(result));
            }
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }

        return ret;
    }
}