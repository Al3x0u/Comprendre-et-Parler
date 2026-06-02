package be.hers.pi.comprendre_et_parler.DTO;

import be.hers.pi.comprendre_et_parler.models.*;

import java.time.LocalDate;

public class UpdateBeneficiaryForm {
    private int id;
    private String login;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;

    public UpdateBeneficiaryForm(){}

    public UpdateBeneficiaryForm(Beneficiary beneficiary) {
        this.id = beneficiary.getId();
        this.login = beneficiary.getLogin();
        this.firstName = beneficiary.getFirstName();
        this.lastName = beneficiary.getLastName();
        this.birthDate = beneficiary.getBirthDate();
        this.email = beneficiary.getEmail();
        this.phoneNumber = beneficiary.getPhoneNumber();
    }

    public UpdateBeneficiaryForm(String fName, String lName, LocalDate birthDate, String email, String phone){
        firstName = fName;
        lastName = lName;
        this.birthDate = birthDate;
        this.email = email;
        phoneNumber = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "UpdateBeneficiaryForm{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
