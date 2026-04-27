package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.Set;

public class Manager extends Interpreter {


    /**
     * Constructor of a Manager object
     */
    public Manager(
            int id, String login, String firstName, String lastName,
            LocalDate birthDate, String hashedPassword,
            String email, String phoneNumber,
            int hourQuotaWeek, int hourQuotaYear,
            String transportMode,
            Set<AcademicSkill> academicSkills, Set<JobSkill> jobSkills,
            Location location,
            Set<BaseTimeSlot> time,
            Set<ExceptionalUnavailability> unavailabilities
    ) {
        super(
                id, login, firstName, lastName,
                birthDate, hashedPassword,
                email, phoneNumber,
                hourQuotaWeek, hourQuotaYear,
                transportMode,
                academicSkills, jobSkills,
                location,
                time,
                unavailabilities
        );
    }

    /**
     * Constructor of a Manager object
     */
    public Manager(
            String login, String firstName, String lastName,
            LocalDate birthDate, String hashedPassword,
            String email, String phoneNumber,
            int hourQuotaWeek, int hourQuotaYear,
            String transportMode,
            Set<AcademicSkill> academicSkills, Set<JobSkill> jobSkills,
            Location location,
            Set<BaseTimeSlot> time,
            Set<ExceptionalUnavailability> unavailabilities
    ) {
        this(
                -1, login, firstName, lastName,
                birthDate, hashedPassword,
                email, phoneNumber,
                hourQuotaWeek, hourQuotaYear,
                transportMode,
                academicSkills, jobSkills,
                location,
                time,
                unavailabilities
        );
    }

    @Override
    public String toString() {
        return "Manager{" + super.toString() + "}";
    }
}