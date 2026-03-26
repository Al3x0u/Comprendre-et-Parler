package be.hers.pi.comprendre_et_parler.domains;

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
}
