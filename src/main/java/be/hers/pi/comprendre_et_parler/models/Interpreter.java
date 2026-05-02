package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

public class Interpreter extends AppliUser{
    private int hourQuotaWeek = 0;
    private int hourQuotaYear = 0;
    private String transportMode;
    private Set<AcademicSkill> academicSkills;
    private Set<JobSkill> jobSkills;
    private Set<Beneficiary> assignedBeneficiaries;
    private Set<Mission> missions;
    private Location location;
    private Set<BaseTimeSlot> availability;
    private Set<ExceptionalUnavailability> unavailability;

    /**
     * Constructor of an Interpreter object,
     * beneficiaries and missions are initialized with null with id
     * @param id                 represent the id in database
     * @param login              represent the login
     * @param firstName          represent the firstname of the interpreter
     * @param lastName           represent the lastname of the interpreter
     * @param birthDate          represent the birthdate of the interpreter
     * @param hashedPassword     represent the hashed password of the interpreter
     * @param email              represent the email of the interpreter
     * @param phoneNumber        represent the phone number of the interpreter
     * @param hQW                represent the hour quota per week
     * @param hQY                represent the hour quota per year
     * @param transportMode      represent the transport mode of the interpreter
     * @param academic           represent the set of academic skills of the interpreter
     * @param job                represent the set of job skills of the interpreter
     * @param location           represent the location of the interpreter
     * @param time               represent the set of punctual time slots of the interpreter
     */
    public Interpreter(int id, String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email,
                       String phoneNumber, int hQW, int hQY, String transportMode,
                       Set<AcademicSkill> academic, Set<JobSkill> job, Location location,
                       Set<BaseTimeSlot> time) {
        super(id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        if (hQW >= 0) hourQuotaWeek = hQW;
        if (hQY >= 0) hourQuotaYear = hQY;
        this.transportMode = transportMode;
        jobSkills = job;
        academicSkills = academic;
        assignedBeneficiaries = null;
        missions = null;
        availability = time;
        this.location = location;
        unavailability = null;
    }

    /**
     * Constructor of an Interpreter object without id,
     * beneficiaries and missions are initialized with null
     * @param login              represent the login
     * @param firstName          represent the firstname of the interpreter
     * @param lastName           represent the lastname of the interpreter
     * @param birthDate          represent the birthdate of the interpreter
     * @param hashedPassword     represent the hashed password of the interpreter
     * @param email              represent the email of the interpreter
     * @param phoneNumber        represent the phone number of the interpreter
     * @param hQW                represent the hour quota per week
     * @param hQY                represent the hour quota per year
     * @param transportMode      represent the transport mode of the interpreter
     * @param academic           represent the set of academic skills of the interpreter
     * @param job                represent the set of job skills of the interpreter
     * @param location           represent the location of the interpreter
     * @param time               represent the set of punctual time slots of the interpreter
     */
    public Interpreter(String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email,
                       String phoneNumber, int hQW, int hQY, String transportMode,
                       Set<AcademicSkill> academic, Set<JobSkill> job, Location location,
                       Set<BaseTimeSlot> time) {
        this(-1, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, hQW,
                hQY, transportMode, academic, job, location, time);
    }

    /**
     * Copy constructor. Creates a deep copy of the given Interpreter.
     * Lists are copied as new ArrayList instances, but their elements
     * are shared (shallow copy of elements).
     * The assignedBeneficiaries and missions fields are copied as new
     * ArrayList instances if not null.
     *
     * @param other the Interpreter to copy, must not be null
     * @throws IllegalArgumentException if the copied hour quotas are negative
     */
    public Interpreter(Interpreter other) {
        super(other);
        hourQuotaWeek = other.hourQuotaWeek;
        hourQuotaYear = other.hourQuotaYear;
        transportMode = other.transportMode;
        if(other.academicSkills != null){
            this.academicSkills = new HashSet<>(other.academicSkills);
        }
        if(other.jobSkills != null){
            this.jobSkills = new HashSet<>(other.jobSkills);
        }
        if(other.assignedBeneficiaries != null){
            this.assignedBeneficiaries = new HashSet<>(other.assignedBeneficiaries);
        }
        if(other.missions != null){
            this.missions = new HashSet<>(other.missions);
        }
        if(other.availability != null){
            this.availability = new HashSet<>(other.availability);
        }
        if(other.location != null){
            this.location = new Location(other.location);
        }
        if(other.unavailability != null){
            this.unavailability = new HashSet<>(other.unavailability);
        }
    }

    public Interpreter clone() {
        return new Interpreter(this);
    }

    /**
     * @return this.transportMode
     */
    public String getTransportMode() {
        return transportMode;
    }

    /**
     * @param transportMode the new transport mode
     */
    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    /**
     * @return this.jobskills
     */
    public Set<JobSkill> getJobSkills() {
        return jobSkills;
    }

    /**
     * @return this.missions
     */
    public Set<Mission> getMissions() {
        return missions;
    }

    /**
     * @param missions the new set of missions
     */
    public void setMissions(Set<Mission> missions) {
        this.missions = missions;
    }

    /**
     * @return this.location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the new location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return this.punctualTime
     */
    public Set<BaseTimeSlot> getAvailability() {
        return availability;
    }

    /**
     * @param availability the new timeSlot
     */
    public void setAvailability(Set<BaseTimeSlot> availability) {
        this.availability = availability;
    }

    /**
     * @return this.unavavailability
     */
    public Set<ExceptionalUnavailability> getUnavailability() {
        return this.unavailability;
    }

    /**
     * @param unavailability the new unavailability
     */
    public void setUnavailability(Set<ExceptionalUnavailability> unavailability) {
        this.unavailability = unavailability;
    }

    /**
     @return this.hourQuotaWeek
     */
    public int getHourQuotaWeek() {
        return hourQuotaWeek;
    }

    /**
     * @param newHourQuotaWeek represent the new quota hour
     */
    public void setHourQuotaWeek(int newHourQuotaWeek) {
        if (newHourQuotaWeek >= 0)
            this.hourQuotaWeek = newHourQuotaWeek;
    }

    /**
     @return this.hourQuotaYear
     */
    public int getHourQuotaYear() {
        return this.hourQuotaYear;
    }

    /**
     * @return this.academicSkills
     */
    public Set<AcademicSkill> getAcademicSkills() {
        return academicSkills;
    }

    /**
     * @param academicSkills the new Set of academic Skills
     */
    public void setAcademicSkills(Set<AcademicSkill> academicSkills) {
        this.academicSkills = academicSkills;
    }

    /**
     * @param newHourQuotaYear represent the new quota year
     */
    public void setHourQuotaYear(int newHourQuotaYear) {
        if (newHourQuotaYear >= 0)
            this.hourQuotaYear = newHourQuotaYear;
    }

    /**
     * @param jobSkills the new set of job skills
     */
    public void setJobSkills(Set<JobSkill> jobSkills) {
        this.jobSkills = jobSkills;
    }

    /**
     * @return this.beneficiaries
     */
    public Set<Beneficiary> getAssignedBeneficiaries() {
        return assignedBeneficiaries;
    }

    /**
     * @param assignedBeneficiaries the new Set of beneficiaries
     */
    public void setAssignedBeneficiaries(Set<Beneficiary> assignedBeneficiaries) {
        this.assignedBeneficiaries = assignedBeneficiaries;
    }

    /**
     * Compare this Interpreter with another Interpreter for equality
     * @param other the Interpreter object to compare with
     * @return true if both Interpreter objects have identical hourQuotaWeek, hourQuotaYear, transportMode and AppliUser fields
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Interpreter)) return false;
        Interpreter interpreter = (Interpreter) other;
        return fieldAreEquals(interpreter);
    }

    /**
     * Utility method to check if all the object attributes are equals
     * @param other the other interpreter object to compare if it is equal to this
     * @return true if all the class object attributes are equals
     */
    private boolean fieldAreEquals(Interpreter other) {
        return super.equals(other) &&
                hourQuotaWeek == other.hourQuotaWeek &&
                hourQuotaYear == other.hourQuotaYear &&
                Objects.equals(transportMode, other.transportMode) &&
                Objects.equals(academicSkills, other.academicSkills) &&
                Objects.equals(jobSkills, other.jobSkills) &&
                Objects.equals(location, other.location) &&
                Objects.equals(availability, other.availability) &&
                Objects.equals(unavailability, other.unavailability);
    }

    /**
     * Computes a hash code for this Interpreter based on its attributes.
     * two Interpreter objects that are equal according to equals() will have the same hash code.
     * @return an integer hash code representing this Interpreter
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                hourQuotaWeek,
                hourQuotaYear,
                transportMode,
                academicSkills,
                jobSkills,
                location,
                availability,
                unavailability
        );
    }

    /**
     * Returns a String representation of this Interpreter,
     * including only the fields inherited from AppliUser.
     * @return a formatted String representing this Interpreter
     */
    @Override
    public String toString() {
        return super.toString() + ", hourQuotaWeek="  + hourQuotaWeek +  ", hourQuotaYear="  + hourQuotaYear + ", transportMode=" + transportMode + " is an interpreter.";

    }
}