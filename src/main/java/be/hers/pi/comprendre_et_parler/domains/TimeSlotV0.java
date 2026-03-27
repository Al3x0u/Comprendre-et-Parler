package be.hers.pi.comprendre_et_parler.domains;

import java.time.LocalDateTime;

public class TimeSlotV0 {
    private String id;
    private LocalDateTime startHourTime;
    private LocalDateTime endHourTime;
    private int day;

    /*
        Constructor of a TimeSlot object that typically concern a Base one
        @param id represent the id
        @param startHourTime represent the hour at which start
        @param startHourTime represent the hour at which finish
        @param day represent the number of the day that the timeslot is associated
     */
    public TimeSlotV0(String id, LocalDateTime startHourTime, LocalDateTime endHourTime, int day) {
        this.id = id;
        this.startHourTime = startHourTime;
        this.endHourTime = endHourTime;
        this.day = day;
    }

    /*
        Constructor of a TimeSlot object that typically concern a punctual one
        @param id represent the id
        @param startHourTime represent the hour at which start AND contains the date of this TimeSlot
        @param startHourTime represent the hour at which finish AND contains the date of this TimeSlot
     */
    public TimeSlotV0(String id, LocalDateTime startHourTimeAndDate, LocalDateTime endHourTimeAndDate) {
        this.id = id;
        this.startHourTime = startHourTimeAndDate;
        this.endHourTime = endHourTimeAndDate;
        this.day = -1;
    }


    /*
        @return this.id
     */
    public String getId() {
        return id;
    }

    /*
        @return this.startHourTime
     */
    public LocalDateTime getStartHourTime() {
        return startHourTime;
    }

    /*
        @return this.startHourTime
     */
    public LocalDateTime getEndHourTime() {
        return endHourTime;
    }

    public int getDay() {
        return day;
    }
}
