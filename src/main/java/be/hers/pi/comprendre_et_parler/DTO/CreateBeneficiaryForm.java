package be.hers.pi.comprendre_et_parler.DTO;

import java.time.LocalDate;

public class CreateBeneficiaryForm {

    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;
    private String password;
    private int statusId;
    private int interpreterRefId;

    /**
     * Default constructor required by Thymeleaf to instantiate
     * and bind form fields via th:object and th:field
     */
    public CreateBeneficiaryForm() {}

    /**
     * Constructor of a CreateBeneficiaryForm
     * @param firstName the first name of the beneficiary
     * @param lastName the last name of the beneficiary
     * @param birthDate the birthdate of the beneficiary
     * @param email the email address of the beneficiary
     * @param phoneNumber the phone number of the beneficiary, may be null
     * @param password the plain text temporary password chosen by the manager
     * @param statusId the id of the status to assign to the beneficiary
     * @param interpreterRefId the id of the reference interpreter to assign
     */
    public CreateBeneficiaryForm(String firstName, String lastName, LocalDate birthDate,
                                 String email, String phoneNumber, String password,
                                 int statusId, int interpreterRefId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.statusId = statusId;
        this.interpreterRefId = interpreterRefId;
    }

    /**
     * @return this.firstName
     */
    public String getFirstName() { return firstName; }

    /**
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * @return this.lastName
     */
    public String getLastName() { return lastName; }

    /**
     * @param lastName the new last name
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * @return this.birthDate
     */
    public LocalDate getBirthDate() { return birthDate; }

    /**
     * @param birthDate the new birthdate
     */
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    /**
     * @return this.email
     */
    public String getEmail() { return email; }

    /**
     * @param email the new email address
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * @return this.phoneNumber
     */
    public String getPhoneNumber() { return phoneNumber; }

    /**
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    /**
     * @return this.password
     */
    public String getPassword() { return password; }

    /**
     * @param password the new plain text password
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * @return this.statusId
     */
    public int getStatusId() { return statusId; }

    /**
     * @param statusId the new status id
     */
    public void setStatusId(int statusId) { this.statusId = statusId; }

    /**
     * @return this.interpreterRefId
     */
    public int getInterpreterRefId() { return interpreterRefId; }

    /**
     * @param interpreterRefId the new reference interpreter id
     */
    public void setInterpreterRefId(int interpreterRefId) { this.interpreterRefId = interpreterRefId; }

    /**
     * Return a String representation of this CreateBeneficiaryForm containing all fields
     * @return formatted string with all fields
     */
    @Override
    public String toString() {
        return "CreateBeneficiaryForm{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", statusId=" + statusId +
                ", interpreterRefId=" + interpreterRefId +
                '}';
    }
}