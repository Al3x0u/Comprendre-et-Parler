package be.hers.pi.comprendre_et_parler.models;

public class MissionFilter {
    private Beneficiary beneficiary;
    private Interpreter interpreter;
    private MissionState stateOfMission;

    public MissionFilter() {}

    public Beneficiary getBeneficiary(){
        return beneficiary;
    }

    public Interpreter getInterpreter(){
        return interpreter;
    }

    public void setBeneficiary(Beneficiary beneficiary){
        this.beneficiary = beneficiary;
    }

    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }

    public MissionState getStateOfMission() {
        return stateOfMission;
    }

    public void setStateOfMission(MissionState stateOfMission) {
        this.stateOfMission = stateOfMission;
    }
}
