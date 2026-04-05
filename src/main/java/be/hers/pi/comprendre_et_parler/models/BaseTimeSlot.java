package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalTime;

public class BaseTimeSlot extends TimeSlot{
    private int day;

    /**
        Constructor of a BaseTimeSlot which extends TimeSlot
        @param id represent the id
        @param startHourTime represent the hour at which start
        @param startHourTime represent the hour at which finish
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
     *
     * @param baseTimeSlot represent the BaseTimeSlot to compare
     * @return True if he overlaps this, else False
     */
    public boolean overlaps(BaseTimeSlot baseTimeSlot){
       return false;
    }

    /**
     * Same to the overlaps but compare overlapping completely
     * @param baseTimeSlot represent the BaseTimeSlot to compare
     * @return True if he overlaps completely this, else False
     */
    public boolean overlapsCompletely(BaseTimeSlot baseTimeSlot){
        return false;
    }
}
