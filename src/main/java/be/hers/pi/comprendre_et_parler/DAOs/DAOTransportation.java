package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;
import be.hers.pi.comprendre_et_parler.models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOTransportation implements DAO<Transportation> {

    /**
     *
     * @param objectToInsert an object of type T to add to the database
     * @post objectToInsert has been added to the database, and the change was commited
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException if objectToInsert is already present in database
     * @throws SQLException if the database could not be reached
     */
    @Override
    public void create(Transportation objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException{

    }

    /**
     *
     * @param objectToUpdate the object to edit in the database
     * @post the line referenced by objectToUpdate's id field has been updated with objectToUpdate's attributes, and the change was commited
     * @throws NoSuchElementException if no object matching objectToUpdate's id was present in the database
     * @throws SQLException if the database could not be reached
     */
    @Override
    public void update(Transportation objectToUpdate) throws AlreadyExistsException, NoSuchElementException, SQLException{

    }

    /**
     *
     * @param objectToDelete the object to delete in the database
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException if the database could not be reached
     */
    @Override
    public void delete(Transportation objectToDelete) throws NoSuchElementException, SQLException{

    }

    /**
     * Search for a Transportation in the database with the int parameter
     * @param id the primary key of the object to find in database
     * @return the object identified by id in database, or null if none was present
     * @throws SQLException if the database could not be reached
     */
    @Override
    public Transportation find(int id) throws SQLException {
        return findById(id);
    }

    /**
     *
     * @return every object of the corresponding type present in database (possibly an empty list)
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Transportation> findAll() throws SQLException{
        return List.of(null);
    }

    /**
     * @param id the id of the transportation in database
     * @return the Transportation identified by id, or null if none was found
     * @throws SQLException if the database could not be reached
     */
    public static Transportation findById(int id)throws SQLException {
        Connection connection = DatabaseConnector.getInstance();
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
            if(rs != null){
                rs.close();
            }
            if(stmt != null){
                stmt.close();
            }
        }
        return transportation;
    }
}
