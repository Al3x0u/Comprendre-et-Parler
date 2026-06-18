package be.hers.pi.comprendre_et_parler.DTO;

import be.hers.pi.comprendre_et_parler.models.Beneficiary;

import java.time.LocalDate;

public class UpdateBeneficiaryForm {
    private int id;
    private String login;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;

    /**
     * Empty constructor of an UpdateBeneficiaryForm
     */
    public UpdateBeneficiaryForm() {}

    /**
     * Constructor of a UpdateBeneficiaryForm
     * @param beneficiary the beneficiary from which it takes information
     */
    public UpdateBeneficiaryForm(Beneficiary beneficiary) {
        id = beneficiary.getId();
        login = beneficiary.getLogin();
        firstName = beneficiary.getFirstName();
        lastName = beneficiary.getLastName();
        birthDate = beneficiary.getBirthDate();
        email = beneficiary.getEmail();
        phoneNumber = beneficiary.getPhoneNumber();
    }

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return this.login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the new login
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return this.firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return this.lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return this.birthDate
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * @param birthDate the new birthdate
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * @return this.email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return this.phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
