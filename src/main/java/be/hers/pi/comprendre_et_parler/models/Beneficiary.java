package be.hers.pi.comprendre_et_parler.models;

import be.hers.pi.comprendre_et_parler.DAOs.DAOInterpreter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Beneficiary extends AppliUser {
    private Status status;
    private Interpreter interpreterRef;


    /**
     * Constructor of a Beneficiary extends User
     * @param id             represent the id
     * @param login          represent the login
     * @param firstName      represent the firstname
     * @param lastName       represent he lastname
     * @param birthDate      represent the birthdate
     * @param hashedPassword represent the hashed password
     * @param email          represent the email
     * @param phoneNumber    represent the phone number
     * @param status         represent the status
     */
    public Beneficiary(int id, String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email,
                       String phoneNumber, Status status) {
        super(id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        this.status = status;
        this.interpreterRef = null;
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

    /**
     * Compare this Beneficiary with another Beneficiary for equality
     * @param other the Beneficiary object to compare with
     * @return true if both Beneficiary objects have identical status, interpreterRef and AppliUser fields
     */
    public boolean equals(Object other) {
        if(this == other) return true;
        if(!(other instanceof Beneficiary)) return false;
        Beneficiary beneficiary = (Beneficiary) other;
        return (super.equals(other) && status == beneficiary.status && interpreterRef == beneficiary.interpreterRef);
    }

    /**
     * Return a String representation of the Beneficiary containing all fields
     * @return formatted string with status, interpreterRef and AppliUser information
     */
    @Override
    public String toString() {
        return null;
    }
}
