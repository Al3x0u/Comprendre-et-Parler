package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;

public abstract class AppliUser {
    private String login;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String hashedPassword;
    private String email;
    private String phoneNumber;

    /**
     Constructor of a AppliUser
     @param login represent the id
     @param firstName represent the firstname
     @param lastName represent he lastname
     @param birthDate represent the birthdate
     @param hashedPassword represent the hashed password
     @param email represent the email
     @param phoneNumber represent the phone number
     */
    public AppliUser(String login, String firstName, String lastName, LocalDate birthDate, String hashedPassword, String email, String phoneNumber) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.phoneNumber = phoneNumber;
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


}