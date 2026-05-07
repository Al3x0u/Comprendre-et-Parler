package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.AcademicSkill;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.sql.SQLException;
import java.util.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DAOAcademicSkill extends DAO<AcademicSkill> {
    protected final String TABLE = "academicskill";
    protected final String FIELD_ID = "id";
    protected final String FIELD_DESIGNATION = "designation";

    @Override
    public AcademicSkill find(int id) throws SQLException {
        String query = String.format(
                "SELECT * FROM %s WHERE %s = ?",
                TABLE, FIELD_ID
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        AcademicSkill ret = null;
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
    public void create(AcademicSkill objectToInsert) throws AlreadyExistsException, SQLException {
        if (checkAlreadyExists(objectToInsert) >= 0)
            throw new AlreadyExistsException("AcademicSkill " + objectToInsert.getDesignation() + " already exists" );

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
    public void update(AcademicSkill objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        int idInDB = checkAlreadyExists(objectToUpdate);
        if (idInDB != objectToUpdate.getId() && idInDB >= 0)
            throw new AlreadyExistsException("AcademicSkill " + objectToUpdate.getDesignation() + " already exists" );

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
                throw new NoSuchElementException("[ERROR] There is no AcademicSkill with the id " + idObjectToDelete);
        } finally {
            closeStatement(statement);
        }
    }

    @Override
    public Set<AcademicSkill> findAll() throws SQLException {
        String query = String.format("SELECT * FROM %s", TABLE);
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<AcademicSkill> ret = new HashSet<>();
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
    protected int checkAlreadyExists(AcademicSkill objectToCheck) throws SQLException {
        String query = String.format(
                "SELECT %s FROM %s WHERE %s = ?",
                FIELD_ID, TABLE, FIELD_DESIGNATION
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToCheck.getDesignation());

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
    protected AcademicSkill getResult(ResultSet result) throws SQLException {
        return new AcademicSkill(
                result.getInt(FIELD_ID),
                result.getString(FIELD_DESIGNATION)
        );
    }

    /**
     * Return all AcademicSkill of An Interpreter
     * @param idInterpreter represent the id of the interpreter that we want the AcademicSkill
     * @return  a Set who represent the AcademicSkill of the interpreter
     * @throws IllegalArgumentException if id is < 0
     * @throws SQLException if the database could not be reached
     */
    public Set<AcademicSkill> getAcademicSkillOfAnInterpreter(int idInterpreter) throws IllegalArgumentException, SQLException {
        if (idInterpreter < 0)
            throw new IllegalArgumentException("Invalid id : " + idInterpreter);

        String query = String.format(
                "SELECT a.* FROM %s a JOIN %s asi ON a.%s = asi.%s WHERE asi.%s = ?",
                TABLE, DAOInterpreter.TABLE_ACADEMIC_SKILL_INTERPRETER, FIELD_ID, DAOInterpreter.ACADEMIC_SKILL_REF_SKILL,
                DAOInterpreter.ACADEMIC_SKILL_REF_INTERPRETER
        );
        PreparedStatement statement = null;
        ResultSet result = null;
        Set<AcademicSkill> ret = new HashSet<>();
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