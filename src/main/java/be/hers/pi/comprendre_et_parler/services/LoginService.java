package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.*;
import be.hers.pi.comprendre_et_parler.exceptions.*;
import be.hers.pi.comprendre_et_parler.models.*;
import be.hers.pi.comprendre_et_parler.services.wrappers.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class LoginService {

    /**
     * Retrieve a user from the database based on their login and password
     * @param login the login of the user, must not be null
     * @param password the plain text password to verify against the stored hash
     * @return the AppliUser matching the login and password, or null if no match was found,
     * if the credentials are incorrect, or if a database error occurred
     */
    public AppliUser getUserData(String login, String password) {
        try {
            login = login.trim();
            AppliUser user = SQLWrap.call((FunctionWithSQLException<String, Manager>) new DAOManager()::find, login);
            if (user == null) {
                user = SQLWrap.call((FunctionWithSQLException<String, Interpreter>) new DAOInterpreter()::find, login);
                if (user == null) {
                    user = SQLWrap.call((FunctionWithSQLException<String, Beneficiary>) new DAOBeneficiary()::find, login);
                }
            }

            if (user == null || !checkUserLogin(user, login, password)) {
                return null;
            }
            boolean passwordUpdated;
            if (user instanceof Manager) {
                passwordUpdated = new DAOManager().getPasswordUpdated(user.getId());
            } else if (user instanceof Interpreter) {
                passwordUpdated = new DAOInterpreter().getPasswordUpdated(user.getId());
            } else {
                passwordUpdated = new DAOBeneficiary().getPasswordUpdated(user.getId());
            }
            user.setPasswordUpdated(passwordUpdated);

            return user;
        } catch (ConnectionException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Verify that the given login and password match the user's stored credentials
     * @param user the user whose credentials to check, must not be null
     * @param login the login to verify against the user's stored login
     * @param password the plain text password to verify against the user's stored hash
     * @return true if both the login and password match the user's stored credentials, false otherwise
     */
    private boolean checkUserLogin(AppliUser user , String login, String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return user.getLogin().equals(login) && encoder.matches(password, user.getHashedPassword());
    }
}
