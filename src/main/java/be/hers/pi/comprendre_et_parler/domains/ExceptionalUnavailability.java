package be.hers.pi.comprendre_et_parler.domains;

import java.time.LocalDate;
import java.time.LocalTime;

public class ExceptionalUnavailability extends PonctualTimeSlot {
    private String reason;

    /**
     * Constructor of a PonctualTimeSlot which extends TimeSlot
     *
     * @param id            represent the id
     * @param startHourTime represent the hour at which start
     * @param endHourTime   represent the hour at which finish
     * @param date          represent the date of the ponctual time slot
     * @param reason        represent the reason of the exceptional unavailability
     */
    public ExceptionalUnavailability(String id, LocalTime startHourTime, LocalTime endHourTime, LocalDate date, String reason) {
        super(id, startHourTime, endHourTime, date);
        this.reason = reason;
    }

    /**
     * @return this.reason
     */
    public String getReason() {
        return reason;
    }
}
