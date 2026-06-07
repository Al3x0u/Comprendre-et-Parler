package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.models.AppliUser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

public class DAOAppliUser {
    protected static final String TABLE_APPLIUSER = "AppliUser";
    protected static final String FIELD_ID = "id";
    protected static final String FIELD_PASSWORD_UPDATED = "passwordUpdated";
    protected static final String FIELD_HASHED_PASSWORD = "hashedPassword";

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
}
