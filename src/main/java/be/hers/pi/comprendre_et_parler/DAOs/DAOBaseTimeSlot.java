package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.BaseTimeSlot;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DAOBaseTimeSlot implements DAO<BaseTimeSlot> {

    public final String table = "timeslot";
    public final String field_id = "id";
    public final String field_startTime = "startHourTime";
    public final String field_endTime = "endHourTime";
    public final String field_day = "day";
    /**
     * Search for a BaseTimeSlot in the database with the String parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public BaseTimeSlot find(int id) throws SQLException {
        String query = "SELECT * FROM " + table + " WHERE " + field_day + " NOT NULL AND " + field_id + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        BaseTimeSlot ret = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if (result.next()) {
                java.sql.Date startTime = result.getDate(field_startTime);
                java.sql.Date endTime = result.getDate(field_endTime);
                ret = new BaseTimeSlot(
                        result.getInt(field_id),
                        LocalDateTime.ofEpochSecond(startTime.getTime(), 0, ZoneOffset.ofHours(1)).toLocalTime(), // Hardcoded as GMT+1
                        LocalDateTime.ofEpochSecond(endTime.getTime(), 0, ZoneOffset.ofHours(1)).toLocalTime(),
                        startTime.toLocalDate().getDayOfWeek()
                );
            }
        }
        finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return ret;
    }

    /**
     * Insert a BaseTimeSlot object in the database
     * @param objectToInsert an object of type BaseTimeSlot to add to the database
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
     * Update a BaseTimeSlot line who already exist in the database
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
     * Delete a BaseTimeSlot line in the table in the database
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
     * Return all line of BaseTimeSlot table in the database in a List
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<BaseTimeSlot> findAll() throws SQLException {
        return List.of();
    }
}