package be.hers.pi.comprendre_et_parler.domains;

import java.time.LocalDate;

public abstract class User {
    private String id;
    private String login;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String hashedPassword;
    private String email;
    private String phoneNumber;

    /*
       Constructor of a User
       @param id represent the id
       @param login represent the login
       @param firstName represent the firstname
       @param lastName represent he lastname
       @param birthdate represent the birthdate
       @param hashedPassword represent the hashed password
       @param email represent the email
       @param phoneNumber represent the phone number
    */
    public User(String id,String login, String firstName, String lastName, LocalDate birthDate, String hashedPassword, String email, String phoneNumber) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.hashedPassword = hashedPassword;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }


    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getId() {
        return id;
    }
}
