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

    /**
     Constructor of a AppliUser with id
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
        if(id >= 0) this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     Constructor of a AppliUser without id
     @param login represent the login
     @param firstName represent the firstname
     @param lastName represent he lastname
     @param birthDate represent the birthdate
     @param hashedPassword represent the hashed password
     @param email represent the email
     @param phoneNumber represent the phone number
     */
    public AppliUser(String login, String firstName, String lastName, LocalDate birthDate, String hashedPassword, String email, String phoneNumber) {
        this(-1, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
    }

    /**
     * Copy constructor. Creates a deep copy of the given AppliUser.
     * @param other the AppliUser to copy, must not be null
     */
    public AppliUser(AppliUser other) {
        this(other.id, other.login, other.firstName, other.lastName, other.birthDate, other.hashedPassword, other.email, other.phoneNumber);
    }

    public abstract AppliUser clone();

    /**
     * @param id represent the new id
     */
    public void setId(int id) {
        if(id >= 0) this.id = id;
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
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if(!(other instanceof AppliUser) ) return false;
        AppliUser user = (AppliUser) other;
        return (Objects.equals(login, user.login) && Objects.equals(firstName, user.firstName)  && Objects.equals(lastName, user.lastName) &&
                Objects.equals(birthDate, user.birthDate) && Objects.equals(hashedPassword, user.hashedPassword) &&
                Objects.equals(email,user.email) && Objects.equals(phoneNumber,user.phoneNumber));
    }

    /**
     * Computes a hash code for this Interpreter based on its attributes.
     * two Interpreter objects that are equal according to equals() will have the same hash code.
     * @return an integer hash code representing this AppliUser
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                login,
                firstName,
                lastName,
                birthDate,
                hashedPassword,
                email,
                phoneNumber
        );
    }

    /**
     * Return a String representation of the AppliUser containing all fields
     * @return formatted string with login, firstName, lastName, birthDate, hashedPassword, email and phoneNumber
     */
    @Override
    public String toString(){
        return "User {id = " + id + ", login = " + login
                + ", first name = " + firstName + ", lastName  = "
                + lastName + ", birthday date = " + birthDate
                + ", email = " + email + ", phone number = " + phoneNumber;
    }
}