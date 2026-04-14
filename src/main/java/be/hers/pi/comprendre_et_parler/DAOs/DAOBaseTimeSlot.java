package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.BaseTimeSlot;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;
import be.hers.pi.comprendre_et_parler.models.Status;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DAOBaseTimeSlot implements DAO<BaseTimeSlot> {

    protected static final String TABLE = "timeslot";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_START_TIME = "startTime";
    protected static final String FIELD_END_TIME = "endTime";
    protected static final String FIELD_DAY = "day";

    /**
     * Search for a BaseTimeSlot in the database with the String parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public BaseTimeSlot find(int id) throws SQLException {
        String query = "SELECT * FROM " + TABLE + " WHERE " + FIELD_DAY + " IS NOT NULL AND " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        BaseTimeSlot ret = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if (result.next()) {
                java.sql.Date startTime = result.getDate(FIELD_START_TIME);
                ret = new BaseTimeSlot(
                        result.getInt(FIELD_ID),
                        result.getTime(FIELD_START_TIME).toLocalTime(),
                        result.getTime(FIELD_END_TIME).toLocalTime(),
                        DayOfWeek.of(result.getInt(FIELD_DAY))
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
    public void create(BaseTimeSlot objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
        // Manage invalid states
        BaseTimeSlot objectInDB = find(objectToInsert.getId());
        if (objectInDB != null) {
            if (objectInDB.equals(objectToInsert))
                throw new AlreadyExistsException("Object already exists in database");
            else
                throw new DuplicatePrimaryKeyException("Object is already present in database under a different primary key");
        }

        // Attempt insertion
        String query = "INSERT INTO %s(%s, %s, %s) VALUES(?, ?, ?)";
        query = String.format(query, TABLE, FIELD_START_TIME, FIELD_END_TIME, FIELD_DAY);
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            //statement.setInt(1, objectToInsert.getId());
            statement.setTime(1, Time.valueOf(objectToInsert.getStartTime()));
            statement.setTime(2, Time.valueOf(objectToInsert.getEndTime()));
            statement.setInt(3, objectToInsert.getDay().getValue());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    /**
     * Update a BaseTimeSlot line who already exist in the database
     * @param objectToUpdate the object to edit in the database
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     */
    @Override
    public void update(BaseTimeSlot objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException {

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
        String query = "SELECT * FROM " + TABLE + " WHERE " + FIELD_DAY + " IS NOT NULL";
        PreparedStatement statement = null;
        ResultSet result = null;
        List<BaseTimeSlot> ret = new ArrayList<>();
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            result = statement.executeQuery();
            while (result.next()) {
                ret.add(new BaseTimeSlot(
                        result.getInt(FIELD_ID),
                        result.getTime(FIELD_START_TIME).toLocalTime(),
                        result.getTime(FIELD_END_TIME).toLocalTime(),
                        DayOfWeek.of(result.getInt(FIELD_DAY))
                ));
            }
        } finally {
            if (statement != null)
                statement.close();
            if (result != null)
                result.close();
        }
        return ret;
    }
}