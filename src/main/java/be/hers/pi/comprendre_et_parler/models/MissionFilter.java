package be.hers.pi.comprendre_et_parler.models;

public class MissionFilter {
    private Beneficiary beneficiary;
    private Interpreter interpreter;
    private MissionState stateOfMission;

    /**
     * Empty constructor of a MissionFilter
     */
    public MissionFilter() {}

    /**
     * @return this.beneficiary
     */
    public Beneficiary getBeneficiary(){
        return beneficiary;
    }

    /**
     * @param beneficiary represents the beneficiary of the mission filter
     */
    public void setBeneficiary(Beneficiary beneficiary){
        this.beneficiary = beneficiary;
    }

    /**
     * @return this.interpreter
     */
    public Interpreter getInterpreter(){
        return interpreter;
    }

    /**
     * @param interpreter represents the interpreter of the mission filter
     */
    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }

    /**
     * @return this.stateOfMission
     */
    public MissionState getStateOfMission() {
        return stateOfMission;
    }

    /**
     * @param stateOfMission represents the stateOfMission of the mission filter
     */
    public void setStateOfMission(MissionState stateOfMission) {
        this.stateOfMission = stateOfMission;
    }
}