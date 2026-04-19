package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.AcademicSkill;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOAcademicSkill implements DAO<AcademicSkill> {
    protected static final String TABLE = "academicskill";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_SKILL = "skill";
    protected static final String FIELD_DESIGNATION = "designation";

    /**
     * Search for an AcademicSkill in the database with the int parameter
     * @param id : identification of the AcademicSkill
     * @return AcademicSkill object who correspond to the given id else null
     * @throws SQLException if the database could not be reached
     */
    @Override
    public AcademicSkill find(int id) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        AcademicSkill academicSkill;
        String query = "SELECT * FROM AcademicSkill  WHERE id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()){
                academicSkill = new AcademicSkill(
                        rs.getInt("id"),
                        rs.getString("designation")
                );
            }else {
                throw new NoSuchElementException();
            }
        }finally {
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
        return academicSkill;
    }

    /**
     * Insert an AcademicSkill object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws SQLException if we couldn't connect to the database
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(AcademicSkill objectToInsert) throws AlreadyExistsException, SQLException {

    }

    /**
     * Update an AcademicSkill line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(AcademicSkill objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {

    }

    /**
     * Delete an AcademicSkill line in the table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the AcademicSkill object in the database
     * @throws SQLException if we couldn't connect to the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(AcademicSkill objectToDelete) throws NoSuchElementException, SQLException {

    }

    /**
     * Return all line of AcademicSkill table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<AcademicSkill> findAll() throws SQLException {
        return null;
    }

    /**
     * @param login the login of the interpreter
     * @return the list of academic skills of the interpreter, empty if none
     * @throws SQLException if the database could not be reached
     */
    public static List<AcademicSkill> findAllByInterpreterLogin(String login) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        List<AcademicSkill> academicSkills = new ArrayList<>();
        String query = "SELECT a.* FROM AcademicSkillInterpreter asi JOIN AcademicSkill a  ON a.id = asi.skill WHERE asi.interpreter = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            while(rs.next()){
                AcademicSkill academicSkil = new AcademicSkill(
                        rs.getInt("id"),
                        rs.getString("designation")
                );
                academicSkills.add(academicSkil);
            }
        }finally {
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
        return academicSkills;
    }
}