package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.AcademicSkill;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.models.Interpreter;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DAOAcademicSkill implements DAO<AcademicSkill> {
    protected final String TABLE = "academicskill";
    protected final String FIELD_ID = "id";
    protected final String FIELD_DESIGNATION = "designation";

    /**
     * Search for a AcademicSkill in the database with the int parameter
     * @param id : identification of the AcademicSkill
     * @return AcademicSkill object who correspond to the given id else null
     * @throws SQLException if the database could not be reached
     */
    @Override
    public AcademicSkill find(int id) throws SQLException {
        String query = "SELECT * FROM " + TABLE + " WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        AcademicSkill ret = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if (result.next()) {
                ret = new AcademicSkill(
                        id,
                        result.getString(FIELD_DESIGNATION)
                );
            }
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return ret;
    }

    /**
     * Insert a AcademicSkill object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws SQLException if we couldn't connect to the database
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(AcademicSkill objectToInsert) throws AlreadyExistsException, SQLException {
        List<AcademicSkill> skills = findAll();
        for(AcademicSkill skill : skills){
            if (skill.equals(objectToInsert)) {
                throw new AlreadyExistsException("AcademicSkill " + objectToInsert.getDesignation() + " already exists at id" + skill.getId() );
            }
        }

        String query = "INSERT INTO %s(%s) VALUES(?)";
        query = String.format(query, TABLE, FIELD_DESIGNATION);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query, new String[]{FIELD_ID});
            statement.setString(1, objectToInsert.getDesignation());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next())
                objectToInsert.setId(generatedKeys.getInt(1));
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
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
    public void update(AcademicSkill objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        List<AcademicSkill> skills = findAll();

        for(AcademicSkill skill : skills){
            if (skill.equals(objectToUpdate)) {
                throw new AlreadyExistsException("AcademicSkill " + objectToUpdate.getDesignation() + " already exists at id" + skill.getId() );
            }
        }

        String query = "UPDATE %s SET %s = ? WHERE %s = ?";
        query = String.format(query, TABLE, FIELD_DESIGNATION, FIELD_ID);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, objectToUpdate.getDesignation());
            statement.setInt(2, objectToUpdate.getId());
            if(statement.executeUpdate() == 0){
                throw new NoSuchElementException("AcademicSkill " + objectToUpdate.getDesignation() + " was not found in database");
            }
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
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
    public void delete(AcademicSkill objectToDelete) throws NoSuchElementException, SQLException {
        String query = "DELETE FROM %s WHERE %s = ? AND %s = ?";
        query = String.format(query, TABLE, FIELD_ID, FIELD_DESIGNATION);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, objectToDelete.getId());
            statement.setString(2, objectToDelete.getDesignation());
            if(statement.executeUpdate() == 0){
                throw new NoSuchElementException("AcademicSkill " + objectToDelete.getDesignation() + " was not found in database");
            }
        }
        finally {
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
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
        String query = "SELECT * FROM " + TABLE;
        PreparedStatement statement = null;
        ResultSet result = null;
        List<AcademicSkill> ret = new ArrayList<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                ret.add(new AcademicSkill(
                        result.getInt(FIELD_ID),
                        result.getString(FIELD_DESIGNATION)
                ));
            }
        }
        finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return ret;
    }

    /**
     * Return all Academic Skill of An Interpreter
     * @param idInterpreter represent the id of the interpreter that we want the Academic Skill
     * @return  a List who represent the Academic Skill of the interpreter
     * @throws SQLException if the database could not be reached
     * @throws NoSuchElementException if the idInterpreter doesn't correspond to an Interpreter
     */
    public List<AcademicSkill> getAcademicSkillOfAnInterpreter(int idInterpreter) throws SQLException, NoSuchElementException{
        DAOInterpreter daoInterpreter = new DAOInterpreter();
        Interpreter interpreter = daoInterpreter.find(idInterpreter);
        if (interpreter == null) {
            throw new NoSuchElementException("Interpreter with id " + idInterpreter + " not found");
        }
        String query = "SELECT a." + FIELD_ID +", a."+ FIELD_DESIGNATION+" FROM " + TABLE + " a JOIN AcademicSkilllInterpreter asi ON a."+ FIELD_ID+" = asi.idAcademicSkill WHERE asi.idInterpreter = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        List<AcademicSkill> ret = new ArrayList<>();

        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, idInterpreter);
            result = statement.executeQuery();

            while (result.next()) {
                ret.add(new AcademicSkill(
                        result.getInt("id"),
                        result.getString("designation")
                ));
            }
        } finally {
            if (result != null) {
                try { result.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }

        return ret;
    }
}