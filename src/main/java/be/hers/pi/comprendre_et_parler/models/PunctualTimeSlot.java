package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class PunctualTimeSlot extends TimeSlot{
    private LocalDate date;

    /**
     * Constructor of a PunctualTimeSlot which extends TimeSlot
     * @param id represent the id
     * @param startHourTime represent the hour at which start
     * @param endHourTime represent the hour at which finish
     * @param date represent the date of the ponctual time slot
     */
    public PunctualTimeSlot(String id, LocalTime startHourTime, LocalTime endHourTime,LocalDate date) {
        super(id,startHourTime,endHourTime);
        this.date = date;
    }

    /**
     * @return this.date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Compare this PunctualTimeSlot with another one to know if they overlap
     * @param ponctualTimeSlot represent the ponctualTimeSlot to compare, not null
     * @return True if this PunctualTimeSlot overlaps the given ponctualTimeSlot, else False
     * @throws NullPointerException if ponctualTimeSlot is null
     */
    public boolean overlaps(PunctualTimeSlot ponctualTimeSlot){
        return false;
    }

    /**
     * Same to the overlaps but compare overlapping completely
     * @param ponctualTimeSlot represent the BaseTimeSlot to compare, not null
     * @return True if this PunctualTimeSlot overlaps completely the given ponctualTimeSlot, else False
     * @throws NullPointerException if ponctualTimeSlot is null
     */
    public boolean overlapsCompletely(PunctualTimeSlot ponctualTimeSlot){
        return false;
    }
}