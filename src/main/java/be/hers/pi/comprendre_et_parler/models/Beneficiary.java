package be.hers.pi.comprendre_et_parler.models;

import be.hers.pi.comprendre_et_parler.DAOs.DAOInterpreter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Beneficiary extends AppliUser {
    private Status status;
    private Interpreter interpreterRef;

    /**
     * Constructor of a Beneficiary extends User with id
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
                       String phoneNumber, Status status, Interpreter interpreterRef) {
        super(id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        this.status = status;
        this.interpreterRef = interpreterRef;
    }

    /**
     * Constructor of a Beneficiary extends User without id
     * @param login          represent the login
     * @param firstName      represent the firstname
     * @param lastName       represent he lastname
     * @param birthDate      represent the birthdate
     * @param hashedPassword represent the hashed password
     * @param email          represent the email
     * @param phoneNumber    represent the phone number
     * @param status         represent the status
     */
    public Beneficiary(String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email,
                       String phoneNumber, Status status, Interpreter interpreterRef) {
        this(-1, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, status, interpreterRef);
    }

    /**
     * Copy constructor. Creates a deep copy of the given Beneficiary.
     * The interpreterRef field is shared (shallow copy) to avoid
     * an infinite recursion between Beneficiary and Interpreter
     * copy constructors.
     *
     * @param other the Beneficiary to copy, must not be null
     */
    public Beneficiary(Beneficiary other) {
        this(other.getId(), other.getLogin(), other.getFirstName(), other.getLastName(),
                other.getBirthDate(), other.getHashedPassword(), other.getEmail(),
                other.getPhoneNumber(), new Status(other.status), new Interpreter(other.interpreterRef));
    }

    public Beneficiary clone() {
        return new Beneficiary(id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, new Status(status), new Interpreter(interpreterRef));
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
        return (super.equals(other) && Objects.equals(status, beneficiary.status) && Objects.equals(interpreterRef, beneficiary.interpreterRef));
    }

    /**
     * Computes a hash code for this Interpreter based on its attributes.
     * two Interpreter objects that are equal according to equals() will have the same hash code.
     * @return an integer hash code representing this Beneficiary
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                status,
                interpreterRef
        );
    }

    /**
     * Return a String representation of the Beneficiary containing all fields
     * @return formatted string with status, interpreterRef and AppliUser information
     */
    @Override
    public String toString() {
        return "Beneficiary " + super.toString() + ", status = " + status.getDesignation()
                + ", reference Interpreter login = " + interpreterRef.getLogin() + "}";
    }
}
