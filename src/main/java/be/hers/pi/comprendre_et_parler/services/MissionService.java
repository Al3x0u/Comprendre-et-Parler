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
    /**
     * Return the list of missions for a given week, filtered according to the user's role.
     * @param user the user requesting the schedule (Manager, Interpreter or Beneficiary)
     * @param weekStart the date of any day within the target week;
     * @throws SQLException if the database could not be reached
     */
    public ArrayList<Mission> getMissionsForWeek(AppliUser user, LocalDate weekStart) throws SQLException {

        DAOMission daoMission = new DAOMission();

        int yearNumber = weekStart.getYear();
        int weekNumber = weekStart.get(java.time.temporal.WeekFields.of(java.util.Locale.getDefault()).weekOfWeekBasedYear());

        Set<Mission> missions;

        if (user instanceof Manager) {
            missions = daoMission.getAllMissionsForWeek(yearNumber, weekNumber);
        }
        else {
            missions = daoMission.getScheduleForWeek(user.getId(), yearNumber, weekNumber);
        }

        return new ArrayList<>(missions);
    }
}
