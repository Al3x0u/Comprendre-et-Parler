package be.hers.pi.comprendre_et_parler.services;

import be.hers.pi.comprendre_et_parler.DAOs.DAOMission;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.AppliUser;
import be.hers.pi.comprendre_et_parler.models.Manager;
import be.hers.pi.comprendre_et_parler.models.Mission;
import be.hers.pi.comprendre_et_parler.models.MissionFilter;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MissionService {

    private final DAOMission daoMission;

    public MissionService(DAOMission daoMission) {
        this.daoMission = daoMission;
    }

    public List<Mission> getByFilter(MissionFilter filter) throws SQLException, ConnectionException {
        Set<Mission> all = SQLWrap.call(daoMission::findAll);
        return all.stream()
                .filter(m -> filter.getBeneficiary() == null ||
                        m.getBeneficiary().equals(filter.getBeneficiary()))
                .filter(m -> filter.getInterpreter() == null ||
                        m.getInterpreters().contains(filter.getInterpreter()))
                .filter(m -> filter.getJobSkill() == null ||
                        m.getJobSkill().equals(filter.getJobSkill()))
                .filter(m -> filter.getAcademicSkill() == null ||
                        m.getAcademicSkill().equals(filter.getAcademicSkill()))
                .filter(m -> filter.getLocation() == null ||
                        m.getLocation().equals(filter.getLocation()))
                .filter(m -> filter.getMinImportance() == null ||
                        m.getImportance() >= filter.getMinImportance())
                .filter(m -> filter.getStateOfMission() == null ||
                        m.getStateOfMission().equals(filter.getStateOfMission()))
                .collect(Collectors.toList());
    }


    /**
     * Return the list of missions for a given week, filtered according to the user's role.
     * @param user the user requesting the schedule (Manager, Interpreter or Beneficiary)
     * @param weekStart the date of any day within the target week;
     * @throws SQLException if the database could not be reached
     */
    public ArrayList<Mission> getMissionsForWeek(AppliUser user, LocalDate weekStart) throws SQLException {

        DAOMission daoMission = new DAOMission();

        int yearNumber = weekStart.getYear();
        int weekNumber = weekStart.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());

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
