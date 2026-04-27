package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalTime;
import java.util.Objects;

public abstract class TimeSlot {
    protected int id;
    protected LocalTime startTime;
    protected LocalTime endTime;

    public TimeSlot(int id, LocalTime startTime, LocalTime endTime) {
        if (id >= 0) {
            this.id = id;
        } else {
            this.id = 0;
        }
        this.startTime = startTime;
        if(startTime.isAfter(this.endTime)) {
            this.endTime = this.startTime.plusHours(1);
        }
        else {
            this.endTime = endTime;
        }
    }

    public TimeSlot(LocalTime startTime, LocalTime endTime) {
        this.id = -1;
        this.startTime = startTime;
        if(startTime.isAfter(this.endTime)) {
            this.endTime = this.startTime.plusHours(1);
        }
        else {
            this.endTime = endTime;
        }
    }

    public TimeSlot(TimeSlot timeslot) {
        this.id = timeslot.id;
        this.startTime = timeslot.startTime;
        this.endTime = timeslot.endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id >= 0) {
            this.id = id;
        }
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        if (startTime.isBefore(this.endTime)) {
            this.startTime = startTime;
        }
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {

        if (endTime.isAfter(this.startTime)) {
            this.endTime = endTime;
        }
    }

    /**
     * Checks if the 2 timeslots overlaps partially or totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps, or false if it doesn't
     */
    public boolean overlaps(TimeSlot timeSlot) {
        return this.startTime.isBefore(timeSlot.endTime) && this.endTime.isAfter(timeSlot.startTime);
    }

    /**
     * Checks if the 2 timeslots overlaps totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps totally, or false if it doesn't
     */
    public boolean overlapsCompletely(TimeSlot timeSlot) {
        return (this.startTime.isBefore(timeSlot.startTime) && this.endTime.isAfter(timeSlot.endTime)) || (this.startTime.isAfter(timeSlot.startTime) && this.endTime.isBefore(timeSlot.endTime));
    }

    public abstract TimeSlot clone();

    /**
     * Return a String representation of the TimeSlot containing all fields
     * @return formatted string with id, startTime and endTime
     */
    @Override
    public String toString() {
        return "TimeSlot{id=" + this.id + " startTime=" + this.startTime.toString() + " endTime=" + this.endTime.toString() + "}";
    }

    /**
     * Check if the time slot have the same data than the current one
     * @param o time slot to compare
     * @return true if it's the same, else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeSlot)) return false;

        TimeSlot other = (TimeSlot) o;
        return this.startTime.equals(other.startTime) && this.endTime.equals(other.endTime);
    }

    /**
     * Return the hashcode of TimeSlot
     * @return an integer whith is the hashcode of TimeSlot
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.startTime, this.endTime);
    }
}
