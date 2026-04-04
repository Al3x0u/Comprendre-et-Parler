package be.hers.pi.comprendre_et_parler.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Mission {
    private int id;
    private String subject;
    private MissionState stateOfMission;
    private String commentary;
    private TimeSlot timeSlot;
    private List<Beneficiary> beneficiaries;
    private List<Interpreter> interpreters;
    private Location location;
    private JobSkill jobSkill;
    private AcademicSkill academicSkill;

    /**
     * Constructor of a Mission object
     * @param id represent the id of the mission
     * @param subject represent the subject of the mission
     * @param stateOfMission represent the state of the mission
     * @param beneficiaries represent the beneficiaries who concern this mission
     * @param interpreters represent the interpreters who work for this mission
     * @param location represent the location of the mission
     * @param jobSkill represent the required business skill
     * @param academicSkill represent the required academic skill
     */
    public Mission(int id, String subject, MissionState stateOfMission, String commentary, TimeSlot timeSlot, List<Beneficiary> beneficiaries, List<Interpreter> interpreters, Location location, JobSkill jobSkill, AcademicSkill academicSkill) {
        this.id = id;
        this.subject = subject;
        this.stateOfMission = stateOfMission;
        this.commentary = commentary;
        this.timeSlot = timeSlot;
        this.beneficiaries = beneficiaries;
        this.interpreters = interpreters;
        this.location = location;
        this.jobSkill = jobSkill;
        this.academicSkill = academicSkill;
    }

    /**
     * Copy constructor of a Mission Object
     * @param mission
     */
    public Mission(Mission mission) {
        this.id = mission.id;
        this.subject = mission.subject;
        this.stateOfMission = mission.stateOfMission;
        this.commentary = mission.commentary;
        this.timeSlot = mission.timeSlot;
        this.beneficiaries = new ArrayList<>(mission.beneficiaries);
        this.interpreters = new ArrayList<>(mission.interpreters);
        this.location = mission.location;
        this.jobSkill = mission.jobSkill;
        this.academicSkill = mission.academicSkill;
    }

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @return this.subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return this.stateOfMission
     */
    public MissionState getStateOfMission() {
        return stateOfMission;
    }

    /**
     * @return this.commentary
     */
    public String getCommentary() {
        return commentary;
    }

    /**
     * @return this.timeSlot
     */
    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /**
     * @return this.beneficiaries
     */
    public List<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    /**
     * @return this.interpreters
     */
    public List<Interpreter> getInterpreters() {
        return interpreters;
    }

    /**
     * @return this.location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @return this.jobSkill
     */
    public JobSkill getJobSkill() {
        return jobSkill;
    }

    /**
     * @return this.academicSkill
     */
    public AcademicSkill getAcademicSkill() {
        return academicSkill;
    }

    /**
     * @param id : mission id
     * @post if id >= 0, id is affected to this.id
     */
    public void setId(int id) {
        if(id >= 0) this.id = id;
    }

    /**
     * @param subject represent the subject of Mission
     */
    public void setSubject(String subject){
        this.subject = subject;
    }

    /**
     * @param state represent the mission state
     */
    public void setStateOfMission(MissionState state){
        this.stateOfMission = state;
    }

    /**
     * @param commentary : represent the mission commentary
     */
    public void setCommentary(String commentary){
        this.commentary = commentary;
    }

    /**
     * @param timeSlot represent the time slot of the mission
     */
    public void setTimeSlot(TimeSlot timeSlot){
        this.timeSlot = timeSlot;
    }

    /**
     * @param beneficiaries represent the beneficiaries of the mission
     */
    public void setBeneficiaries(List<Beneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    /**
     * @param interpreters represent the interpreters of the mission
     */
    public void setInterpreters(List<Interpreter> interpreters) {
        this.interpreters = interpreters;
    }

    /**
     * @param location represent the location of the mission
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @param jobSkill represent the business skill required for the mission
     */
    public void setJobSkill(JobSkill jobSkill) {
        this.jobSkill = jobSkill;
    }

    /**
     * @param academicSkill represent the academic skill required for the mission
     */
    public void setAcademicSkill(AcademicSkill academicSkill) {
        this.academicSkill = academicSkill;
    }

    /**
     * @return a String which contains all information about the mission
     */
    @Override
    public String toString(){
        return "Mission{id=" + id + ", subject=" + subject + ", stateOfMission=" + stateOfMission +
                ", commentary=" + commentary + ", timeSlot=" + timeSlot + ", beneficiaries=" + beneficiaries +
                ", interpreters=" + interpreters + ", location=" + location + ", jobSkill=" + jobSkill +
                ", academicSkill=" + academicSkill + "}";
    }

    /**
     * Compare if two missions are the same
     * @param mission
     * @post mission is unchanged
     * @return true if mission and this are the same, else false
     */
    public boolean equals(Mission mission) {
        if (this == mission) return true;
        if (mission == null) return false;
        return mission.id == this.id
                && mission.subject.equals(this.subject)
                && mission.stateOfMission.equals(this.stateOfMission)
                && mission.commentary.equals(this.commentary)
                && mission.timeSlot.equals(this.timeSlot)
                && mission.beneficiaries.equals(this.beneficiaries)
                && mission.interpreters.equals(this.interpreters)
                && mission.location.equals(this.location)
                && mission.jobSkill.equals(this.jobSkill)
                && mission.academicSkill.equals(this.academicSkill);
    }

    /**
     * @return hashcode of the mission
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, subject, stateOfMission, commentary, timeSlot, beneficiaries, interpreters, location, jobSkill, academicSkill);
    }

    /**
     * Compare 2 missions based on the time slot
     * @param mission
     * @post mission is unchanged
     * @return 0 if this == mission based on time slot,
     *         1 if this > mission based on time slot,
     *         else -1
     */
    public int compareTo(Mission mission) {
        if (this == mission) return 0;
        return this.timeSlot.compareTo(mission.timeSlot); //en supposant que compareTo soit implémenté dans TimeSlot
    }

}