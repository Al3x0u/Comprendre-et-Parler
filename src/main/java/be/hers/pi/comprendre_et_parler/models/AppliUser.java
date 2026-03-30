package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;

public abstract class AppliUser {
    private String login;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String password;
    private String mail;
    private String phone;

    /**
     Constructor of a AppliUser
     @param login represent the login
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
        birthday = birthDate;
        password = hashedPassword;
        mail = email;
        phone = phoneNumber;
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
    public LocalDate getBirthday() {
        return birthday;
    }

    /**
     * @return this.hashedPassword
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return this.email
     */
    public String getMail() {
        return mail;
    }

    /**
     * @return this.phoneNumber
     */
    public String getPhone() {
        return phone;
    }

}