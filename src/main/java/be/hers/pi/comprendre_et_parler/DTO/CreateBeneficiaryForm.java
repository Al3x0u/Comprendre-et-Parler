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
}