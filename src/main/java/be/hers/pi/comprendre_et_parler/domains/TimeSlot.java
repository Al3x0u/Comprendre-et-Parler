package be.hers.pi.comprendre_et_parler.domains;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeSlot {
    private String id;
    private LocalTime startHourTime;
    private LocalTime endHourTime;

    /**
        Constructor of a TimeSlot object
        @param id represent the id
        @param startHourTime represent the hour at which start
        @param endHourTime represent the hour at which finish
     */
    public TimeSlot(String id, LocalTime startHourTime, LocalTime endHourTime) {
        this.id = id;
        this.startHourTime = startHourTime;
        this.endHourTime = endHourTime;
    }

    /**
        @return this.id
     */
    public String getId() {
        return id;
    }

    /**
        @return this.startHourTime
     */
    public LocalTime getStartHourTime() {
        return startHourTime;
    }

    /**
        @return this.startHourTime
     */
    public LocalTime getEndHourTime() {
        return endHourTime;
    }


}
