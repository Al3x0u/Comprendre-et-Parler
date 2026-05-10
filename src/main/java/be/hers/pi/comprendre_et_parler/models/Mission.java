package be.hers.pi.comprendre_et_parler.models;

import java.util.*;
import be.hers.pi.comprendre_et_parler.exceptions.AlreadyExistsException;

public class Mission {
    private int id = -1;
    private String subject;
    private MissionState stateOfMission;
    private String commentary;
    private TimeSlot timeSlot;
    private Beneficiary beneficiary;
    private HashSet<Interpreter> interpreters;
    private Location location;
    private JobSkill jobSkill;
    private AcademicSkill academicSkill;
    private String room;
    private int importance = 0;

    /**
     * Constructor of a Mission object without beneficiary
     * @param id represent the id of the mission
     * @param subject represent the subject of the mission
     * @param stateOfMission represent the state of the mission
     * @param commentary represent the commentary of the mission
     * @param timeSlot represent the time slot of the mission
     * @param location represent the location of the mission
     * @param interpreters represent the interpreters of the mission
     * @param jobSkill represent the required business skill
     * @param academicSkill represent the required academic skill
     * @param room represent the room of the mission (can be null)
     * @param importance represent the importance of the mission
     */
    public Mission(int id, String subject, MissionState stateOfMission, String commentary, TimeSlot timeSlot,
                   Location location, HashSet<Interpreter> interpreters, JobSkill jobSkill, AcademicSkill academicSkill,
                   String room, int importance) {
        if (id > 0) this.id = id;
        this.subject = subject;
        this.stateOfMission = stateOfMission;
        this.commentary = commentary;
        this.timeSlot = timeSlot;
        this.beneficiary = null;
        this.interpreters = interpreters;
        this.location = location;
        this.jobSkill = jobSkill;
        this.academicSkill = academicSkill;
        this.room = room;
        if (importance >= 0 && importance <= 3) this.importance = importance;
    }

    /**
     * Constructor of a Mission object without id and without beneficiary
     * @param subject represent the subject of the mission
     * @param stateOfMission represent the state of the mission
     * @param commentary represent the commentary of the mission
     * @param timeSlot represent the time slot of the mission
     * @param location represent the location of the mission
     * @param interpreters represent the interpreters of the mission
     * @param jobSkill represent the required business skill
     * @param academicSkill represent the required academic skill
     * @param room represent the room of the mission (can be null)
     * @param importance represent the importance of the mission
     */
    public Mission(String subject, MissionState stateOfMission, String commentary, TimeSlot timeSlot,
                   Location location, HashSet<Interpreter> interpreters, JobSkill jobSkill, AcademicSkill academicSkill,
                   String room, int importance) {
        this(-1, subject, stateOfMission, commentary, timeSlot, location, interpreters, jobSkill, academicSkill, room, importance);
    }

    /**
     * Constructor of a Mission object with beneficiary and no interpreters
     * @param id represent the id of the mission
     * @param subject represent the subject of the mission
     * @param stateOfMission represent the state of the mission
     * @param commentary represent the commentary of the mission
     * @param timeSlot represent the time slot of the mission
     * @param beneficiary represent the beneficiary who concern this mission
     * @param location represent the location of the mission
     * @param jobSkill represent the required business skill
     * @param academicSkill represent the required academic skill
     * @param room represent the room of the mission (can be null)
     */
    public Mission(int id, String subject, MissionState stateOfMission, String commentary, TimeSlot timeSlot,
                   Beneficiary beneficiary, Location location,
                   JobSkill jobSkill, AcademicSkill academicSkill, String room, int importance) {
        if (id > 0) this.id = id;
        this.subject = subject;
        this.stateOfMission = stateOfMission;
        this.commentary = commentary;
        this.timeSlot = timeSlot;
        this.beneficiary = beneficiary;
        this.interpreters = null;
        this.location = location;
        this.jobSkill = jobSkill;
        this.academicSkill = academicSkill;
        this.room = room;
        if (importance >= 0 && importance <= 3) this.importance = importance;
    }

    /**
     * Constructor of a Mission object without id and without interpreters
     * @param subject represent the subject of the mission
     * @param stateOfMission represent the state of the mission
     * @param commentary represent the commentary of the mission
     * @param timeSlot represent the time slot of the mission
     * @param beneficiary represent the beneficiary who concern this mission
     * @param location represent the location of the mission
     * @param jobSkill represent the required business skill
     * @param academicSkill represent the required academic skill
     * @param room represent the room of the mission (can be null)
     */
    public Mission(String subject, MissionState stateOfMission, String commentary, TimeSlot timeSlot,
                   Beneficiary beneficiary, Location location,
                   JobSkill jobSkill, AcademicSkill academicSkill, String room, int importance) {
        this(-1, subject, stateOfMission, commentary, timeSlot, beneficiary, location, jobSkill, academicSkill, room, importance);
    }

    /**
     * Copy constructor of a Mission Object
     * @param mission the Mission object to copy
     */
    public Mission(Mission mission) {
        this.id = mission.id;
        this.subject = mission.subject;
        this.stateOfMission = mission.stateOfMission;
        this.commentary = mission.commentary;
        this.timeSlot = mission.timeSlot.clone();

        if (mission.beneficiary != null) this.beneficiary = new Beneficiary(mission.beneficiary);
        if (mission.interpreters != null) this.interpreters = new HashSet<>(mission.interpreters);

        this.location = new Location(mission.location);
        this.jobSkill = new JobSkill(mission.jobSkill);
        this.academicSkill = new AcademicSkill(mission.academicSkill);
        this.room = mission.room;
        this.importance = mission.importance;
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
     * @return this.beneficiary
     */
    public Beneficiary getBeneficiary() {
        return beneficiary;
    }

    /**
     * @return this.interpreters
     */
    public HashSet<Interpreter> getInterpreters() {
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
     * @return this.room (can be null)
     */
    public String getRoom() {
        return room;
    }

    /**
     * @return this.importance
     */
    public int getImportance() {
        return importance;
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
     * @param beneficiary represent the beneficiary of the mission
     */
    public void setBeneficiary(Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }

    /**
     * @param interpreters represent the interpreters of the mission
     * @throws AlreadyExistsException if two interpreters have the same id or are equal
     */
    public void setInterpreters(Set<Interpreter> interpreters) throws AlreadyExistsException {
        List<Interpreter> list = new ArrayList<>(interpreters);
        for (int i = 0; i < list.size(); i++)
            for (int j = i + 1; j < list.size(); j++)
                if (list.get(i).getId() == list.get(j).getId())
                    throw new AlreadyExistsException("Two interpreters have the same id");
        this.interpreters = new HashSet<>(interpreters);
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
     * @param room represent the room of the mission (can be null)
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * @param importance represent the importance of the mission
     * @post if 0 <= importance <= 3, importance is affected to this.importance
     */
    public void setImportance(int importance) {
        if (importance >= 0 && importance <= 3) this.importance = importance;
    }

    /**
     * Return a String representation of the Mission containing all fields
     * @return formatted string with id, subjet, stateOfMission, commentary, timeSlot, beneficiary, interpreters,
     * location, jobSkill, academicSkill, room and importance
     */
    @Override
    public String toString(){
        return "Mission{id=" + id + ", subject=" + subject + ", stateOfMission=" + stateOfMission +
                ", commentary=" + commentary + ", timeSlot=" + timeSlot + ", beneficiary=" + beneficiary +
                ", interpreters=" + interpreters + ", location=" + location + ", jobSkill=" + jobSkill +
                ", academicSkill=" + academicSkill + ", room=" + room + ", importance=" + importance + "}";
    }

    /**
     * Compare this Mission with another Mission for equality
     * @param o the Mission object to compare with
     * @return true if both Mission objects have identical subject, stateOfMission,
     * commentary, timeSlot, beneficiary, interpreters, location, jobSkill,
     * academicSkill, room and importance (id is not compared), else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mission)) return false;

        Mission other = (Mission) o;
        return subject.equals(other.subject)
                && stateOfMission.equals(other.stateOfMission)
                && (Objects.equals(commentary, other.commentary))
                && timeSlot.equals(other.timeSlot)
                && (Objects.equals(beneficiary, other.beneficiary))
                && (Objects.equals(interpreters, other.interpreters))
                && location.equals(other.location)
                && (Objects.equals(jobSkill, other.jobSkill))
                && (Objects.equals(academicSkill, other.academicSkill))
                && (Objects.equals(room, other.room))
                && importance == other.importance;
    }

    /**
     * Computes the hash code of this Mission.
     * @return an integer hash code value based on subject, stateOfMission,
     * commentary, timeSlot, beneficiary, interpreters, location, jobSkill,
     * academicSkill, room and importance (id is not taken into account)
     */
    @Override public int hashCode() {
        return Objects.hash(subject, stateOfMission, commentary, timeSlot, beneficiary, interpreters, location,
                jobSkill, academicSkill, room, importance
        );
    }

    /**
     * Add an Interpreter to the interpreters Set
     * @param interpreter represent the Interpreter to add, not null
     * @throws AlreadyExistsException if an interpreter with the same id already exists in the set
     * @throws NullPointerException if interpreter is null
     */
    public void addInterpreter(Interpreter interpreter) throws AlreadyExistsException, NullPointerException {
        if (interpreter == null)
            throw new NullPointerException("Interpreter cannot be null");

        if (interpreters == null)
            interpreters = new HashSet<>();

        for (Interpreter i : interpreters)
            if (i.getId() == interpreter.getId())
                throw new AlreadyExistsException("Interpreter already exists in this mission");

        interpreters.add(interpreter);
    }

    /**
     * Remove an Interpreter from the interpreters Set by id
     * @param id represent the id of the Interpreter to remove
     * @throws NoSuchElementException if no interpreter with the given id exists in the set
     */
    public void deleteInterpreter(int id) throws NoSuchElementException {
        if (interpreters == null)
            return;

        Interpreter toRemove = null;
        boolean found = false;
        Iterator<Interpreter> it = interpreters.iterator();
        while (!found && it.hasNext()) {
            Interpreter i = it.next();
            if (i.getId() == id) {
                toRemove = i;
                found = true;
            }
        }
        if (!found) throw new NoSuchElementException("No interpreter with id: " + id);
        interpreters.remove(toRemove);
    }
}