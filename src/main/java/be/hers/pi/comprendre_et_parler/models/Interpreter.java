package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.List;

public class Interpreter extends AppliUser{
    private int hourQuotaWeek;
    private int hourQuotaYear;
    private String transportMode;
    private Transportation transportation;
    private List<AcademicSkill> academicSkills;
    private List<JobSkill> jobSkills;
    private List<Beneficiary> beneficiaries;
    private List<Mission> missions;
    private Location location;
    private List<PunctualTimeSlot> punctualTime;
    private List<ExceptionalUnavailability> unavailability;

    /**
     * Constructor of an Interpreter object
     * @param hQW             represent the hour quota per week
     * @param hQY             represent the hour quota per year
     * @param id              represent the id
     * @param login           represent the login
     * @param firstName       represent the firstname of the interpreter
     * @param lastName        represent the lastname of the interpreter
     * @param birthDate       represent the birthdate of the interpreter
     * @param hashedPassword  represent the hashed password of the interpreter
     * @param email           represent the email of the interpreter
     * @param phoneNumber     represent the phone number of the interpreter
     * @param transportMode   represent the transport mode of the interpreter
     * @param academicSkills  represent the academic skills of the interpreter
     * @param jobSkills       represent the job skills of the interpreter
     * @param beneficiaries   represent the beneficiaries linked to the interpreter
     * @param missions        represent the missions assigned to the interpreter
     * @param location        represent the location of the interpreter
     * @param punctualTime    represent the punctual availability time slots
     * @param unavailability  represent the exceptional unavailability periods
     * @param transportation  represent the transportation of the interpreter
     * @throws IllegalArgumentException if hQW or hQY is negative
     */
    public Interpreter(int hQW, int hQY, int id, String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email, String phoneNumber, String transportMode, Transportation transportation, List<AcademicSkill> academicSkills, List<JobSkill> jobSkills, List<Beneficiary> beneficiaries, List<Mission> missions, Location location, List<PunctualTimeSlot> punctualTime, List<ExceptionalUnavailability> unavailability) {
        super(id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        this.transportation = transportation;
        this.academicSkills = academicSkills;
        this.jobSkills = jobSkills;
        this.beneficiaries = beneficiaries;
        this.missions = missions;
        this.location = location;
        this.punctualTime = punctualTime;
        this.unavailability = unavailability;
        if (hQW < 0 || hQY < 0) {
            throw new IllegalArgumentException("Hour quotas cannot be negative");
        }
        this.hourQuotaWeek = hQW;
        this.hourQuotaYear = hQY;
        this.transportMode = transportMode;
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
    public int getHourQuotaYear() {
        return hourQuotaYear;
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
     *
     * @return this.transport
     */
    public String getTransportMode() {
        return transportMode;
    }

    /**
     *
     * @param transportMode represent the new transport mode
     */
    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    /**
     * @return this.academicSkills
     */
    public List<AcademicSkill> getAcademicSkills() {
        return academicSkills;
    }

    /**
     * @param academicSkills represent the new academic skills list
     */
    public void setAcademicSkills(List<AcademicSkill> academicSkills) {
        this.academicSkills = academicSkills;
    }

    /**
     * @return this.jobSkills
     */
    public List<JobSkill> getJobSkills() {
        return jobSkills;
    }

    /**
     * @param jobSkills represent the new job skills list
     */
    public void setJobSkills(List<JobSkill> jobSkills) {
        this.jobSkills = jobSkills;
    }

    /**
     * @return this.beneficiaries
     */
    public List<Beneficiary> getBeneficiaries() {
        return beneficiaries;
    }

    /**
     * @param beneficiaries represent the new beneficiaries list
     */
    public void setBeneficiaries(List<Beneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }

    /**
     * @return this.missions
     */
    public List<Mission> getMissions() {
        return missions;
    }

    /**
     * @param missions represent the new missions list
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
     * @param location represent the new location
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
     * @param punctualTime represent the new punctual time slots list
     */
    public void setPunctualTime(List<PunctualTimeSlot> punctualTime) {
        this.punctualTime = punctualTime;
    }

    /**
     * @return this.unavailability
     */
    public List<ExceptionalUnavailability> getUnavailability() {
        return unavailability;
    }

    /**
     * @param unavailability represent the new unavailability list
     */
    public void setUnavailability(List<ExceptionalUnavailability> unavailability) {
        this.unavailability = unavailability;
    }

    /**
     * @return this.transportation
     */
    public Transportation getTransportation() {
        return transportation;
    }

    /**
     * @param transportation represent the new transportation
     */
    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
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