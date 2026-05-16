package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.Set;

public class Manager extends Interpreter {

    /**
     * Constructor of a Manager,
     * beneficiaries and missions are initialized with null
     * @param id represent the id
     * @param login represent the login
     * @param firstName represent the firstname
     * @param lastName represent the lastname
     * @param birthDate represent the birthdate
     * @param hashedPassword represent the hashed password
     * @param email represent the email
     * @param phoneNumber represent the phone number
     * @param hourQuotaWeek represent the hour quota per week
     * @param hourQuotaYear represent the hour quota per year
     * @param transportMode represent the transport mode
     * @param academicSkills represent the set of academic skills
     * @param jobSkills represent the set of job skills
     * @param location represent the location
     * @param availability represent the set of availabilities
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
     * @param login represent the login
     * @param firstName represent the firstname
     * @param lastName represent the lastname
     * @param birthDate represent the birthdate
     * @param hashedPassword represent the hashed password
     * @param email represent the email
     * @param phoneNumber  represent the phone number
     * @param hourQuotaWeek represent the hour quota per week
     * @param hourQuotaYear represent the hour quota per year
     * @param transportMode represent the transport mode
     * @param academicSkills represent the set of academic skills
     * @param jobSkills represent the set of job skills
     * @param location represent the location
     * @param availability represent the set of availabilities
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