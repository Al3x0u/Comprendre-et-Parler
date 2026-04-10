package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.Objects;

public abstract class AppliUser {
    private int id;
    private String login;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String hashedPassword;
    private String email;
    private String phoneNumber;

    /**
     Constructor of a AppliUser
     @param id represent the id
     @param login represent the login
     @param firstName represent the firstname
     @param lastName represent he lastname
     @param birthDate represent the birthdate
     @param hashedPassword represent the hashed password
     @param email represent the email
     @param phoneNumber represent the phone number
     */
    public AppliUser(int id, String login, String firstName, String lastName, LocalDate birthDate, String hashedPassword, String email, String phoneNumber) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @return this.login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return this.firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return this.lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @return this.birthDate
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * @return this.hashedPassword
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * @return this.email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return this.phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param id represent the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param login represent the new login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @param firstName represent the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @param lastName represent the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @param birthDate represent the new birth date
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @param hashedPassword represent the new hashed password
     */
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    /**
     * @param email represent the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param phoneNumber represent the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Compare this AppliUser with another AppliUser for equality
     * @param other the AppliUser object to compare with
     * @return true if both AppliUser objects have identical login, firstName, lastName, birthDate, hashedPassword, email and phoneNumber
     */
    public boolean equals(AppliUser other) {
        return (login == other.login && firstName == other.firstName && lastName == other.lastName &&
                birthDate == other.birthDate && hashedPassword == other.hashedPassword &&
                email == other.email && phoneNumber == other.phoneNumber);
    }

    /**
     * Return a String representation of the AppliUser containing all fields
     * @return formatted string with login, firstName, lastName, birthDate, hashedPassword, email and phoneNumber
     */
    @Override
    public String toString() {
        return null;
    }
}