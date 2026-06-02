package be.hers.pi.comprendre_et_parler.models;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.Objects;

public class BaseTimeSlot extends TimeSlot {
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private DayOfWeek day;

    /**
     * Empty constructor of a BaseTimeSlot
     */
    public BaseTimeSlot() {}

    /**
     * Constructor of a BaseTimeSlot
     * @param id represent the id
     * @param startDate represent the startDate
     * @param endDate represent the endDate
     * @param startTime represent the startTime
     * @param endTime represent the endTime
     * @param day represent the day
     */
    public BaseTimeSlot(int id, LocalDate startDate, LocalDate endDate,
                        LocalTime startTime, LocalTime endTime, DayOfWeek day) {
        super(id);
        this.startDate = startDate;
        if (!endDate.isBefore(startDate))
            this.endDate = endDate;
        else
            this.endDate = startDate;

        this.startTime = startTime.withNano(0).withSecond(0);
        if (!endTime.isBefore(startTime))
            this.endTime = endTime.withNano(0).withSecond(0);
        else
            this.endTime = this.startTime;

        this.day = day;
    }

    /**
     * Constructor of a BaseTimeSlot without id
     * @param startDate represent the startDate
     * @param endDate represent the endDate
     * @param startTime represent the startTime
     * @param endTime represent the endTime
     * @param day represent the day
     */
    public BaseTimeSlot(LocalDate startDate, LocalDate endDate,
                        LocalTime startTime, LocalTime endTime, DayOfWeek day) {
        this(-1, startDate, endDate, startTime, endTime, day);
    }

    /**
     * Copy constructor of a BaseTimeSlot
     * @param other the BaseTimeSlot to copy, must not be null
     */
    public BaseTimeSlot(BaseTimeSlot other) {
        this(other.id, other.startDate, other.endDate, other.startTime, other.endTime, other.day);
    }

    /**
     * @return this.startDate
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @param startDate represent the new startDate
     */
    public void setStartDate(LocalDate startDate) {
        if (!startDate.isAfter(this.endDate))
            this.startDate = startDate;
    }

    /**
     * @return this.endDate
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @param endDate represent the new endDate
     */
    public void setEndDate(LocalDate endDate) {
        if (!endDate.isBefore(this.startDate))
            this.endDate = endDate;
    }

    /**
     * @return this.startTime
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * @param startTime represent the new startTime
     */
    public void setStartTime(LocalTime startTime) {
        if (startTime.isBefore(this.endTime))
            this.startTime = startTime.withNano(0).withSecond(0);
    }

    /**
     * @return this.endTime
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * @param endTime represent the new endTime
     */
    public void setEndTime(LocalTime endTime) {
        if (endTime.isAfter(this.startTime))
            this.endTime = endTime.withNano(0).withSecond(0);
    }

    /**
     * @return this.day
     */
    public DayOfWeek getDay() {
        return day;
    }

    /**
     * @param day represent the new day
     */
    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    /**
     * @return a copy of this BaseTimeSlot
     */
    @Override
    public BaseTimeSlot clone() {
        return new BaseTimeSlot(this);
    }

    /**
     * Checks if the 2 timeslots overlaps partially or totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps, or false if it doesn't
     */
    public boolean overlaps(BaseTimeSlot timeSlot) {
        if (timeSlot == null) return false;
        if (timeSlot == this) return true;
        return day.equals(timeSlot.day) && startTime.isBefore(timeSlot.endTime)
                && endTime.isAfter(timeSlot.startTime);
    }

    /**
     * Checks if the 2 timeslots overlaps totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps totally, or false if it doesn't
     */
    public boolean overlapsCompletely(BaseTimeSlot timeSlot) {
        if (timeSlot == null) return false;
        if (timeSlot == this) return true;
        return day.equals(timeSlot.day)
                && ((startTime.isBefore(timeSlot.startTime) && endTime.isAfter(timeSlot.endTime))
                || (startTime.isAfter(timeSlot.startTime) && endTime.isBefore(timeSlot.endTime)));
    }

    /**
     * Compare this BaseTimeSlot with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical day, startDate, endDate, startTime and endTime
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseTimeSlot)) return false;

        BaseTimeSlot other = (BaseTimeSlot) o;
        return Objects.equals(day, other.day) && Objects.equals(startDate, other.startDate)
                && Objects.equals(endDate, other.endDate) && Objects.equals(startTime, other.startTime)
                && Objects.equals(endTime, other.endTime);
    }

    /**
     * Computes the hash code of this BaseTimeSlot
     * two BaseTimeSlot objects that are equal according to equals() will have the same hash code
     * @return an integer hash code representing this BaseTimeSlot (id is not taken into account)
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.startDate, this.endDate, this.startTime, this.endTime, this.day);
    }

    /**
     * Return a String representation of the BaseTimeSlot containing all fields
     * @return formatted string with super, startDate, endDate, startTime, endTime and day
     */
    @Override
    public String toString() {
        return "BaseTimeSlot{" + super.toString() + ", startDate = " + startDate
                + ", endDate = " + endDate + ", startTime = " + startTime
                + ", endTime = " + endTime + ", dayOfWeek = " + day + "}";
    }
}
