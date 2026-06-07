package be.hers.pi.comprendre_et_parler.DAOs;

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

    @Override
    public JobSkill find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        JobSkill ret = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);

            result = statement.executeQuery();
            if (result.next())
                ret = getResult(result);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return ret;
    }

    @Override
    public void create(JobSkill objectToInsert) throws AlreadyExistsException, SQLException {
        int idInDB = checkAlreadyExists(objectToInsert);
        if (idInDB >= 0) {
            objectToInsert.setId(idInDB);
            throw new AlreadyExistsException("JobSkill " + objectToInsert.getDesignation() + " already exists");
        }

        String query = String.format("INSERT INTO %s VALUES(NULL, ?)", TABLE);
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query, new String[]{FIELD_ID});
            statement.setString(1, objectToInsert.getDesignation());

            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next())
                objectToInsert.setId(generatedKeys.getInt(1));
        } finally {
            closeResultSet(generatedKeys);
            closeStatement(statement);
        }
    }

    @Override
    public void update(JobSkill objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        if (find(objectToUpdate.getId()) == null)
            throw new NoSuchElementException("[ERROR] There is no JobSkill with the id " + objectToUpdate.getId());

        int idInDB = checkAlreadyExists(objectToUpdate);
        if (idInDB != objectToUpdate.getId() && idInDB >= 0)
            throw new AlreadyExistsException("JobSkill " + objectToUpdate.getDesignation() + " already exists" );

        String query = String.format(
                "UPDATE %s SET %s = ? WHERE %s = ?",
                TABLE, FIELD_DESIGNATION, FIELD_ID
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getDesignation());
            statement.setInt(2, objectToUpdate.getId());

            statement.executeUpdate();
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public void delete(int idObjectToDelete) throws NoSuchElementException, SQLException {
        String query = String.format(
                "DELETE FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idObjectToDelete);

            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no JobSkill with the id " + idObjectToDelete);
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public Set<JobSkill> findAll() throws SQLException {
        String query = String.format("SELECT * FROM %s", TABLE);
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<JobSkill> ret = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);

            result = statement.executeQuery();
            while (result.next())
                ret.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return ret;
    }

    @Override
    protected int checkAlreadyExists(JobSkill object) throws SQLException {
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                FIELD_ID, TABLE, FIELD_DESIGNATION
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, object.getDesignation());

            result = statement.executeQuery();
            if(result.next())
                return result.getInt(FIELD_ID);
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return -1;
    }

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
     * @throws IllegalArgumentException if id is < 0
     * @throws SQLException if the database could not be reached
     */
    public Set<JobSkill> getJobSkillOfAnInterpreter(int idInterpreter) throws IllegalArgumentException, SQLException {
        if (idInterpreter < 0)
            throw new IllegalArgumentException("Invalid id : " + idInterpreter);

        String query = String.format(
                "SELECT j.* FROM %s j JOIN %s jsi ON j.%s = jsi.%s WHERE jsi.%s = ?",
                TABLE, DAOInterpreter.TABLE_JOB_SKILL_INTERPRETER, FIELD_ID, DAOInterpreter.JOB_SKILL_REF_SKILL,
                DAOInterpreter.JOB_SKILL_REF_INTERPRETER
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<JobSkill> ret = new HashSet<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idInterpreter);

            result = statement.executeQuery();
            while (result.next())
                ret.add(getResult(result));
        } finally {
            closeResultSet(result);
            closeStatement(statement);
        }
        return ret;
    }
}