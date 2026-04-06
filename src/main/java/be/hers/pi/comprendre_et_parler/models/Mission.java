package be.hers.pi.comprendre_et_parler.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import be.hers.pi.comprendre_et_parler.DAOs.DAOMission;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

public class Mission {
    private int id=0;
    private String subject;
    private MissionState stateOfMission;
    private String commentary;
    private TimeSlot timeSlot;
    private List<Beneficiary> beneficiaries;
    private List<Interpreter> interpreters;
    private Location location;
    private JobSkill jobSkill;
    private AcademicSkill academicSkill;
    private String room;

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
     * @param room represent the room of the mission (can be null)
     */
    public Mission(int id, String subject, MissionState stateOfMission, String commentary, TimeSlot timeSlot,
                   List<Beneficiary> beneficiaries, List<Interpreter> interpreters, Location location,
                   JobSkill jobSkill, AcademicSkill academicSkill, String room) {
        if(id > 0) this.id = id;
        this.subject = subject;
        this.stateOfMission = stateOfMission;
        this.commentary = commentary;
        this.timeSlot = new TimeSlot(timeSlot);
        this.beneficiaries = new ArrayList<>(beneficiaries);
        this.interpreters = new ArrayList<>(interpreters);
        this.location = new Location(location);
        this.jobSkill = new JobSkill(jobSkill);
        this.academicSkill = new AcademicSkill(academicSkill);
        this.room = room;
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
        this.timeSlot = new TimeSlot(mission.timeSlot);
        this.beneficiaries = new ArrayList<>(mission.beneficiaries);
        this.interpreters = new ArrayList<>(mission.interpreters);
        this.location = new Location(mission.location);
        this.jobSkill = new JobSkill(mission.jobSkill);
        this.academicSkill = new AcademicSkill(mission.academicSkill);
        this.room = mission.room;
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
        return new TimeSlot(timeSlot);
    }

    /**
     * @return this.beneficiaries
     */
    public List<Beneficiary> getBeneficiaries() {
        return new ArrayList<>(beneficiaries);
    }

    /**
     * @return this.interpreters
     */
    public List<Interpreter> getInterpreters() {
        return new ArrayList<>(interpreters);
    }

    /**
     * @return this.location
     */
    public Location getLocation() {
        return new Location(location);
    }

    /**
     * @return this.jobSkill
     */
    public JobSkill getJobSkill() {
        return new JobSkill(jobSkill);
    }

    /**
     * @return this.academicSkill
     */
    public AcademicSkill getAcademicSkill() {
        return new AcademicSkill(academicSkill);
    }

    /**
     * @return this.room (can be null)
     */
    public String getRoom() {
        return room;
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
        this.timeSlot = new TimeSlot(timeSlot);
    }

    /**
     * @param beneficiaries represent the beneficiaries of the mission
     */
    public void setBeneficiaries(List<Beneficiary> beneficiaries) {
        this.beneficiaries = new ArrayList<>(beneficiaries);
    }

    /**
     * @param interpreters represent the interpreters of the mission
     */
    public void setInterpreters(List<Interpreter> interpreters) {
        this.interpreters = new ArrayList<>(interpreters);
    }

    /**
     * @param location represent the location of the mission
     */
    public void setLocation(Location location) {
        this.location = new Location(location);
    }

    /**
     * @param jobSkill represent the business skill required for the mission
     */
    public void setJobSkill(JobSkill jobSkill) {
        this.jobSkill = new JobSkill(jobSkill);
    }

    /**
     * @param academicSkill represent the academic skill required for the mission
     */
    public void setAcademicSkill(AcademicSkill academicSkill) {
        this.academicSkill = new AcademicSkill(academicSkill);
    }

    /**
     * @param room represent the room of the mission (can be null)
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Return a String representation of the Mission containing all fields
     * @return formatted string with id, subjet, state, beneficiaries, interpreters, location, jobSkill, academicSkill and room
     */
    @Override
    public String toString(){
        return "Mission{id=" + id + ", subject=" + subject + ", stateOfMission=" + stateOfMission +
                ", commentary=" + commentary + ", timeSlot=" + timeSlot + ", beneficiaries=" + beneficiaries +
                ", interpreters=" + interpreters + ", location=" + location + ", jobSkill=" + jobSkill +
                ", academicSkill=" + academicSkill + "}";
    }

    /**
     * Compare this Mission with another Mission for equality
     * @param other the Mission object to compare with
     * @return true if both Mission objects have identical id, subjet, stateOfMission, location, jobSkill, academicSkill and room
     */
    public boolean equals(Mission other) {
        if (this == other) return true;
        if (other == null) return false;
        return other.id == this.id
                && other.subject.equals(this.subject)
                && other.stateOfMission.equals(this.stateOfMission)
                && other.commentary.equals(this.commentary)
                && other.timeSlot.equals(this.timeSlot)
                && other.beneficiaries.equals(this.beneficiaries)
                && other.interpreters.equals(this.interpreters)
                && other.location.equals(this.location)
                && other.jobSkill.equals(this.jobSkill)
                && other.academicSkill.equals(this.academicSkill);
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

    /**
     * Add a Beneficiary to the beneficiaries List
     * @param beneficiary represent the Beneficiary to add, not null
     * @param importance represent the importance of the beneficiary in the mission
     * @throws AlreadyExistsException if the beneficiary is already in the list
     * @throws NullPointerException if beneficiary is null
     * @throws SQLException if the database could not be reached
     */
    public void addBeneficiary(Beneficiary beneficiary, int importance) throws AlreadyExistsException, NullPointerException, SQLException {
        if (beneficiary == null)
            throw new NullPointerException("Beneficiary cannot be null");

        if (beneficiaries.contains(beneficiary))
            throw new AlreadyExistsException("Beneficiary already exists in this mission");

        DAOMission daoMission = new DAOMission();
        daoMission.addBeneficiaryToMission(this.getId(), beneficiary.getId(), importance);
        beneficiaries.add(beneficiary);
    }

    /**
     * Remove a Beneficiary from the beneficiaries List by login
     * @param login represent the id of the Beneficiary to remove
     * @throws NoSuchElementException if no beneficiary with the given login exists in the list
     * @throws SQLException if the database could not be reached
     */
    public void deleteBeneficiary(int login) throws NoSuchElementException, SQLException {
        int i = 0;
        boolean found = false;
        while (!found && i < beneficiaries.size()) {
            if (beneficiaries.get(i).getLogin() == login) {
                found = true;
            } else {
                i++;
            }
        }
        if (!found) throw new NoSuchElementException("No beneficiary with login: " + login);

        DAOMission daoMission = new DAOMission();
        daoMission.removeBeneficiaryFromMission(this.getId(), beneficiaries.get(i).getId());
        beneficiaries.remove(i);
    }


    /**
     * Add an Interpreter to the interpreters List
     * @param interpreter represent the Interpreter to add, not null
     * @throws AlreadyExistsException if the interpreter is already in the list
     * @throws NullPointerException if interpreter is null
     * @throws SQLException if the database could not be reached
     */
    public void addInterpreter(Interpreter interpreter) throws AlreadyExistsException, NullPointerException, SQLException {
        if (interpreter == null)
            throw new NullPointerException("Interpreter cannot be null");

        if (interpreters.contains(interpreter))
            throw new AlreadyExistsException("Interpreter already exists in this mission");

        DAOMission daoMission = new DAOMission();
        daoMission.addInterpreterToMission(this.getId(), interpreter.getId());
        interpreters.add(interpreter);
    }

    /**
     * Remove an Interpreter from the interpreters List by login
     * @param login represent the login of the Interpreter to remove
     * @throws NoSuchElementException if no interpreter with the given login exists in the list
     * @throws SQLException if the database could not be reached
     */
    public void deleteInterpreter(int login) throws NoSuchElementException, SQLException {
        int i = 0;
        boolean found = false;
        while (!found && i < interpreters.size()) {
            if (interpreters.get(i).getLogin() == login) {
                found = true;
            } else {
                i++;
            }
        }
        if (!found) throw new NoSuchElementException("No interpreter with login: " + login);

        DAOMission daoMission = new DAOMission();
        daoMission.removeInterpreterFromMission(this.getId(), interpreters.get(i).getId());
        interpreters.remove(i);
    }
}