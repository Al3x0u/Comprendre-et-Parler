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
    public PunctualTimeSlot(int id, LocalTime startHourTime, LocalTime endHourTime,LocalDate date) {
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
     * @param newDate represent the new date
     */
    public void setDate(LocalDate newDate){
        this.date = newDate;
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
     * @param ponctualTimeSlot represent the PunctualTimeSlot to compare, not null
     * @return True if this PunctualTimeSlot overlaps completely the given ponctualTimeSlot, else False
     * @throws NullPointerException if ponctualTimeSlot is null
     */
    public boolean overlapsCompletely(PunctualTimeSlot ponctualTimeSlot){
        return false;
    }

    /**
     * Compare this PunctualTimeSlot with another PunctualTimeSlot for equality
     * @param other the PunctualTimeSlot object to compare with
     * @return true if both PunctualTimeSlot objects have identical  date
     */
    public boolean equals(PunctualTimeSlot other) {
        return (super.equals(other) && date == other.date);
    }

    /**
     * Return a String representation of the PunctualTimeSlot containing all fields
     * @return formatted string with id, startHourTime, endHourTime and date
     */
    @Override
    public String toString() {
        return null;
    }
}