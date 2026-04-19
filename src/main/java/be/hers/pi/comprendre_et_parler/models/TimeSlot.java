package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeSlot {
    private int id;
    private String Interpreter;
    private LocalTime startTime;
    private LocalTime endTime;

    /**
        Constructor of a TimeSlot object
        @param startTime represent the hour at which start
        @param endTime represent the hour at which finish
     */
    public TimeSlot(int id, String interpreter, LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return this.id;
    }

    public String getInterpreter() {
        return this.Interpreter;
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
