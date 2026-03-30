package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;

public class Beneficiary extends AppliUser {
    private Status status;
    private Interpreter referenceInterpreter;
    /**
     * Constructor of a Beneficiary extends User
     * @param login          represent the login
     * @param firstName      represent the firstname
     * @param lastName       represent he lastname
     * @param birthDate      represent the birthdate
     * @param hashedPassword represent the hashed password
     * @param email          represent the email
     * @param phoneNumber    represent the phone number
     */
    public Beneficiary(String login, String firstName, String lastName, LocalDate birthDate, String hashedPassword, String email, String phoneNumber, Status status, Interpreter referenceInterpreter) {
        super(login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        this.status = status;
        this.referenceInterpreter = referenceInterpreter;
    }
}
