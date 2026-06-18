package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.Set;

public class Manager extends Interpreter {

    /**
     * Constructor of a Manager,
     * beneficiaries and missions are initialized with null
     * @param id represents the id
     * @param login represents the login
     * @param firstName represents the firstname
     * @param lastName represents the lastname
     * @param birthDate represents the birthdate
     * @param hashedPassword represents the hashed password
     * @param email represents the email
     * @param phoneNumber represents the phone number
     * @param hourQuotaWeek represents the hour quota per week
     * @param hourQuotaYear represents the hour quota per year
     * @param transportMode represents the transport mode
     * @param academicSkills represents the set of academic skills
     * @param jobSkills represents the set of job skills
     * @param location represents the location
     * @param availability represents the set of availabilities
     */
    public Manager(int id, String login, String firstName, String lastName,
                   LocalDate birthDate, String hashedPassword, String email,
                   String phoneNumber, int hourQuotaWeek, int hourQuotaYear, String transportMode,
                   Set<AcademicSkill> academicSkills, Set<JobSkill> jobSkills, Location location,
                   Set<BaseTimeSlot> availability) {
        super(id, login, firstName, lastName, birthDate, hashedPassword,
                email, phoneNumber, hourQuotaWeek, hourQuotaYear, transportMode,
                academicSkills, jobSkills, location, availability);
    }

    /**
     * Constructor of a Manager without id,
     * beneficiaries and missions are initialized with null
     * @param login represents the login
     * @param firstName represents the firstname
     * @param lastName represents the lastname
     * @param birthDate represents the birthdate
     * @param hashedPassword represents the hashed password
     * @param email represents the email
     * @param phoneNumber  represents the phone number
     * @param hourQuotaWeek represents the hour quota per week
     * @param hourQuotaYear represents the hour quota per year
     * @param transportMode represents the transport mode
     * @param academicSkills represents the set of academic skills
     * @param jobSkills represents the set of job skills
     * @param location represents the location
     * @param availability represents the set of availabilities
     */
    public Manager(String login, String firstName, String lastName,
                   LocalDate birthDate, String hashedPassword, String email,
                   String phoneNumber, int hourQuotaWeek, int hourQuotaYear, String transportMode,
                   Set<AcademicSkill> academicSkills, Set<JobSkill> jobSkills, Location location,
                   Set<BaseTimeSlot> availability) {
        super(login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber,
                hourQuotaWeek, hourQuotaYear, transportMode, academicSkills, jobSkills, location, availability);
    }

    /**
     * Return a String representation of this Manager containing all fields
     * @return formatted string with super
     */
    @Override
    public String toString() {
        return "Manager{" + super.toString() + "}";
    }
}