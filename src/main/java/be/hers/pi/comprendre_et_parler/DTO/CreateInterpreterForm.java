package be.hers.pi.comprendre_et_parler.DTO;

import java.time.LocalDate;
import java.util.List;

public class CreateInterpreterForm {
    private String lastName;
    private String firstName;
    private String email;
    private String phoneNumber;
    private String password;
    private LocalDate birthDate;
    private String transportMode;

    private Integer postalCode;
    private String street;
    private String streetNumber;
    private String cityDesignation;
    private String locationDesignation;
    private Integer box;

    private Integer hourQuotaWeek;
    private Integer hourQuotaYear;

    private List<Integer> academicSkillIds;
    private List<Integer> jobSkillIds;

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

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
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

    public String getCityDesignation() {
        return cityDesignation;
    }

    public void setCityDesignation(String city_designation) {
        this.cityDesignation = city_designation;
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

    public List<Integer> getAcademicSkillIds() {
        return academicSkillIds;
    }

    public void setAcademicSkillIds(List<Integer> academicSkillIds) {
        this.academicSkillIds = academicSkillIds;
    }

    public List<Integer> getJobSkillIds() {
        return jobSkillIds;
    }

    public void setJobSkillIds(List<Integer> jobSkillIds) {
        this.jobSkillIds = jobSkillIds;
    }
}
