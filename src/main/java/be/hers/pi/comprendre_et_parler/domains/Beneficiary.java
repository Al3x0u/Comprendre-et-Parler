package be.hers.pi.comprendre_et_parler.domains;

import java.time.LocalDate;

public class Beneficiary extends AppliUser {

    /**
     * Constructor of a Beneficiary extends User
     *
     * @param id             represent the id
     * @param login          represent the login
     * @param firstName      represent the firstname
     * @param lastName       represent he lastname
     * @param birthDate      represent the birthdate
     * @param hashedPassword represent the hashed password
     * @param email          represent the email
     * @param phoneNumber    represent the phone number
     */
    public Beneficiary(String id, String login, String firstName, String lastName, LocalDate birthDate, String hashedPassword, String email, String phoneNumber) {
        super(id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
    }


}
