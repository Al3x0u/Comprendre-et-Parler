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
    public BaseTimeSlot(int id, LocalTime startHourTime, LocalTime endHourTime, int day) {
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
     * @param newDay    represent the new day
     */
    public void setDay(int newDay){
        this.day = newDay;
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
     * Compare this BaseTimeSlot with another BaseTimeSlot for equality
     * @param other the BaseTimeSlot object to compare with
     * @return true if both BaseTimeSlot objects have identical day
     */
    public boolean equals(BaseTimeSlot other) {
        return (super.equals(other) && day == other.day);
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