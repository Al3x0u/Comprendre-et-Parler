package be.hers.pi.comprendre_et_parler.DTO;

import be.hers.pi.comprendre_et_parler.models.AcademicSkill;
import be.hers.pi.comprendre_et_parler.models.JobSkill;

import java.time.LocalDate;
import java.util.List;

public class CreateInterpreterForm {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private String password;
    private String transportMode;
    private int cityId;
    private String street;
    private String streetNumber;
    private String locationDesignation;
    private Integer box;
    private Integer hourQuotaWeek;
    private Integer hourQuotaYear;
    private List<AcademicSkill> academicSkillList;
    private List<JobSkill> jobSkillList;

    /**
     * @return this.firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return this.lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return this.birthDate
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate the new birthDate
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return this.email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return this.phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return this.password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
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
     * @return this.cityId
     */
    public int getCityId() {
        return cityId;
    }

    /**
     * @param cityId the new city ID
     */
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    /**
     * @return this.street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street the new street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @return this.streetNumber
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * @param streetNumber the new street number
     */
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * @return this.locationDesignation
     */
    public String getLocationDesignation() {
        return locationDesignation;
    }

    /**
     * @param locationDesignation the new location designation
     */
    public void setLocationDesignation(String locationDesignation) {
        this.locationDesignation = locationDesignation;
    }

    /**
     * @return this.box
     */
    public Integer getBox() {
        return box;
    }

    /**
     * @param box the new box
     */
    public void setBox(Integer box) {
        this.box = box;
    }

    /**
     * @return this.hourQuotaWeek
     */
    public Integer getHourQuotaWeek() {
        return hourQuotaWeek;
    }

    /**
     * @param hourQuotaWeek the new weekly hour quota
     */
    public void setHourQuotaWeek(Integer hourQuotaWeek) {
        this.hourQuotaWeek = hourQuotaWeek;
    }

    /**
     * @return this.hourQuotaYear
     */
    public Integer getHourQuotaYear() {
        return hourQuotaYear;
    }

    /**
     * @param hourQuotaYear the new annual hour quota
     */
    public void setHourQuotaYear(Integer hourQuotaYear) {
        this.hourQuotaYear = hourQuotaYear;
    }

    /**
     * @return this.academicSkillList
     */
    public List<AcademicSkill> getAcademicSkillList() {
        return academicSkillList;
    }

    /**
     * @param academicSkillList the new list of academic skill
     */
    public void setAcademicSkillList(List<AcademicSkill> academicSkillList) {
        this.academicSkillList = academicSkillList;
    }

    /**
     * @return this.jobSkillList
     */
    public List<JobSkill> getJobSkillList() {
        return jobSkillList;
    }

    /**
     * @param jobSkillList the new list of job skill
     */
    public void setJobSkillList(List<JobSkill> jobSkillList) {
        this.jobSkillList = jobSkillList;
    }
}