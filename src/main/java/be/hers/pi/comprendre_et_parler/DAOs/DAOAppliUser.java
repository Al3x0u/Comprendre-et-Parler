package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;
import be.hers.pi.comprendre_et_parler.exceptions.DuplicatePrimaryKeyException;
import be.hers.pi.comprendre_et_parler.models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DAOAppliUser implements DAO<AppliUser> {

    /**
     *
     * @param objectToInsert an object of type T to add to the database
     * @throws DuplicatePrimaryKeyException if an object matching objectToInsert's id but not all of its attributes is already present in database
     * @throws AlreadyExistsException       if objectToInsert is already present in database
     * @throws SQLException          if the database could not be reached
     * @post objectToInsert has been added to the database, and the change was commited
     */
    @Override
    public void create(AppliUser objectToInsert) throws AlreadyExistsException, DuplicatePrimaryKeyException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        String query = "INSERT INTO AppliUser (login, firstName, lastName, birthdate, hashedPassword, email, phoneNumber) VALUES(?, ?, ?, ?, ?, ?, ?)";
        int rowsAffected = 0;
        PreparedStatement stmt = null;
        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, objectToInsert.getLogin());
            stmt.setString(2, objectToInsert.getFirstName());
            stmt.setString(3, objectToInsert.getLastName());
            stmt.setDate(4, Date.valueOf(objectToInsert.getBirthday()));
            stmt.setString(5, objectToInsert.getPassword());
            stmt.setString(6, objectToInsert.getMail());
            stmt.setString(7, objectToInsert.getPhone());
            rowsAffected = stmt.executeUpdate();
            if(rowsAffected < 1 ){
                throw new NoSuchElementException();//
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
    public void update(AppliUser objectToUpdate)
            throws AlreadyExistsException, NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        String query = "UPDATE AppliUser SET firstName = ?, lastName = ?, birthday = ?, hashedPassword = ?, email = ?, phoneNumber = ? WHERE login = ?";
        int rowsAffected = 0;
        PreparedStatement stmt = null;

        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, objectToUpdate.getFirstName());
            stmt.setString(2, objectToUpdate.getLastName());
            stmt.setDate(3, Date.valueOf(objectToUpdate.getBirthday()));
            stmt.setString(4, objectToUpdate.getPassword());
            stmt.setString(5, objectToUpdate.getMail());
            stmt.setString(6, objectToUpdate.getPhone());
            stmt.setString(7, objectToUpdate.getLogin());
            rowsAffected = stmt.executeUpdate();

            if(rowsAffected < 1 ){
                throw new NoSuchElementException();
            }
        }finally {
            DatabaseConnector.closeStmt(null, stmt);
        }

    }

    /**
     *
     * @param objectToDelete the object to delete in the database
     * @throws NoSuchElementException if no object matching every attribute of objectToDelete was present in the database
     * @throws SQLException    if the database could not be reached
     * @post the object matching every attribute of objectToDelete has been deleted from the database, and the change was commited
     */
    @Override
    public void delete(AppliUser objectToDelete)
            throws NoSuchElementException, SQLException {
        Connection connection = DatabaseConnector.getConnection();
        String query = "DELETE Appliuser WHERE login = ?";
        int rowsAffected = 0;

        PreparedStatement stmt = null;
        try{
            stmt = connection.prepareStatement(query);
            stmt.setString(1, objectToDelete.getLogin());
            rowsAffected = stmt.executeUpdate();

            if(rowsAffected < 1 ){
                throw new NoSuchElementException();
            }
        }finally {
            DatabaseConnector.closeStmt(null, stmt);
        }

    }

    /**
     *
     * @return null, AppliUser is abstract
     */
    @Override
    public List<AppliUser> findAll() throws SQLException{
        return null;
    }
}
