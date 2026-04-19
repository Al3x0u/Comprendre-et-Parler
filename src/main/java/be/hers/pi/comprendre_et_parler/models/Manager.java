package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.util.List;

public class Manager extends Interpreter {

    /**
     * Constructor of a Manager object
     * @param hQW            represent the hour quota per week
     * @param hQY            represent the hour quota per year
     * @param id             represent the id
     * @param login          represent the login
     * @param firstName      represent the firstname of the Manager
     * @param lastName       represent he lastname of the Manager
     * @param birthDate      represent the birthdate of the Manager
     * @param hashedPassword represent the hashed password of the Manager
     * @param email          represent the email of the Manager
     * @param phoneNumber    represent the phone number of the Manager
     * @param transportMode  represent the transport mode of the Manager
     */
    public Manager(int id, String login, String firstName, String lastName, LocalDate birthDate,
                   String hashedPassword, String email, String phoneNumber, int hQW, int hQY,
                   Transportation transportMode, List<AcademicSkill> academic, List<JobSkill> job,
                   Location location, List<PunctualTimeSlot> time, List<ExceptionalUnavailability> unavailability) {
        super(id, login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber,
                hQW, hQY, transportMode, academic, job, location, time, unavailability);
    }

    /**
     * Compare this Manager with another Manager for equality
     * @param other the Manager object to compare with
     * @return true if both Manager objects have Interpreter fields
     */
    public boolean equals(Manager other) {
        return false;
    }

    /**
     * Return a String representation of the Manager containing all fields
     * @return formatted string with Interpreter fields
     */
    @Override
    public String toString() {
        return null;
    }
}