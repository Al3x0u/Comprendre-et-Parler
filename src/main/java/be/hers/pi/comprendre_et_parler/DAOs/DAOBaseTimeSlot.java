package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.BaseTimeSlot;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOBaseTimeSlot implements DAO<BaseTimeSlot> {

    /**
     *
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    public BaseTimeSlot find(String id) throws SQLException {
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
    public void create(BaseTimeSlot objectToInsert)
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
    public void update(BaseTimeSlot objectToUpdate)
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
    public void delete(BaseTimeSlot objectToDelete)
            throws NoSuchElementException, SQLException {

    }

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<BaseTimeSlot> findAll() throws SQLException {
        return List.of();
    }

    public static List<BaseTimeSlot> findByDay(int day) throws SQLException {
        Connection connection = DatabaseConnector.getConnection();
        List<BaseTimeSlot> baseTimeSlots = new ArrayList<>();
        String query = "SELECT t.*, a.* FROM TimeSlot t, Availability a WHERE t.id = a.timeSlot AND t.day = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, day);
            rs = stmt.executeQuery();

            while(rs.next()){
                BaseTimeSlot baseTimeSlot = new BaseTimeSlot(
                        rs.getString("interpreter"),
                        rs.getTime("startHourTime").toLocalTime(),
                        rs.getTime("endHourTime").toLocalTime(),
                        rs.getInt("day")
                );
                baseTimeSlots.add(baseTimeSlot);
            }
        }finally{
            DatabaseConnector.closeStmt(rs, stmt);
        }
        return baseTimeSlots;
    }
}