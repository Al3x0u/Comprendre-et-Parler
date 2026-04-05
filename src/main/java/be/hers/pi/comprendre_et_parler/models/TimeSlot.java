package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeSlot {
    private LocalTime startTime;
    private LocalTime endTime;

    /**
        Constructor of a TimeSlot object
        @param startTime represent the hour at which start
        @param endTime represent the hour at which finish
     */
    public TimeSlot(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
        @return this.startTime
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * @param startTime represent the new startTime
     */
    public void setStartTime(LocalTime startTime){
        this.startTime = startTime;
    }

    /**
     * @param endTime represent the new endTime
     */
    public void setEndTime(LocalTime endTime){
        this.endTime = endTime;
    }

    /**
        @return this.startTime
     */
    public LocalTime getEndTime() {
        return endTime;
    }


}
