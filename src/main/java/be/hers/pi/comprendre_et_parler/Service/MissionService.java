package be.hers.pi.comprendre_et_parler.Service;

import be.hers.pi.comprendre_et_parler.DAOs.DAOMission;
import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;
import be.hers.pi.comprendre_et_parler.models.Mission;
import be.hers.pi.comprendre_et_parler.models.MissionFilter;
import be.hers.pi.comprendre_et_parler.services.wrappers.SQLWrap;

import java.sql.SQLException;
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
}
