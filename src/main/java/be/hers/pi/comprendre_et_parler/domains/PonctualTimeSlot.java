package be.hers.pi.comprendre_et_parler.domains;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class PonctualTimeSlot extends TimeSlot{
    private LocalDateTime date;

    /*
        Constructor of a PonctualTimeSlot which extends TimeSlot
        @param id represent the id
        @param startHourTime represent the hour at which start
        @param startHourTime represent the hour at which finish
        @param date represent the date of the ponctual time slot
     */
    public PonctualTimeSlot(String id, LocalTime startHourTime, LocalTime endHourTime,LocalDateTime date) {
        super(id,startHourTime,endHourTime);
        this.date = date;
    }

    /*
        @return this.date
     */
    public LocalDateTime getDate() {
        return date;
    }
}
