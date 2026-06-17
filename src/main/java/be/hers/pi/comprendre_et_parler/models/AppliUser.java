package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.Objects;

public abstract class AppliUser {
    protected int id = -1;
    protected String login;
    protected String firstName;
    protected String lastName;
    protected LocalDate birthDate;
    protected String hashedPassword;
    protected String email;
    protected String phoneNumber;
    protected boolean passwordUpdated;

    /**
     * Minimal constructor
     * @param id represents the id
     */
    public AppliUser(int id) {
        this.id = id;
    }

    /**
     * Lightweight constructor
     * @param id represents the id
     * @param firstName represents the firstName
     * @param lastName the user's last lastName
     */
    public AppliUser(int id, String firstName, String lastName) {
        if (id >= 0) this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Constructor of a AppliUser
     * @param id represents the id
     * @param login represents the login
     * @param firstName represents the firstname
     * @param lastName represents he lastname
     * @param birthDate represents the birthdate
     * @param hashedPassword represents the hashed password
     * @param email represents the email
     * @param phoneNumber represents the phone number
     */
    public AppliUser(int id, String login, String firstName, String lastName, LocalDate birthDate,
                     String hashedPassword, String email, String phoneNumber) {
        if (id >= 0) this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.phoneNumber = phoneNumber;
        passwordUpdated = false;
    }

    /**
     * Constructor of a AppliUser without id
     * @param login represents the login
     * @param firstName represents the firstname
     * @param lastName represents he lastname
     * @param birthDate represents the birthdate
     * @param hashedPassword represents the hashed password
     * @param email represents the email
     * @param phoneNumber represents the phone number
     */
    public AppliUser(String login, String firstName, String lastName, LocalDate birthDate,
                     String hashedPassword, String email, String phoneNumber) {
        this(-1, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
    }

    /**
     * Copy constructor of a AppliUser
     * @param other the AppliUser to copy, must not be null
     */
    public AppliUser(AppliUser other) {
        this(other.id, other.login, other.firstName, other.lastName, other.birthDate,
                other.hashedPassword, other.email, other.phoneNumber);
    }

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id represents the new id
     */
    public void setId(int id) {
        if (id >= 0) this.id = id;
    }

    /**
     * @return this.login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login represents the new login
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
     * @param firstName represents the new first name
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
     * @return The AppliUser's first then last name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * @param lastName represents the new last name
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
     * @param birthDate represents the new birthdate
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return this.hashedPassword
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * @param hashedPassword represents the new hashed password
     */
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    /**
     * @return this.email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email represents the new email
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
     * @param phoneNumber represents the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return true if password has been updated otherwise false
     */
    public boolean isPasswordUpdated() {
        return passwordUpdated;
    }

    /**
     * @param passwordUpdated represents the new phone number
     */
    public void setPasswordUpdated(boolean passwordUpdated) {
        this.passwordUpdated = passwordUpdated;
    }

    @Override
    public abstract AppliUser clone();

    /**
     * Compare this AppliUser with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical login, firstName, lastName,
     * birthDate, email and phoneNumber
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppliUser)) return false;

        AppliUser other = (AppliUser) o;
        return Objects.equals(firstName, other.firstName)  && Objects.equals(lastName, other.lastName)
                && Objects.equals(birthDate, other.birthDate) && Objects.equals(email, other.email)
                && Objects.equals(phoneNumber, other.phoneNumber);
    }

    /**
     * Compares two AppliUser lexicographically according to their designations
     * @param a The second user to compare to this
     * @return The result is a negative integer if this AppliUser.firstName lexicographically precedes the other AppliUser.firstName.
     * The result is a positive integer if this AppliUser.firstName lexicographically follows the other AppliUser.firstName.
     * If the firstName are the same, compare lastName.
     * If the lastName are the same, compare login.
     * If the login are the same, return 0.
     */
    public int compareTo(AppliUser a) {
        if (this == a) return 0;

        int res = firstName.compareTo(a.firstName);
        if (res == 0) {
            res = lastName.compareTo(a.lastName);
            if (res == 0)
                res = login.compareTo(a.login);
        }
        return res;
    }

    /**
     * Computes the hash code of this AppliUser
     * two AppliUser objects that are equal according to equals() will have the same hash code.
     * @return an integer hash code representing this AppliUser (id is not taken into account)
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                firstName,
                lastName,
                birthDate,
                email,
                phoneNumber
        );
    }

    /**
     * Return a String representation of this AppliUser
     * @return formatted string with id, login, firstName, lastName, birthDate, email and phoneNumber
     */
    @Override
    public String toString() {
        return "User {id = " + id + ", login = " + login
                + ", first name = " + firstName + ", lastName  = "
                + lastName + ", birthday date = " + birthDate
                + ", email = " + email + ", phone number = " + phoneNumber + "}";
    }
}