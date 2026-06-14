package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.AppliUser;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class DAOAppliUser {
    protected static final String TABLE_APPLIUSER = "AppliUser";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_PASSWORD_UPDATED = "passwordUpdated";
    protected static final String FIELD_HASHED_PASSWORD = "hashedPassword";
    protected static final String FIELD_EMAIL = "email";

    /**
     * Retrieve the passwordUpdated flag of a user from the database
     * @param id the id of the user
     * @return true if the password has been updated, false otherwise
     * @throws SQLException if the database could not be reached
     */
    public boolean getPasswordUpdated(int id) throws SQLException, NoSuchElementException {
        String query = "SELECT " + FIELD_PASSWORD_UPDATED + " FROM " + TABLE_APPLIUSER + " WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if(!result.next())
                throw new NoSuchElementException("[ERROR] There is no AppliUser with the id " + id);
            return result.getInt(FIELD_PASSWORD_UPDATED) == 1;
        } finally {
            if(result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Update the passwordUpdated flag of an AppliUser in the database
     * @param user the user to update password
     * @throws SQLException if the database could not be reached
     * @throws NoSuchElementException if no AppliUser with this id exists in the database
     * @post the passwordUpdated flag of the AppliUser has been set to true in the database
     */
    public void updatePasswordUpdated(AppliUser user) throws SQLException, NoSuchElementException {
        String query = "UPDATE " + TABLE_APPLIUSER + " SET " + FIELD_PASSWORD_UPDATED + " = 1, " + FIELD_HASHED_PASSWORD + " = ? WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, user.getHashedPassword());
            statement.setInt(2, user.getId());
            if(statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no AppliUser with the id " + user.getId());
        } finally {
            if(statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Disable a user account by setting its end date to today
     * @param id the id of the user to disable
     * @throws SQLException if the database could not be reached
     * @throws NoSuchElementException if no AppliUser with this id exists in the database
     */
    public void disableAccount(int id) throws SQLException, NoSuchElementException {
        String query = "UPDATE AppliUserT SET end = SYSDATE WHERE id = ?";
        PreparedStatement statement = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            if (statement.executeUpdate() == 0)
                throw new NoSuchElementException("[ERROR] There is no AppliUser with the id " + id);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Check if a user account is active (end date is null or in the future)
     * @param id the id of the user to check
     * @return true if the account is active, false if it has been disabled
     * @throws SQLException if the database could not be reached
     * @throws NoSuchElementException if no AppliUser with this id exists in the database
     */
    public boolean isAccountActive(int id) throws SQLException, NoSuchElementException {
        String query = "SELECT end FROM AppliUserT WHERE id = ?";
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setInt(1, id);
            result = statement.executeQuery();
            if (!result.next())
                throw new NoSuchElementException("[ERROR] There is no AppliUser with the id " + id);
            return result.getDate("end") == null;
        } finally {
            if (result != null) {
                try {
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Get the id of the active account with this email
     * @param email l'email recherché
     * @return the id of the user or -1 if there is no active account with the email
     * @throws SQLException is an database error occurs
     */
    public int findByEmail(String email) throws SQLException{
        int theId = -1;
        String query = "SELECT " + FIELD_ID + " FROM " +
                TABLE_APPLIUSER + " WHERE " + FIELD_EMAIL + " = ?";
        PreparedStatement statement = null;
        ResultSet result = null;

        try{
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, email);
            result = statement.executeQuery();

            if(result.next()){
                theId = result.getInt(FIELD_ID);
            }
        }finally {
            if(result != null){
                try{
                    result.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return theId;
    }

    /**
     * Update the hash of the password of an user et passwordUpdated
     * @param id the id of the user
     * @param hashedPassword the new hash
     * @throws NoSuchElementException if there is no account with this id
     * @throws SQLException is an database error occurs
     * @post the hash has been replace and the flag passwordUpdated is 1
     */
    public void updatePassword(int id, String hashedPassword) throws NoSuchElementException, SQLException{
        String query = "UPDATE " + TABLE_APPLIUSER + " SET " + FIELD_HASHED_PASSWORD + " = ?, "
                + FIELD_PASSWORD_UPDATED  + " = 1 WHERE " + FIELD_ID + " = ?";
        PreparedStatement statement = null;
        try{
            statement = DatabaseConnector.getInstance().prepareStatement(query);
            statement.setString(1, hashedPassword);
            statement.setInt(2, id);
            if(statement.executeUpdate() == 0){
                throw new NoSuchElementException("[ERROR] There is no user with the id " + id);
            }
        }finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
