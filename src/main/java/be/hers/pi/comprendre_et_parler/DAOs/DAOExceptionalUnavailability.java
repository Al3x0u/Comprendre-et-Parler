package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.ExceptionalUnavailability;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;
import be.hers.pi.comprendre_et_parler.models.PunctualTimeSlot;
import org.thymeleaf.standard.processor.StandardAttrprependTagProcessor;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOExceptionalUnavailability implements DAO<ExceptionalUnavailability> {

    /**
     *
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public ExceptionalUnavailability find(String id) throws SQLException {
        return null;
    }

    /**
     *
     * @param objectToInsert an object of type T to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException       if objectToInsert is already present in database
     * @throws SQLException          if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(ExceptionalUnavailability objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
    }

    /**
     *
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(ExceptionalUnavailability objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
    }

    /**
     *
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(ExceptionalUnavailability objectToDelete)
            throws NoSuchElementException, SQLException {
    }

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<ExceptionalUnavailability> findAll() throws SQLException {
        return List.of();
    }

    /**
     *
     * @param idInterpreter the id of an Interpreter
     * @return a List of ExceptionalUnavailability which contains the ExceptionalUnavailability of an Interpreter
     * @throws NoSuchElementException if there are not a Interpreter with the given id
     */
    public List<ExceptionalUnavailability> findForInterpreter(String idInterpreter)
            throws NoSuchElementException {
        return null;
    }

    public static List<ExceptionalUnavailability> findById(int id)throws SQLException{
        Connection connection = DatabaseConnector.getConnection();
        List<ExceptionalUnavailability> list = new ArrayList<>();
        String query = "SELECT t.*, u.* FROM timeSlot t, Unavailability u WHERE t.id = u.timeSlot AND t.id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            while(rs.next()){
                ExceptionalUnavailability unavailability = new ExceptionalUnavailability(
                        rs.getString("reason"),
                        new PunctualTimeSlot(
                                rs.getInt("id"),
                                rs.getString("interpreter"),
                                rs.getTime("startHourTime").toLocalTime(),
                                rs.getTime("endHourTime").toLocalTime(),
                                rs.getDate("startHourTime").toLocalDate()
                        ),
                        DAOInterpreter.findByLogin(rs.getString("login"))
                );
                list.add(unavailability);
            }
        }finally {
            DatabaseConnector.closeStmt(rs,stmt);
        }
        return list;
    }

    public static List<ExceptionalUnavailability> findByInterpreterLogin(String login) throws SQLException{
        Connection connection = DatabaseConnector.getConnection();
        List<ExceptionalUnavailability> list = new ArrayList<>();
        String query = "SELECT t.*, u.* FROM TimeSlot t JOIN Unavailability u ON t.id = u.timeSlot WHERE u.interpreter = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            while (rs.next()) {
                ExceptionalUnavailability unavailability = new ExceptionalUnavailability(
                        rs.getString("reason"),
                        new PunctualTimeSlot(
                                rs.getInt("id"),
                                rs.getString("interpreter"),
                                rs.getTime("startTime").toLocalTime(),
                                rs.getTime("endTime").toLocalTime(),
                                rs.getDate("startTime").toLocalDate()
                        ),
                        DAOInterpreter.findByLogin(login)
                );
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return list;
    }
}