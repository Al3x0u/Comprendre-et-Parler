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

    /**
     * Empty constructor of an UpdateInterpreterForm
     */
    public UpdateInterpreterForm() {}

    /**
     * Constructor of a UpdateInterpreterForm
     * @param interpreter the interpreter from which it takes information
     */
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

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return this.login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the new login
     */
    public void setLogin(String login) {
        this.login = login;
    }

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
     * @param birthDate the new birthdate
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
}