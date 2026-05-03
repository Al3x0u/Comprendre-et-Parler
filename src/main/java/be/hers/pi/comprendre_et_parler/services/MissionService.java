package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOMission;
import be.hers.pi.comprendre_et_parler.models.AppliUser;
import be.hers.pi.comprendre_et_parler.models.Manager;
import be.hers.pi.comprendre_et_parler.models.Mission;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

public class MissionService {
    public ArrayList<Mission> getMissionsForWeek(AppliUser user, LocalDate weekStart) throws SQLException {

        DAOMission daoMission = new DAOMission();

        
    }
}
