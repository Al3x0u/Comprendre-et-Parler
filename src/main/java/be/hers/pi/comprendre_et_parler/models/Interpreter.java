package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Interpreter extends AppliUser{
    private int hourQuotaWeek;
    private int hourQuotaYear;
    private String transportMode;
    private List<AcademicSkill> academicSkills;
    private List<JobSkill> jobSkills;
    private List<Beneficiary> assignedBeneficiaries;
    private List<Mission> missions;
    private Location location;
    private List<PunctualTimeSlot> punctualTime;
    private List<ExceptionalUnavailability> unavailability;


    /**
     * Constructor of an Interpreter object,
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
     * @param academic           represent the list of academic skills of the interpreter
     * @param job                represent the list of job skills of the interpreter
     * @param location           represent the location of the interpreter
     * @param time               represent the list of punctual time slots of the interpreter
     * @param unavailability     represent the list of exceptional unavailabilities of the interpreter
     * @throws IllegalArgumentException if hQW or hQY is negative
     */
    public Interpreter(String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email,
                       String phoneNumber, int hQW, int hQY, String transportMode,
                       List<AcademicSkill> academic, List<JobSkill> job, Location location,
                       List<PunctualTimeSlot> time, List<ExceptionalUnavailability> unavailability) {
        super(login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        if (hQW < 0 || hQY < 0) {
            throw new IllegalArgumentException("Hour quotas cannot be negative");
        }
        hourQuotaWeek = hQW;
        hourQuotaYear = hQY;
        this.transportMode = transportMode;
        jobSkills = job;
        academicSkills = academic;
        assignedBeneficiaries = null;
        missions = null;
        punctualTime = time;
        this.location = location;
        this.unavailability = unavailability;
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
        if (other.hourQuotaWeek < 0 || other.hourQuotaYear < 0) {
            throw new IllegalArgumentException("Hour quotas cannot be negative");
        }
        hourQuotaWeek = other.hourQuotaWeek;
        hourQuotaYear = other.hourQuotaYear;
        transportMode = other.transportMode;
        if(other.academicSkills != null){
            this.academicSkills = new ArrayList<>(other.academicSkills);
        }
        if(other.jobSkills != null){
            this.jobSkills = new ArrayList<>(other.jobSkills);
        }
        if(other.assignedBeneficiaries != null){
            this.assignedBeneficiaries = new ArrayList<>(other.assignedBeneficiaries);
        }
        if(other.missions != null){
            this.missions = new ArrayList<>(other.missions);
        }
        if(other.punctualTime != null){
            this.punctualTime = new ArrayList<>(other.punctualTime);
        }
        this.location = other.location;
        if(other.unavailability != null){
            this.unavailability = new ArrayList<>(other.unavailability);
        }
    }

    /**
     * @return this.hourQuotaYear
     */
    public int getHourQuotaYear() {
        return hourQuotaYear;
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
    public List<JobSkill> getJobSkills() {
        return jobSkills;
    }

    /**
     * @return this.missions
     */
    public List<Mission> getMissions() {
        return missions;
    }

    /**
     * @param missions the new list of missions
     */
    public void setMissions(List<Mission> missions) {
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
    public List<PunctualTimeSlot> getPunctualTime() {
        return punctualTime;
    }

    /**
     * @param punctualTime the new timeSlot
     */
    public void setPunctualTime(List<PunctualTimeSlot> punctualTime) {
        this.punctualTime = punctualTime;
    }

    /**
     * @return this.unavavailability
     */
    public List<ExceptionalUnavailability> getUnavailability() {
        return this.unavailability;
    }

    /**
     * @param unavailability the new unavailability
     */
    public void setUnavailability(List<ExceptionalUnavailability> unavailability) {
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
     * @throws IllegalArgumentException if newHourQuotaWeek is negative
     */
    public void setHourQuotaWeek(int newHourQuotaWeek){
        if (newHourQuotaWeek < 0) {
            throw new IllegalArgumentException("Hour quota week cannot be negative");
        }
        this.hourQuotaWeek = newHourQuotaWeek;
    }

    /**
     @return this.hourQuotaYear
     */
    public int getHourQuotayear() {
        return this.hourQuotaYear;
    }

    public void setHourQuotayear(final int hourQuotayear) {
        this.hourQuotaYear = hourQuotayear;
    }

    /**
     * @return this.academicSkills
     */
    public List<AcademicSkill> getAcademicSkills() {
        return academicSkills;
    }

    /**
     * @param academicSkills the new list of academic Skills
     */
    public void setAcademicSkills(List<AcademicSkill> academicSkills) {
        this.academicSkills = academicSkills;
    }

    /**
     * @param newHourQuotaYear represent the new quota year
     * @throws IllegalArgumentException if newHourQuotaYear is negative
     */
    public void setHourQuotaYear(int newHourQuotaYear){
        if (newHourQuotaYear < 0) {
            throw new IllegalArgumentException("Hour quota year cannot be negative");
        }
        this.hourQuotaYear = newHourQuotaYear;
    }

    /**
     * @param jobSkills the new list of job skills
     */
    public void setJobSkills(List<JobSkill> jobSkills) {
        this.jobSkills = jobSkills;
    }

    /**
     * @return this.beneficiaries
     */
    public List<Beneficiary> getAssignedBeneficiaries() {
        return assignedBeneficiaries;
    }

    /**
     * @param assignedBeneficiaries the new list of beneficiaries
     */
    public void setAssignedBeneficiaries(List<Beneficiary> assignedBeneficiaries) {
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
                Objects.equals(punctualTime, other.punctualTime) &&
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
                punctualTime,
                unavailability
        );
    }

    /**
     * Return a String representation of the Interpreter containing all fields
     * @return formatted string with hour quotas, transport mode and AppliUser fields
     */
    @Override
    public String toString() {
        return super.toString();

    }
}