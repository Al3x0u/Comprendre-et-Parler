package be.hers.pi.comprendre_et_parler.models;

public class MissionFilter {
    private Beneficiary beneficiary;
    private Interpreter interpreter;
    private JobSkill jobSkill;
    private AcademicSkill academicSkill;
    private Location location;
    private Integer minImportance;
    private MissionState stateOfMission;

    public MissionFilter() {}

    public Beneficiary getBeneficiary(){
        return beneficiary;
    }

    public Interpreter getInterpreter(){
        return interpreter;
    }

    public JobSkill getJobSkill(){
        return jobSkill;
    }

    public AcademicSkill getAcademicSkill(){
        return academicSkill;
    }

    public Location getLocation(){
        return location;
    }

    public Integer getMinImportance(){
        return minImportance;
    }

    public void setBeneficiary(Beneficiary beneficiary){
        this.beneficiary = beneficiary;
    }

    public void setInterpreter(Interpreter interpreter){
        this.interpreter = interpreter;
    }

    public void setJobSkill(JobSkill jobSkill){
        this.jobSkill = jobSkill;
    }

    public void setAcademicSkill(AcademicSkill academicSkill){
        this.academicSkill = academicSkill;
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public void setMinImportance(Integer minImportance){
        this.minImportance = minImportance;
    }

    public MissionState getStateOfMission() {
        return stateOfMission;
    }

    public void setStateOfMission(MissionState stateOfMission) {
        this.stateOfMission = stateOfMission;
    }
}
