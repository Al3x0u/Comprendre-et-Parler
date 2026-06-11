package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.NoSuchElementException;

@Service
public class PasswordService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Hash the new password and update it in the database, then set passwordUpdated to true.
     * @param user the authenticated user whose password to update, must not be null
     * @param newPassword the new plain text password to hash and store, must not be null
     * @throws ConnectionException if a connection error occurred
     * @throws SQLException if any other database error occurred
     * @throws NoSuchElementException if no user with this id exists in the database
     * @post the user's hashed password has been updated in the database and passwordUpdated
     *       has been set to true
     */
    public void changePassword(AppliUser user, String newPassword) throws SQLException, ConnectionException {
        String hashedNewPassword = encoder.encode(newPassword);
        String oldHash = user.getHashedPassword();
        user.setHashedPassword(hashedNewPassword);
        try {
            SQLWrap.callTransaction(new DAOAppliUser()::updatePasswordUpdated, user);
        } catch (SQLException | ConnectionException e) {
            user.setHashedPassword(oldHash);
            throw e;
        }
        user.setPasswordUpdated(true);
    }

    /**
     * Verify that the given password is the correct one
     * @param user   the authenticated user
     * @param password the password to verify
     * @return true if it's the correct password else false
     */
    public boolean verifyCurrentPassword(AppliUser user, String password) {
        return encoder.matches(password, user.getHashedPassword());
    }
}
