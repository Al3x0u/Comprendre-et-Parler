package be.hers.pi.comprendre_et_parler.domains;

import java.util.List;
import java.util.Locale;

public class Mission {
    private int id;
    private String subjet;
    private MissionState stateOfMission;
    private List<Beneficiary> beneficiaries;
    private List<Interpreter> interpreters;
    private Location location;
    private JobSkill jobSkill;
    private AcademicSkill academicSkill;

    /**
     * Constructor of a Mission object
     *
     * @param id represent the id of the mission
     * @param subjet represent the subject of the mission
     * @param stateOfMission represent the state of the mission
     * @param beneficiaries represent the beneficiaries who concern this mission
     * @param interpreters represent the interpreters who work for this mission
     * @param location represent the location of the mission
     * @param jobSkill represent the required business skill
     * @param academicSkill represent the required academic skill
     */
    public Mission(int id, String subjet, MissionState stateOfMission, List<Beneficiary> beneficiaries, List<Interpreter> interpreters, Location location, JobSkill jobSkill, AcademicSkill academicSkill) {
        this.id = id;
        this.subjet = subjet;
        this.stateOfMission = stateOfMission;
        this.beneficiaries = beneficiaries;
        this.interpreters = interpreters;
        this.location = location;
        this.jobSkill = jobSkill;
        this.academicSkill = academicSkill;
    }

    /**
     * @return this.subject
     */
    public String getSubjet() {
        return subjet;
    }

    /**
     * @param subject represent the subject of Mission
     */
    public void setSubject(String subject){
        this.subjet = subject;
    }

    /**
     * @return this.stateOfMission
     */
    public MissionState getStateOfMission() {
        return stateOfMission;
    }

    /**
     * @param state represent the mission state
     */
    public void setStateOfMission(MissionState state){
        this.stateOfMission = state;
    }

    /**
     * @return a String which contains all information about the mission
     */
    public String toString(){
        return null;
    }

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @param newId represent the new id
     */
    public void setId(int newId){
        this.id = newId;
    }

    /**
     * @return this.beneficiaries
     */
    public List<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    /**
     * @param beneficiaries represent the beneficiaries of the mission
     */
    public void setBeneficiaries(List<Beneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    /**
     * @return this.interpreters
     */
    public List<Interpreter> getInterpreters() {
        return interpreters;
    }

    /**
     * @param interpreters represent the interpreters of the mission
     */
    public void setInterpreters(List<Interpreter> interpreters) {
        this.interpreters = interpreters;
    }

    /**
     * @return this.location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location represent the location of the mission
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return this.jobSkill
     */
    public JobSkill getJobSkill() {
        return jobSkill;
    }

    /**
     * @param jobSkill represent the business skill required for the mission
     */
    public void setJobSkill(JobSkill jobSkill) {
        this.jobSkill = jobSkill;
    }

    /**
     * @return this.academicSkill
     */
    public AcademicSkill getAcademicSkill() {
        return academicSkill;
    }

    /**
     * @param academicSkill represent the academic skill required for the mission
     */
    public void setAcademicSkill(AcademicSkill academicSkill) {
        this.academicSkill = academicSkill;
    }
}