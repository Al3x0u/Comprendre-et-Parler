package be.hers.pi.comprendre_et_parler.models;

import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

import java.util.List;
import java.util.NoSuchElementException;

public class Mission {
    private int id;
    private String subjet;
    private MissionState stateOfMission;
    private List<Beneficiary> beneficiaries;
    private List<Interpreter> interpreters;
    private Location location;
    private JobSkill jobSkill;
    private AcademicSkill academicSkill;
    private String room;
    private String commentary;

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
     * @param room represent the room of the mission (can be null)
     * @param commentary represent the commentary of the mission (can be null)
     */
    public Mission(int id, String subjet, MissionState stateOfMission, List<Beneficiary> beneficiaries, List<Interpreter> interpreters, Location location, JobSkill jobSkill, AcademicSkill academicSkill, String room, String commentary) {
        this.id = id;
        this.subjet = subjet;
        this.stateOfMission = stateOfMission;
        this.beneficiaries = beneficiaries;
        this.interpreters = interpreters;
        this.location = location;
        this.jobSkill = jobSkill;
        this.academicSkill = academicSkill;
        this.room = room;
        this.commentary = commentary;
    }

    /**
     * @return this.subjet
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
     * @return this.commentary
     */
    public String getCommentary() {
        return commentary;
    }

    /**
     * @param commentary represent the commentary of Mission
     */
    public void setCommentary(String commentary){
        this.commentary = commentary;
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
     * Add a Beneficiary to the beneficiaries List
     * @param beneficiary represent the Beneficiary to add, not null
     * @throws AlreadyExistsException if the beneficiary is already in the list
     * @throws NullPointerException if beneficiary is null
     */
    public void addBeneficiary(Beneficiary beneficiary) throws AlreadyExistsException, NullPointerException {

    }

    /**
     * Remove a Beneficiary from the beneficiaries List by login
     * @param login represent the login of the Beneficiary to remove
     * @throws NoSuchElementException if no beneficiary with the given login exists in the list
     */
    public void deleteBeneficiary(String login) throws NoSuchElementException {

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
     * Add an Interpreter to the interpreters List
     * @param interpreter represent the Interpreter to add, not null
     * @throws AlreadyExistsException if the interpreter is already in the list
     * @throws NullPointerException if interpreter is null
     */
    public void addInterpreter(Interpreter interpreter) throws AlreadyExistsException, NullPointerException {

    }

    /**
     * Remove an Interpreter from the interpreters List by login
     * @param login represent the login of the Interpreter to remove
     * @throws NoSuchElementException if no interpreter with the given login exists in the list
     */
    public void deleteInterpreter(String login) throws NoSuchElementException {

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

    /**
     * @return this.room (can be null)
     */
    public String getRoom() {
        return room;
    }

    /**
     * @param room represent the room of the mission (can be null)
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Compare this Mission with another Mission for equality
     * @param other the Mission object to compare with
     * @return true if both Mission objects have identical id, subjet, stateOfMission, location, jobSkill, academicSkill and room
     */
    public boolean equals(Mission other) {
        return (id == other.id && subjet.equals(other.subjet) && stateOfMission == other.stateOfMission &&
                location == other.location && jobSkill == other.jobSkill &&
                academicSkill == other.academicSkill && room.equals(other.room) && commentary.equals(other.commentary));
    }

    /**
     * Return a String representation of the Mission containing all fields
     * @return formatted string with id, subjet, state, beneficiaries, interpreters, location, jobSkill, academicSkill and room
     */
    public String toString(){
        return null;
    }
}