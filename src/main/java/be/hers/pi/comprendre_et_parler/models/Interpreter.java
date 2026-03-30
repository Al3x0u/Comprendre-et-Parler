package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.List;

public class Interpreter extends AppliUser{
    private int hourQuotaWeek;
    private int hourQuotayear;
    private Transportation transportation;
    private List<AcademicSkill> academicSkills;
    private List<JobSkill> jobSkills;
    private List<Beneficiary> beneficiaries;

    /**
     * Constructor of an Interpreter object
     * @param hQW            represent the hour quota per week
     * @param hQY            represent the hour quota per year
     * @param login          represent the login
     * @param firstName      represent the firstname of the interpreter
     * @param lastName       represent he lastname of the interpreter
     * @param birthDate      represent the birthdate of the interpreter
     * @param hashedPassword represent the hashed password of the interpreter
     * @param email          represent the email of the interpreter
     * @param phoneNumber    represent the phone number of the interpreter
     * @param transport  represent the transport mode of the interpreter
     */
    public Interpreter(String login,String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email,
                       String phoneNumber,int hQW, int hQY, Transportation transport,
                       List<AcademicSkill> academic, List<JobSkill> job, List<Beneficiary> beneficiaries) {
        super(login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        hourQuotaWeek = hQW;
        hourQuotayear = hQY;
        transportation = transport;
        jobSkills = job;
        academicSkills = academic;
        this.beneficiaries = beneficiaries;
    }


    /**
        @return this.hourQuotaWeek
     */
    public int getHourQuotaWeek() {
        return hourQuotaWeek;
    }

    /**
     * @param newHourQuotaWeek represent the new quota hour
     */
    public void setHourQuotaWeek(int newHourQuotaWeek){
        this.hourQuotaWeek = newHourQuotaWeek;
    }

    public int getHourQuotayear() {
        return this.hourQuotayear;
    }

    public void setHourQuotayear(final int hourQuotayear) {
        this.hourQuotayear = hourQuotayear;
    }

    public Transportation getTransportation() {
        return this.transportation;
    }

    public void setTransportation(final Transportation transportation) {
        this.transportation = transportation;
    }

    public List<AcademicSkill> getAcademicSkills() {
        return this.academicSkills;
    }

    public void setAcademicSkills(final List<AcademicSkill> academicSkills) {
        this.academicSkills = academicSkills;
    }

    public List<JobSkill> getJobSkills() {
        return this.jobSkills;
    }

    public void setJobSkills(final List<JobSkill> jobSkills) {
        this.jobSkills = jobSkills;
    }

    public List<Beneficiary> getBeneficiaries() {
        return this.beneficiaries;
    }

    public void setBeneficiaries(final List<Beneficiary> beneficiaries) {
        this.beneficiaries = beneficiaries;
    }


}
