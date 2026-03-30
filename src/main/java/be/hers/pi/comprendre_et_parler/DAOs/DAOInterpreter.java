package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOInterpreter implements DAO<Interpreter> {


    /*
    * constructor for DAOInterpreter object*/
    public DAOInterpreter() {
        try{
            DatabaseConnector.initialize();
        }catch (SQLException e){
            e.printStackTrace();//most useful than an Exception
        }
    }

    private void fermer(ResultSet rs, Statement stmt) {
        if(rs != null){
            try{
                rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        if(stmt != null){
            try{
                stmt.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    private Transportation getTransportation(int id, Connection connection)throws SQLException {
        Transportation transportation = null;
        String query = "SELECT * FROM transportation WHERE id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()){
                transportation = new Transportation(
                        rs.getInt("id"),
                        rs.getString("designation")
                );
            }
        }finally {
            fermer(rs, stmt);
        }
        return transportation;
    }

    private List<AcademicSkill> getAcademicSkills(String id, Connection connection) throws SQLException {
        List<AcademicSkill> academicSkils = new ArrayList<>();
        String query = "SELECT a.id, a.designation FROM AcademicSkillInterpreter asi JOIN AcademicSkill a ON a.id = asi.skill WHERE asi.interpreter = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            while(rs.next()){
                AcademicSkill academicSkil = new AcademicSkill(
                        rs.getInt("id"),
                        rs.getString("designation")
                );
                academicSkils.add(academicSkil);
            }
        }finally {
            fermer(rs, stmt);
        }
        return academicSkils;
    }

    /**
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Interpreter find(String id) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        try{
            DatabaseConnector.initialize();
        }catch(SQLException e){
            e.printStackTrace();
        }

        Interpreter interpreter = null;
        String query = "SELECT * FROM interpreter i JOIN AppliUser a ON a.id = i.id WHERE i.id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, id);
            rs = stmt.executeQuery();

            if(rs.next()){
                int idTransportation = rs.getInt("transportation");
                interpreter = new Interpreter(
                        rs.getString("id"),
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getDate("birthday").toLocalDate(),
                        rs.getString("hashPassword"),
                        rs.getString("mail"),
                        rs.getString("phone"),
                        rs.getInt("hourQuotaWeek"),
                        rs.getInt("hourQuotaYear"),
                        rs.getBoolean("isManager"),

                        getTransportation(idTransportation, connection),
                        getAcademicSkills(id, connection),
                        getJobSkills(id, connection),
                        getBeneficiaries(id, connection)
                );
            }
        }finally {
            fermer(rs, stmt);
        }

        return interpreter;
        return null;
    }

    /**
     * @param objectToInsert an object of type T to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(Interpreter objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
    }

    /**
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(Interpreter objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
    }

    /**
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(Interpreter objectToDelete)
            throws NoSuchElementException, SQLException {
    }

    /**
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Interpreter> findAll() throws SQLException {
        return List.of();
    }

    /**
     * @param start represent the start of the time that we want the availability
     * @param end represent the end of the time that we want the availability
     * @param date represent the date
     * @return a List of Interpreter who are available in the given time and date
     */
    public List<Interpreter> findAvailable(LocalTime start, LocalTime end, LocalDate date) {
        return null;
    }

    /**
     * @param idAcademicSkills the id of the AcademicSkill
     * @return a List of Interpreter who have the AcademicSkill having the idAcademicSkills
     * @throws NoSuchElementException if idAcademicSkills doesn't correspond to the id of any AcademicSkill
     */
    public List<Interpreter> findByAcademicSkills(int idAcademicSkills)
            throws NoSuchElementException {
        return null;
    }
}