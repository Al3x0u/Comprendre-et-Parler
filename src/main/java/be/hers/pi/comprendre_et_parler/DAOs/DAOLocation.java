package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.Location;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOLocation implements DAO<Location> {

    /**
     * Search for a location in the database with the String parameter
     * @param id : identification of the location
     * @return Location object who correspond to the given id else null
     * @throws SQLException if the database could not be reached
     */

    public Location find(String id) throws SQLException {
        return null;
    }

    /**
     * Insert a Location Object in the database
     * @param objectToInsert : Object that we gonna insert
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws DuplicatePrimaryKeyException if the given id already used in the database
     * @throws SQLException if we couldn't connect to the database
     */
    @Override
    public void create(Location objectToInsert)
            throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
    }

    /**
     * Update a Location line who already exist in the database
     * @param objectToUpdate : object with the news information
     * @throws AlreadyExistsException if there are already a line with there information
     * @throws NoSuchElementException if there are not the element to update in the database
     * @throws SQLException if there are an error during the connection to the database
     */
    @Override
    public void update(Location objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
    }

    /**
     * Delete a line in the Location table in the database
     * @param objectToDelete : object with the information of the line who need to be deleted
     * @throws NoSuchElementException if we couldn't find the Location object in the database
     * @throws SQLException if we couldn't connect to the database
     */
    @Override
    public void delete(Location objectToDelete)
            throws NoSuchElementException, SQLException {
    }

    /**
     * Return all line of Location table in the database in Location Object in a List
     * @return a List who contains Location Object
     * @throws SQLException if the database could not be reached
     */
    @Override
    public List<Location> findAll() throws SQLException {
        return List.of();
    }

    /*
    * String designation, String city, String street,
    * String streetNum, int box
    */
    public static Location findById(int id)throws SQLException, NoSuchElementException{
        Connection connection = DatabaseConnector.getConnection();
        Location location = null;
        String query = "SELECT * FROM Location WHERE id = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if(rs.next()){
                location = new Location(
                        rs.getString("designation"),
                        rs.getString("city"),
                        rs.getString("street"),
                        rs.getString("streetNumber"),
                        rs.getInt("box")
                );
            }else{
                throw new NoSuchElementException();
            }
        }finally {
            DatabaseConnector.closeStmt(rs,stmt);
        }
        return location;
    }
}