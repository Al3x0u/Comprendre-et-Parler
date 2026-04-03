package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalTime;

public class BaseTimeSlot extends TimeSlot{
    private int day;

    /**
     Constructor of a BaseTimeSlot which extends TimeSlot
     @param id represent the id
     @param startHourTime represent the hour at which start
     @param endHourTime represent the hour at which finish
     @param day represent the day of the week
     */
    public BaseTimeSlot(String id, LocalTime startHourTime, LocalTime endHourTime, int day) {
        super(id, startHourTime, endHourTime);
        this.day = day;
    }

    /**
     @return this.day
     */
    public int getDay() {
        return day;
    }

    /**
     * Compare this BaseTimeSlot with another one to know if they overlap
     * @param baseTimeSlot represent the BaseTimeSlot to compare, not null
     * @return True if this BaseTimeSlot overlaps the given BaseTimeSlot, else False
     * @throws NullPointerException if baseTimeSlot is null
     */
    public boolean overlaps(BaseTimeSlot baseTimeSlot){
        return false;
    }

    /**
     * Same to the overlaps but compare overlapping completely
     * @param baseTimeSlot represent the BaseTimeSlot to compare, not null
     * @return True if this BaseTimeSlot overlaps completely the given BaseTimeSlot, else False
     * @throws NullPointerException if baseTimeSlot is null
     */
    public boolean overlapsCompletely(BaseTimeSlot baseTimeSlot){
        return false;
    }

    /**
     * Return a String representation of the BaseTimeSlot containing all fields
     * @return formatted string with id, startHourTime, endHourTime and day
     */
    @Override
    public String toString() {
        return null;
    }
}