package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.PunctualTimeSlot;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import javax.xml.crypto.Data;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOPunctualTimeSlot implements DAO<PunctualTimeSlot> {

    /**
     *
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws ConnectionException if the database could not be reached
     */
    public PunctualTimeSlot find(String id) throws ConnectionException {
        return null;
    }

    /**
     *
     * @param objectToInsert an object of PunctualTimeSlot to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException       if objectToInsert is already present in database
     * @throws SQLException          if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(PunctualTimeSlot objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        String queryTimeSlot = "INSERT INTO TimeSlot(startTime, endTime, day) VALUES(?, ?, ?)";
        PreparedStatement stmt = null;
        int rowsAffected = 0;

        try{
            stmt = connection.prepareStatement(queryTimeSlot);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(objectToInsert.getDate(), objectToInsert.getStartTime())));
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(objectToInsert.getDate(), objectToInsert.getEndTime())));
            stmt.setNull(3, Types.INTEGER);
            rowsAffected = stmt.executeUpdate();

            if(rowsAffected < 1){
                throw new NoSuchElementException();
            }
        }finally {
            DatabaseConnector.closeStmt(null, stmt);
        }
    }

    /**
     *
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(PunctualTimeSlot objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        PreparedStatement stmt = null;
        String query = "UPDATE TimeSlot SET startTime = ?, endTime = ?, day = ? WHERE id = ?";
        int rowsAffected = 0;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(objectToUpdate.getDate(), objectToUpdate.getStartTime())));
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.of(objectToUpdate.getDate(), objectToUpdate.getEndTime())));
            stmt.setNull(3, Types.INTEGER);
            stmt.setInt(4, objectToUpdate.getId());

            rowsAffected = stmt.executeUpdate();

            if(rowsAffected < 1){
                throw new NoSuchElementException();
            }
        }finally {
            DatabaseConnector.closeStmt(null, stmt);
        }
    }

    /**
     *
     *
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(PunctualTimeSlot objectToDelete) throws NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        PreparedStatement stmt = null;

        String query = "DELETE FROM TimeSlot WHERE id = ?";
        int rowsAffected = 0;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, objectToDelete.getId());
            rowsAffected = stmt.executeUpdate();

            if(rowsAffected < 1){
                throw new NoSuchElementException();
            }
        }finally {
            DatabaseConnector.closeStmt(null, stmt);
        }
    }

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<PunctualTimeSlot> findAll() throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<PunctualTimeSlot> list = new ArrayList<>();

        return list;
    }

    public static List<PunctualTimeSlot> findAllBydate(LocalDate date) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<PunctualTimeSlot> list = new ArrayList<>();
        String query = "SELECT t.*, a.* FROM TimeSlot t, Availability a WHERE t.id = a.timeSlot AND (TRUNC(startHourTime) = TRUNC(?)) AND t.day IS NULL)";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setDate(1, Date.valueOf(date));
            rs = stmt.executeQuery();
            while(rs.next()){
                PunctualTimeSlot punctualTimeSlot = new PunctualTimeSlot(
                        rs.getInt("id"),
                        rs.getString("interpreter"),
                        rs.getTime("startHourTime").toLocalTime(),
                        rs.getTime("endHourTime").toLocalTime(),
                        date
                );
                list.add(punctualTimeSlot);
            }
        }finally{
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return list;
    }

    public static PunctualTimeSlot findById(int id) throws SQLException, NoSuchElementException{
        Connection connection = DatabaseConnector.getConnection();
        PunctualTimeSlot time;
        String query = "SELECT * FROM TimeSlot WHERE id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()){
                time = new PunctualTimeSlot(
                        rs.getInt("id"),
                        rs.getString("interpreter"),
                        rs.getTime("startHourTime").toLocalTime(),
                        rs.getTime("endHourTime").toLocalTime(),
                        rs.getDate("startHourTime").toLocalDate()
                );
            }else{
                throw new NoSuchElementException();
            }
        }finally {
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return time;
    }


    public static List<PunctualTimeSlot> findAllById(int id) throws SQLException{
        Connection connection = DatabaseConnector.getConnection();
        List<PunctualTimeSlot> list = new ArrayList<>();
        String query = "SELECT * FROM TimeSlot WHERE id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            while(rs.next()){
                PunctualTimeSlot time = new PunctualTimeSlot(
                        rs.getInt("id"),
                        rs.getString("interpreter"),
                        rs.getTime("startHourTime").toLocalTime(),
                        rs.getTime("endHourTime").toLocalTime(),
                        rs.getDate("startHourTime").toLocalDate()
                );
                list.add(time);
            }
        }finally {
            DatabaseConnector.closeStmt(rs,stmt);
        }
        return list;
    }

    public static List<PunctualTimeSlot> findAllByInterpreterLogin(String login) throws SQLException{
        Connection connection = DatabaseConnector.getConnection();
        List<PunctualTimeSlot> list = new ArrayList<>();
        String query = "SELECT t.*, a.* FROM TimeSlot t JOIN Availability a ON t.id = a.timeSlot WHERE id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, login);
            rs = stmt.executeQuery();

            while(rs.next()){
                PunctualTimeSlot time = new PunctualTimeSlot(
                        rs.getInt("id"),
                        rs.getString("interpreter"),
                        rs.getTime("startHourTime").toLocalTime(),
                        rs.getTime("endHourTime").toLocalTime(),
                        rs.getDate("startHourTime").toLocalDate()
                );
                list.add(time);
            }
        }finally {
            DatabaseConnector.closeStmt(rs,stmt);
        }
        return list;
    }
}