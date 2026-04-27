package be.hers.pi.comprendre_et_parler.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class BaseTimeSlot extends TimeSlot {
    private DayOfWeek day;

    public BaseTimeSlot(int id, LocalTime startTime, LocalTime endTime, DayOfWeek day)
    {
        super(id, startTime, endTime);
        this.day = day;
    }

    public BaseTimeSlot(LocalTime beginTime, LocalTime endTime, DayOfWeek day)
    {
        super(beginTime, endTime);
        this.day = day;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    @Override
    public BaseTimeSlot clone() {
        return new BaseTimeSlot(super.id, super.startTime, super.endTime, this.day);
    }

    /**
     * Checks if the 2 timeslots overlaps partially or totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps, or false if it doesn't
     */
    public boolean overlaps(BaseTimeSlot timeSlot) {
        boolean result = false;
        if(this.day.equals(timeSlot.day)) {
            result = super.overlaps(timeSlot);
        }
        return result;
    }

    /**
     * Checks if the 2 timeslots overlaps totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps totally, or false if it doesn't
     */
    public boolean overlapsCompletely(BaseTimeSlot timeSlot) {
        boolean result = false;
        if(this.day.equals(timeSlot.day)) {
            result = super.overlapsCompletely(timeSlot);
        }
        return result;
    }

    /**
     * Return a String representation of the BaseTimeSlot containing all fields
     * @return formatted string with day, id, startTime and endTime
     */
    @Override
    public String toString() {
        return "BaseTimeSlot{dayOfWeek=" + this.day + super.toString() + "}";
    }

    /**
     * Check if the time slot have the same data than the current one
     * @param o time slot to compare
     * @return true if it's the same, else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseTimeSlot)) return false;

        BaseTimeSlot other = (BaseTimeSlot) o;
        return this.day.equals(other.day) && super.equals(other);
    }

    /**
     * Return the hashcode of BaseTimeSlot
     * @return an integer whith is the hashcode of BaseTimeSlot
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), day);
    }
}
