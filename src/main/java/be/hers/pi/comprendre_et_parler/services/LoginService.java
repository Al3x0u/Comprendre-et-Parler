package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOBeneficiary;
import be.hers.pi.comprendre_et_parler.DAOs.DAOInterpreter;
import be.hers.pi.comprendre_et_parler.DAOs.DAOManager;
import be.hers.pi.comprendre_et_parler.models.AppliUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class LoginService {

    public AppliUser getUserData(String login, String password){
        AppliUser user = null;
        if(login.trim().startsWith("i")){
            try {
                user = new DAOInterpreter().find(login);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else if(login.trim().startsWith("b")){
            try {
                user = new DAOBeneficiary().find(login);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else if(login.trim().startsWith("r")){
            try {
                user = new DAOManager().find(login);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else{
            return null;
        }
        if(user == null || !checkUserLogin(user, login, password) ) {
            return null;
        }

        return user;
    }

    private boolean checkUserLogin(AppliUser user , String login, String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return user.getLogin().equals(login) && encoder.matches(password, user.getHashedPassword());
    }
}
