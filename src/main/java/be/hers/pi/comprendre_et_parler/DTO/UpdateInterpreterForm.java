package be.hers.pi.comprendre_et_parler.DTO;

import be.hers.pi.comprendre_et_parler.models.Interpreter;
import be.hers.pi.comprendre_et_parler.models.Location;

import java.time.LocalDate;

public class UpdateInterpreterForm {
    private int id;
    private String login;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;

    private String transportMode;

    private int cityId;
    private String street;
    private String streetNumber;
    private String locationDesignation;
    private Integer box;

    public UpdateInterpreterForm() {}

    public UpdateInterpreterForm(Interpreter interpreter) {
        id = interpreter.getId();
        login = interpreter.getLogin();
        firstName = interpreter.getFirstName();
        lastName = interpreter.getLastName();
        birthDate = interpreter.getBirthDate();
        email = interpreter.getEmail();
        phoneNumber = interpreter.getPhoneNumber();
        transportMode = interpreter.getTransportMode();
        Location location = interpreter.getLocation();
        cityId = location.getCity().getId();
        street = location.getStreet();
        streetNumber = location.getStreetNumber();
        locationDesignation = location.getDesignation();
        box = location.getBox();
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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
}
