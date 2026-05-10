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
     * Empty constructor of an Interpreter
     */
    public Interpreter() {}

    /**
     * Constructor of an Interpreter,
     * beneficiaries and missions are initialized with null
     * @param id represent the id
     * @param login represent the login
     * @param firstName represent the firstname
     * @param lastName represent the lastname
     * @param birthDate represent the birthdate
     * @param hashedPassword represent the hashed password
     * @param email represent the email
     * @param phoneNumber represent the phone number
     * @param hourQuotaWeek represent the hour quota per week
     * @param hourQuotaYear represent the hour quota per year
     * @param transportMode represent the transport mode
     * @param academicSkills represent the set of academic skills
     * @param jobSkills represent the set of job skills
     * @param location represent the location
     * @param availability represent the set of availabilities
     */
    public Interpreter(int id, String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email,
                       String phoneNumber, int hourQuotaWeek, int hourQuotaYear, String transportMode,
                       Set<AcademicSkill> academicSkills, Set<JobSkill> jobSkills, Location location,
                       Set<BaseTimeSlot> availability) {
        super(id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        if (hourQuotaWeek >= 0) this.hourQuotaWeek = hourQuotaWeek;
        if (hourQuotaYear >= 0) this.hourQuotaYear = hourQuotaYear;
        this.transportMode = transportMode;
        this.jobSkills = jobSkills;
        this.academicSkills = academicSkills;
        assignedBeneficiaries = null;
        missions = null;
        this.availability = availability;
        this.location = location;
        unavailability = null;
    }

    /**
     * Constructor of an Interpreter without id,
     * beneficiaries and missions are initialized with null
     * @param login represent the login
     * @param firstName represent the firstname
     * @param lastName represent the lastname
     * @param birthDate represent the birthdate
     * @param hashedPassword represent the hashed password
     * @param email represent the email
     * @param phoneNumber  represent the phone number
     * @param hourQuotaWeek represent the hour quota per week
     * @param hourQuotaYear represent the hour quota per year
     * @param transportMode represent the transport mode
     * @param academicSkills represent the set of academic skills
     * @param jobSkills represent the set of job skills
     * @param location represent the location
     * @param availability represent the set of availabilities
     */
    public Interpreter(String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email,
                       String phoneNumber, int hourQuotaWeek, int hourQuotaYear, String transportMode,
                       Set<AcademicSkill> academicSkills, Set<JobSkill> jobSkills, Location location,
                       Set<BaseTimeSlot> availability) {
        this(-1, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber,
                hourQuotaWeek, hourQuotaYear, transportMode, academicSkills, jobSkills, location, availability);
    }

    /**
     * Copy constructor of an Interpreter
     * @param other the Interpreter to copy, must not be null
     */
    public Interpreter(Interpreter other) {
        super(other);
        hourQuotaWeek = other.hourQuotaWeek;
        hourQuotaYear = other.hourQuotaYear;
        transportMode = other.transportMode;

        if (other.academicSkills != null)
            this.academicSkills = new HashSet<>(other.academicSkills);

        if (other.jobSkills != null)
            this.jobSkills = new HashSet<>(other.jobSkills);

        if (other.assignedBeneficiaries != null)
            this.assignedBeneficiaries = new HashSet<>(other.assignedBeneficiaries);

        if (other.missions != null)
            this.missions = new HashSet<>(other.missions);

        if (other.availability != null)
            this.availability = new HashSet<>(other.availability);

        if (other.location != null)
            this.location = new Location(other.location);

        if (other.unavailability != null)
            this.unavailability = new HashSet<>(other.unavailability);
    }

    public Interpreter clone() {
        return new Interpreter(this);
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
     * @param newHourQuotaYear represent the new quota year
     */
    public void setHourQuotaYear(int newHourQuotaYear) {
        if (newHourQuotaYear >= 0)
            this.hourQuotaYear = newHourQuotaYear;
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
     * @return this.academicSkills
     */
    public Set<AcademicSkill> getAcademicSkills() {
        return academicSkills;
    }

    /**
     * @param academicSkills the new Set of AcademicSkills
     */
    public void setAcademicSkills(Set<AcademicSkill> academicSkills) {
        this.academicSkills = academicSkills;
    }

    /**
     * @return this.jobSkills
     */
    public Set<JobSkill> getJobSkills() {
        return jobSkills;
    }

    /**
     * @param jobSkills the new set of JobSkills
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
     * @param mission the mission to add to this interpreter's list of missions
     */
    public void addMission(Mission mission) {
        this.missions.add(mission);
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
     * @return this.unavailability
     */
    public Set<ExceptionalUnavailability> getUnavailability() {
        return unavailability;
    }

    /**
     * @param unavailability the new unavailability
     */
    public void setUnavailability(Set<ExceptionalUnavailability> unavailability) {
        this.unavailability = unavailability;
    }

    /**
     * @param unavailability the exceptional unavailability to add to this interpreter's list of unavailabilities
     */
    public void addUnavailability(ExceptionalUnavailability unavailability) {
        this.unavailability.add(unavailability);
    }

    /**
     * Compare this Interpreter with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical first name, last name, birthdate, hashed password, email, phone number,
     * hourQuotaWeek, hourQuotaYear, transportMode and location
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interpreter)) return false;

        Interpreter other = (Interpreter) o;
        return super.equals(other) && Objects.equals(hourQuotaWeek, other.hourQuotaWeek)
                && Objects.equals(hourQuotaYear, other.hourQuotaYear)
                && Objects.equals(transportMode, other.transportMode)
                && Objects.equals(academicSkills, other.academicSkills)
                && Objects.equals(jobSkills, other.jobSkills) && Objects.equals(location, other.location)
                && Objects.equals(availability, other.availability)
                && Objects.equals(unavailability, other.unavailability);
    }

    /**
     * Computes the hash code of this Interpreter
     * two Interpreter objects that are equal according to equals() will have the same hash code
     * @return an integer hash code representing this Interpreter
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hourQuotaWeek, hourQuotaYear, transportMode,
                academicSkills, jobSkills, location, availability, unavailability
        );
    }

    /**
     * Return a String representation of this Interpreter
     * @return formatted string with super, hourQuotaWeek, hourQuotaYear and transportMode
     */
    @Override
    public String toString() {
        return "Interpreter{" + super.toString() + ", hourQuotaWeek = "  + hourQuotaWeek +
                ", hourQuotaYear = "  + hourQuotaYear + ", transportMode = " + transportMode + "}";
    }
}