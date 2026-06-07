package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.Objects;

public class Beneficiary extends AppliUser {
    private Status status;
    private Interpreter interpreterRef;

    /**
     * Constructor of a Beneficiary
     * @param id represent the id
     * @param login represent the login
     * @param firstName represent the firstname
     * @param lastName represent he lastname
     * @param birthDate represent the birthdate
     * @param hashedPassword represent the hashed password
     * @param email represent the email
     * @param phoneNumber represent the phone number
     * @param status represent the status
     * @param interpreterRef represent the reference interpreter
     */
    public Beneficiary(int id, String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email,
                       String phoneNumber, Status status, Interpreter interpreterRef) {
        super(id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        this.status = status;
        this.interpreterRef = interpreterRef;
    }

    /**
     * Constructor of a Beneficiary without id
     * @param login represent the login
     * @param firstName represent the firstname
     * @param lastName represent he lastname
     * @param birthDate represent the birthdate
     * @param hashedPassword represent the hashed password
     * @param email represent the email
     * @param phoneNumber represent the phone number
     * @param status represent the status
     * @param interpreterRef represent the reference interpreter
     */
    public Beneficiary(String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email,
                       String phoneNumber, Status status, Interpreter interpreterRef) {
        this(-1, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber, status, interpreterRef);
    }

    /**
     * Copy constructor of a Beneficiary
     * @param other the Beneficiary to copy, must not be null
     */
    public Beneficiary(Beneficiary other) {
        super(other.getId(), other.getLogin(), other.getFirstName(), other.getLastName(),
                other.getBirthDate(), other.getHashedPassword(), other.getEmail(), other.getPhoneNumber());

        if (other.status != null)
            status = new Status(other.status);

        if (other.interpreterRef != null)
            interpreterRef = new Interpreter(other.interpreterRef);
    }

    /**
     * @return this.status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status represent the new status
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
     * @return a copy of this Beneficiary
     */
    @Override
    public Beneficiary clone() {
        return new Beneficiary(id, login, firstName, lastName, birthDate, hashedPassword,
                email, phoneNumber, new Status(status), new Interpreter(interpreterRef));
    }

    /**
     * Compare this Beneficiary with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical first name, last name, birthdate, hashed password, email, phone number,
     * status and interpreterRef
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Beneficiary)) return false;

        Beneficiary other = (Beneficiary) o;
        return (super.equals(other) && Objects.equals(status, other.status)
                && Objects.equals(interpreterRef, other.interpreterRef));
    }

    /**
     * Computes the hash code of this Beneficiary
     * two Beneficiary objects that are equal according to equals() will have the same hash code.
     * @return an integer hash code representing this Beneficiary (id is not taken into account)
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                status,
                interpreterRef.getLogin()
        );
    }

    /**
     * Return a String representation of this Beneficiary
     * @return formatted string with super, status and interpreterRef.login
     */
    @Override
    public String toString() {
        return "Beneficiary {" + super.toString() + ", status = " + status.getDesignation()
                + ", reference Interpreter login = " + interpreterRef.getLogin() + "}";
    }
}
