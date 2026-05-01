package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.Set;

public class Manager extends Interpreter {


    /**
     * Constructor of a Manager object,
     * beneficiaries and missions are initialized with null with id
     * @param id                 represent the id in database
     * @param login              represent the login
     * @param firstName          represent the firstname of the interpreter
     * @param lastName           represent the lastname of the interpreter
     * @param birthDate          represent the birthdate of the interpreter
     * @param hashedPassword     represent the hashed password of the interpreter
     * @param email              represent the email of the interpreter
     * @param phoneNumber        represent the phone number of the interpreter
     * @param hourQuotaWeek                represent the hour quota per week
     * @param hourQuotaYear                represent the hour quota per year
     * @param transportMode      represent the transport mode of the interpreter
     * @param academicSkills           represent the set of academic skills of the interpreter
     * @param jobSkills                represent the set of job skills of the interpreter
     * @param location           represent the location of the interpreter
     * @param time               represent the set of punctual time slots of the interpreter
     */
    public Manager(
            int id, String login, String firstName, String lastName,
            LocalDate birthDate, String hashedPassword,
            String email, String phoneNumber,
            int hourQuotaWeek, int hourQuotaYear,
            String transportMode,
            Set<AcademicSkill> academicSkills, Set<JobSkill> jobSkills,
            Location location,
            Set<BaseTimeSlot> time
    ) {
        super(
                id, login, firstName, lastName,
                birthDate, hashedPassword,
                email, phoneNumber,
                hourQuotaWeek, hourQuotaYear,
                transportMode,
                academicSkills, jobSkills,
                location,
                time
        );
    }

    /**
     * Constructor of a Manager object without id,
     * beneficiaries and missions are initialized with null
     * @param login              represent the login
     * @param firstName          represent the firstname of the interpreter
     * @param lastName           represent the lastname of the interpreter
     * @param birthDate          represent the birthdate of the interpreter
     * @param hashedPassword     represent the hashed password of the interpreter
     * @param email              represent the email of the interpreter
     * @param phoneNumber        represent the phone number of the interpreter
     * @param hourQuotaWeek                represent the hour quota per week
     * @param hourQuotaYear                represent the hour quota per year
     * @param transportMode      represent the transport mode of the interpreter
     * @param academicSkills           represent the set of academic skills of the interpreter
     * @param jobSkills                represent the set of job skills of the interpreter
     * @param location           represent the location of the interpreter
     * @param time               represent the set of punctual time slots of the interpreter
     */
    public Manager(
            String login, String firstName, String lastName,
            LocalDate birthDate, String hashedPassword,
            String email, String phoneNumber,
            int hourQuotaWeek, int hourQuotaYear,
            String transportMode,
            Set<AcademicSkill> academicSkills, Set<JobSkill> jobSkills,
            Location location,
            Set<BaseTimeSlot> time
    ) {
        this(
                -1, login, firstName, lastName,
                birthDate, hashedPassword,
                email, phoneNumber,
                hourQuotaWeek, hourQuotaYear,
                transportMode,
                academicSkills, jobSkills,
                location,
                time
        );
    }

    /**
     * Returns a String representation of this Manager.
     * @return a formatted String representing this Manager
     */
    @Override
    public String toString() {
        return "Manager{" + super.toString() + "}";
    }
}