package be.hers.pi.comprendre_et_parler.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Objects;

public class BaseTimeSlot extends TimeSlot {
    private DayOfWeek day;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public BaseTimeSlot(int id, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, DayOfWeek day)
    {
        super(id);
        this.startDate = startDate;
        if(endDate.isAfter(startDate)) {
            this.endDate = endDate;
        } else {
            this.endDate = startDate;
        }

        this.startTime = startTime;
        if(endTime.isAfter(startTime)) {
            this.endTime = endTime;
        } else {
            this.endTime = startTime;
        }
        this.day = day;
    }

    public BaseTimeSlot(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime, DayOfWeek day)
    {
        super();
        this.startDate = startDate;
        if(endDate.isAfter(startDate)) {
            this.endDate = endDate;
        } else {
            this.endDate = startDate;
        }

        this.startTime = startTime;
        if(endTime.isAfter(startTime)) {
            this.endTime = endTime;
        } else {
            this.endTime = startTime;
        }
        this.day = day;
    }

    public BaseTimeSlot(BaseTimeSlot b)
    {
        this(b.id, b.startDate, b.endDate, b.startTime, b.endTime, b.day);
    }

    public DayOfWeek getDay() {
        return day;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public LocalDate getstartDate() {
        return startDate;
    }

    public void setstartDate(LocalDate startDate) {
        if(startDate.isBefore(this.endDate)) {
            this.startDate = startDate;
        }
    }

    public LocalDate getendDate() {
        return endDate;
    }

    public void setendDate(LocalDate endDate) {
        if(endDate.isAfter(this.startDate)) {
            this.endDate = endDate;
        }
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        if(startTime.isBefore(this.endTime)) {
            this.startTime = startTime;
        }
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        if(endTime.isAfter(this.startTime)) {
            this.endTime = endTime;
        }
    }

    @Override
    public BaseTimeSlot clone() {
        return new BaseTimeSlot(super.id, this.startDate, this.endDate, this.startTime, this.endTime, this.day);
    }

    /**
     * Checks if the 2 timeslots overlaps partially or totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps, or false if it doesn't
     */
    public boolean overlaps(BaseTimeSlot timeSlot) {
        boolean result = false;
        if(this.day.equals(timeSlot.day) && this.startDate.isBefore(timeSlot.endDate) && this.endDate.isAfter(timeSlot.startDate)) {
            result = this.startTime.isBefore(timeSlot.endTime) && this.endTime.isAfter(timeSlot.startTime);
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
        if(this.day.equals(timeSlot.day) && (this.startDate.isBefore(timeSlot.startDate) && this.endDate.isAfter(timeSlot.endDate)) || (this.startDate.isAfter(timeSlot.startDate) && this.endDate.isBefore(timeSlot.endDate))) {
            result = (this.startTime.isBefore(timeSlot.startTime) && this.endTime.isAfter(timeSlot.endTime)) || (this.startTime.isAfter(timeSlot.startTime) && this.endTime.isBefore(timeSlot.endTime));
        }
        return result;
    }

    /**
     * Return a String representation of the BaseTimeSlot containing all fields
     * @return formatted string with day, id, startTime and endTime
     */
    @Override
    public String toString() {
        return "BaseTimeSlot{startDate=" + this.startDate.toString() + " endDate=" + this.endDate.toString() + " startTime=" + this.startTime.toString() + " endTime=" + this.endTime.toString() + " dayOfWeek=" + this.day + super.toString() + "}";
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
        return this.day.equals(other.day) && this.startDate.equals(other.startDate) && this.endDate.equals(other.endDate) && this.startTime.equals(other.startTime) && this.endTime.equals(other.endTime);
    }

    /**
     * Return the hashcode of BaseTimeSlot
     * @return an integer whith is the hashcode of BaseTimeSlot
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.startDate, this.endDate, this.startTime, this.endTime, this.day);
    }
}
