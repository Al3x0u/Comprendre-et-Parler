package be.hers.pi.comprendre_et_parler.models;

import tools.jackson.core.ObjectReadContext;

import java.time.LocalDate;
import java.util.List;

public class Interpreter extends AppliUser{
    private int id;
    private int hourQuotaWeek;
    private int hourQuotaYear;
    private Transportation transportMode;
    private List<AcademicSkill> academicSkills;
    private List<JobSkill> jobSkills;
    private List<Beneficiary> beneficiaries;
    private List<Mission> missions;
    private Location location;
    private List<PunctualTimeSlot> punctualTime;
    private List<ExceptionalUnavailability> unavailability;


    /**
     * Constructor of an Interpreter object
     * @param id                 represent the id
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
     * @param beneficiaries      represent the list of beneficiaries of the interpreter
     * @param missions           represent the list of missions of the interpreter
     * @param location           represent the location of the interpreter
     * @param time               represent the list of punctual time slots of the interpreter
     * @param unavailability     represent the list of exceptional unavailabilities of the interpreter
     * @throws IllegalArgumentException if hQW or hQY is negative
     */
    public Interpreter(int id, String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email,
                       String phoneNumber, int hQW, int hQY, Transportation transportMode,
                       List<AcademicSkill> academic, List<JobSkill> job, List<Beneficiary> beneficiaries,
                       List<Mission> missions, Location location, List<PunctualTimeSlot> time, List<ExceptionalUnavailability> unavailability) {
        super(id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        if (hQW < 0 || hQY < 0) {
            throw new IllegalArgumentException("Hour quotas cannot be negative");
        }
        this.hourQuotaWeek = hQW;
        this.hourQuotaYear = hQY;
        this.transportMode = transportMode;
        jobSkills = job;
        academicSkills = academic;
        this.beneficiaries = beneficiaries;
        this.missions = missions;
        this.punctualTime = time;
        this.location = location;
        this.unavailability = unavailability;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getHourQuotaYear() {
        return this.hourQuotaYear;
    }

    public Transportation getTransportMode() {
        return this.transportMode;
    }

    public void setTransportMode(Transportation transportMode) {
        this.transportMode = transportMode;
    }

    public List<JobSkill> getJobSkills() {
        return this.jobSkills;
    }

    public List<Mission> getMissions() {
        return this.missions;
    }

    public void setMissions(List<Mission> missions) {
        this.missions = missions;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<PunctualTimeSlot> getPunctualTime() {
        return this.punctualTime;
    }

    public void setPunctualTime(List<PunctualTimeSlot> punctualTime) {
        this.punctualTime = punctualTime;
    }

    public List<ExceptionalUnavailability> getUnavailability() {
        return this.unavailability;
    }

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

    public Transportation getTransportation() {
        return this.transportMode;
    }

    public void setTransportation(Transportation transportation) {
        this.transportMode = transportation;
    }

    public List<AcademicSkill> getAcademicSkills() {
        return this.academicSkills;
    }

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

    public void setJobSkills(List<JobSkill> jobSkills) {
        this.jobSkills = jobSkills;
    }

    public List<Beneficiary> getBeneficiaries() {
        return this.beneficiaries;
    }

    public void setBeneficiaries(List<Beneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    /**
     * Compare this Interpreter with another Interpreter for equality
     * @param other the Interpreter object to compare with
     * @return true if both Interpreter objects have identical hourQuotaWeek, hourQuotaYear, transportMode and AppliUser fields
     */
    public boolean equals(Interpreter other) {
        return (super.equals(other) && hourQuotaWeek == other.hourQuotaWeek &&
                hourQuotaYear == other.hourQuotaYear && transportMode == other.transportMode);
    }

    /**
     * Return a String representation of the Interpreter containing all fields
     * @return formatted string with hour quotas, transport mode and AppliUser fields
     */
    @Override
    public String toString() {
        return null;
    }
}