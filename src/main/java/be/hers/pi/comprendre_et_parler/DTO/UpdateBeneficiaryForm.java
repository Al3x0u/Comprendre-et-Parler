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

    public UpdateBeneficiaryForm() {}

    public UpdateBeneficiaryForm(Beneficiary beneficiary) {
        id = beneficiary.getId();
        login = beneficiary.getLogin();
        firstName = beneficiary.getFirstName();
        lastName = beneficiary.getLastName();
        birthDate = beneficiary.getBirthDate();
        email = beneficiary.getEmail();
        phoneNumber = beneficiary.getPhoneNumber();
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
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
}
