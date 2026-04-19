package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.JobSkill;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOJobSkill implements DAO<JobSkill> {
    protected final static String TABLE = "JobSkill";
    protected final static String FIELD_ID = "id";
    protected static final String FIELD_SKILL = "skill";
    protected final static String FIELD_DESIGNATION = "designation";

    /**
     * Search for a JobSkill in the database with the int parameter
     * @param id identification of the JobSkill
     * @return JobSkill object which correspond to the given id else null
     * @throws SQLException if the database could not be reached
     */
    @Override
    public JobSkill find(int id) throws SQLException {
        return null;
    }

    /**
     * Insert a JobSkill Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws DuplicatePrimaryKeyException if the given id already used in the database
     * @throws SQLException if we couldn't connect to the database
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(JobSkill objectToInsert) throws AlreadyExistsException, SQLException {

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

    }

    /**
     * Return all line of JobSkill table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<JobSkill> findAll() throws SQLException {
        return null;
    }

    /**
     * @param login the id of the Interpreter
     * @return the list of job skills of the interpreter, empty if none
     * @throws SQLException if the database could not be reached
     */
    public static List<JobSkill> findAllByInterpreterLogin(String login) throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
        List<JobSkill> jobSkills = new ArrayList<>();
        String query = "SELECT j.id, j.designation FROM JobSkillInterpreter jsi JOIN JobSkill j ON j.id = jsi.skill WHERE jsi.interpreter = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            while(rs.next()){
                JobSkill jobSkill = new JobSkill(
                        rs.getInt("id"),
                        rs.getString("designation")
                );
                jobSkills.add(jobSkill);
            }
        }finally {
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
        return jobSkills;
    }

    /**
     * @param id the id of the job skill
     * @return the job skill with that id
     * @throws SQLException if the database could not be reached
     */
    public static JobSkill findById(int id) throws SQLException,NoSuchElementException {
        Connection connection = DatabaseConnector.getInstance();
        JobSkill jobSkill;
        String query = "SELECT * FROM JobSkill WHERE id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()){
                jobSkill = new JobSkill(
                        rs.getInt("id"),
                        rs.getString("designation")
                );
            }else{
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
        return jobSkill;
    }
}