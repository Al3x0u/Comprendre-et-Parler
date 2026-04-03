package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;

public class Beneficiary extends AppliUser {
    private Status status;
    private Interpreter interpreterRef;


    /**
     * Constructor of a Beneficiary extends User
     *
     * @param login          represent the login
     * @param firstName      represent the firstname
     * @param lastName       represent he lastname
     * @param birthDate      represent the birthdate
     * @param hashedPassword represent the hashed password
     * @param email          represent the email
     * @param phoneNumber    represent the phone number
     * @param status         represent the status
     * @param interpreterRef represent the referent interpreter
     */
    public Beneficiary(String login, String firstName, String lastName, LocalDate birthDate, String hashedPassword, String email, String phoneNumber, Status status, Interpreter interpreterRef) {
        super(login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        this.status = status;
        this.interpreterRef = interpreterRef;
    }

    /**
     * @return this.status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status represent the new Status object
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return this.interpreterRef
     */
    public Interpreter getInterpreterRef() {
        return interpreterRef;
    }

    /**
     * @param interpreterRef represent the new referent Interpreter
     */
    public void setInterpreterRef(Interpreter interpreterRef) {
        this.interpreterRef = interpreterRef;
    }
}
