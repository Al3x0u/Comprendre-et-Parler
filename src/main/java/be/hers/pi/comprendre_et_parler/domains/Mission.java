package be.hers.pi.comprendre_et_parler.domains;

import java.util.List;

public class Mission {
    private String subjet;
    private MissionState stateOfMission;

    /**
     * Constructor of a Mission object
     *
     * @param subjet    represent the subject of the mission
     * @param stateOfMission  represent the state of the mission
     */
    public Mission(String subjet, MissionState stateOfMission) {
        this.subjet = subjet;
        this.stateOfMission = stateOfMission;
    }

    /**
     * @return this.subject
     */
    public String getSubjet() {
        return subjet;
    }

    /**
     * @return this.stateOfMission
     */
    public MissionState getStateOfMission() {
        return stateOfMission;
    }
}
