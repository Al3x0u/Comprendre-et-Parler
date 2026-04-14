package be.hers.pi.comprendre_et_parler.models;

import java.sql.SQLException;
import java.util.*;

import be.hers.pi.comprendre_et_parler.DAOs.DAOMission;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

public class Mission {
    private int id=0;
    private String subject;
    private MissionState stateOfMission;
    private String commentary;
    private TimeSlot timeSlot;
    private Map<Beneficiary, Integer> beneficiaries;
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
     * @param commentary represent the commentary of the mission
     * @param timeSlot represent the time slot of the mission
     * @param location represent the location of the mission
     * @param jobSkill represent the required business skill
     * @param academicSkill represent the required academic skill
     * @param room represent the room of the mission (can be null)
     */
    public Mission(int id, String subject, MissionState stateOfMission, String commentary, TimeSlot timeSlot,
                   Location location, JobSkill jobSkill, AcademicSkill academicSkill, String room) {
        this.id = id;
        this.subject = subject;
        this.stateOfMission = stateOfMission;
        this.commentary = commentary;
        this.timeSlot = timeSlot.clone();
        this.beneficiaries = null;
        this.interpreters = null;
        this.location = new Location(location);
        this.jobSkill = new JobSkill(jobSkill);
        this.academicSkill = new AcademicSkill(academicSkill);
        this.room = room;
    }

    /**
     * Constructor of a Mission object with lists
     * @param id represent the id of the mission
     * @param subject represent the subject of the mission
     * @param stateOfMission represent the state of the mission
     * @param commentary represent the commentary of the mission
     * @param timeSlot represent the time slot of the mission
     * @param beneficiaries represent the beneficiaries who concern this mission
     * @param interpreters represent the interpreters who work for this mission
     * @param location represent the location of the mission
     * @param jobSkill represent the required business skill
     * @param academicSkill represent the required academic skill
     * @param room represent the room of the mission (can be null)
     */
    public Mission(int id, String subject, MissionState stateOfMission, String commentary, TimeSlot timeSlot,
                   Map<Beneficiary, Integer> beneficiaries, List<Interpreter> interpreters, Location location,
                   JobSkill jobSkill, AcademicSkill academicSkill, String room) {
        if(id > 0) this.id = id;
        this.subject = subject;
        this.stateOfMission = stateOfMission;
        this.commentary = commentary;
        this.timeSlot = timeSlot.clone();
        this.beneficiaries = new HashMap<>(beneficiaries);
        this.interpreters = interpreters.stream()
                                        .distinct()
                                        .toList();
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
        this.timeSlot = mission.timeSlot.clone();
        this.beneficiaries = new HashMap<>(mission.beneficiaries);
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
     * @return a copy of this.timeSlot
     */
    public TimeSlot getTimeSlot() {
        return timeSlot.copy();
    }

    /**
     * @return a copy of this.beneficiaries
     */
    public Map<Beneficiary, Integer> getBeneficiaries() {
        return new HashMap<>(beneficiaries);
    }

    /**
     * @return a copy this.interpreters
     */
    public List<Interpreter> getInterpreters() {
        return new ArrayList<>(interpreters);
    }

    /**
     * @return a copy of this.location
     */
    public Location getLocation() {
        return new Location(location);
    }

    /**
     * @return a copy of this.jobSkill
     */
    public JobSkill getJobSkill() {
        return new JobSkill(jobSkill);
    }

    /**
     * @return a copy of this.academicSkill
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
        this.timeSlot = timeSlot.copy();
    }

    /**
     * @param beneficiaries represent the beneficiaries and their importance
     * @throws AlreadyExistsException if two beneficiaries have the same id or are equal
     */
    public void setBeneficiaries(Map<Beneficiary, Integer> beneficiaries) throws AlreadyExistsException {
        for (Beneficiary b1 : beneficiaries.keySet()) {
            for (Beneficiary b2 : beneficiaries.keySet()) {
                if (b1 != b2 && (b1.getId() == b2.getId() || b1.equals(b2)))
                    throw new AlreadyExistsException("Two beneficiaries have the same id or are equal");
            }
        }
        this.beneficiaries = new HashMap<>(beneficiaries);
    }

    /**
     * @param interpreters represent the interpreters of the mission
     * @throws AlreadyExistsException if two interpreters have the same id or are equal
     */
    public void setInterpreters(List<Interpreter> interpreters) throws AlreadyExistsException {
        for (int i = 0; i < interpreters.size(); i++) {
            for (int j = i + 1; j < interpreters.size(); j++) {
                if (interpreters.get(i).getId() == interpreters.get(j).getId() || interpreters.get(i).equals(interpreters.get(j)))
                    throw new AlreadyExistsException("Two interpreters have the same id or are equal");
            }
        }
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
     * @return formatted string with id, subjet, stateOfMission, commentary, timeSlot, beneficiaries, interpreters, location, jobSkill, academicSkill and room
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
     * @param o the Mission object to compare with
     * @return true if both Mission objects have identical subject, stateOfMission,
     * commentary, timeSlot, beneficiaries, interpreters, location, jobSkill,
     * academicSkill and room (id is not compared), else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mission)) return false;

        Mission other = (Mission) o;
        return Objects.equals(subject, other.subject)
                && Objects.equals(stateOfMission, other.stateOfMission)
                && Objects.equals(commentary, other.commentary)
                && Objects.equals(timeSlot, other.timeSlot)
                && Objects.equals(beneficiaries, other.beneficiaries)
                && Objects.equals(interpreters, other.interpreters)
                && Objects.equals(location, other.location)
                && Objects.equals(jobSkill, other.jobSkill)
                && Objects.equals(academicSkill, other.academicSkill)
                && Objects.equals(room, other.room);
    }

    /**
     * Computes the hash code of this Mission.
     * @return an integer hash code value based on subject, stateOfMission,
     * commentary, timeSlot, beneficiaries, interpreters, location, jobSkill,
     * academicSkill and room (id is not taken into account)
     */
    @Override public int hashCode() {
        return Objects.hash(subject, stateOfMission, commentary, timeSlot, beneficiaries, interpreters, location,
                jobSkill, academicSkill, room
        );
    }

    /**
     * Add a Beneficiary to the beneficiaries List
     * @param beneficiary represent the Beneficiary to add, not null
     * @param importance represent the importance of the beneficiary in the mission
     * @throws AlreadyExistsException if the beneficiary is already in the list
     * @throws NullPointerException if beneficiary is null
     * @throws SQLException if the database could not be reached
     */
    public void addBeneficiary(Beneficiary beneficiary, int importance) throws AlreadyExistsException, NullPointerException {
        if (beneficiary == null)
            throw new NullPointerException("Beneficiary cannot be null");

        if (beneficiaries == null)
            beneficiaries = new HashMap<>();

        for (Beneficiary b : beneficiaries.keySet()) {
            if (b.equals(beneficiary)) throw new AlreadyExistsException("Beneficiary already exists in this mission");
        }
        beneficiaries.put(beneficiary, importance);
    }

    /**
     * Remove a Beneficiary from the beneficiaries List by login
     * @param id represent the id of the Beneficiary to remove
     * @throws NoSuchElementException if no beneficiary with the given login exists in the list
     * @throws SQLException if the database could not be reached
     */
    public void deleteBeneficiary(int id) throws NoSuchElementException, SQLException {
        if (beneficiaries == null)
            return;

        Beneficiary toRemove = null;
        boolean found = false;
        List<Beneficiary> keys = new ArrayList<>(beneficiaries.keySet());
        int i = 0;
        while (!found && i < keys.size()) {
            if (keys.get(i).getId() == id) {
                toRemove = keys.get(i);
                found = true;
            } else {
                i++;
            }
        }
        if (!found) throw new NoSuchElementException("No beneficiary with id: " + id);
        beneficiaries.remove(toRemove);
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

        if (interpreters == null)
            interpreters = new ArrayList<>();

        for (Interpreter i : interpreters) {
            if (i.equals(interpreter)) throw new AlreadyExistsException("Interpreter already exists in this mission");
        }
        interpreters.add(interpreter);
    }

    /**
     * Remove an Interpreter from the interpreters List by id
     * @param id represent the id of the Interpreter to remove
     * @throws NoSuchElementException if no interpreter with the given id exists in the list
     * @throws SQLException if the database could not be reached
     */
    public void deleteInterpreter(int id) throws NoSuchElementException, SQLException {
        if (interpreters == null)
            return;

        int i = 0;
        boolean found = false;
        while (!found && i < interpreters.size()) {
            if (interpreters.get(i).getId() == id) {
                found = true;
            } else {
                i++;
            }
        }
        if (!found) throw new NoSuchElementException("No interpreter with id: " + id);
        interpreters.remove(i);
    }
}