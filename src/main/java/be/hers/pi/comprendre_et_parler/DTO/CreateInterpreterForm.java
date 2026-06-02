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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getLocationDesignation() {
        return locationDesignation;
    }

    public void setLocationDesignation(String location_designation) {
        this.locationDesignation = location_designation;
    }

    public Integer getBox() {
        return box;
    }

    public void setBox(Integer box) {
        this.box = box;
    }

    public Integer getHourQuotaWeek() {
        return hourQuotaWeek;
    }

    public void setHourQuotaWeek(Integer hourQuotaWeek) {
        this.hourQuotaWeek = hourQuotaWeek;
    }

    public Integer getHourQuotaYear() {
        return hourQuotaYear;
    }

    public void setHourQuotaYear(Integer hourQuotaYear) {
        this.hourQuotaYear = hourQuotaYear;
    }

    public List<AcademicSkill> getAcademicSkillList() {
        return academicSkillList;
    }

    public void setAcademicSkillList(List<AcademicSkill> academicSkillIds) {
        this.academicSkillList = academicSkillIds;
    }

    public List<JobSkill> getJobSkillList() {
        return jobSkillList;
    }

    public void setJobSkillList(List<JobSkill> jobSkillIds) {
        this.jobSkillList = jobSkillIds;
    }
}
