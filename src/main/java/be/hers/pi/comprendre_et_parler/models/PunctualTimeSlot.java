package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class PunctualTimeSlot extends TimeSlot {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    /**
     * Constructor of a PunctualTimeSlot Object
     * @param id : represent id
     * @param startDate : represent startDate
     * @param endDate : represent endDate
     */
    public PunctualTimeSlot(int id, LocalDateTime startDate, LocalDateTime endDate) {
        super(id);
        this.startDate = startDate.withNano(0).withSecond(0);

        if (endDate.isAfter(startDate)) {
            this.endDate = endDate.withNano(0).withSecond(0);
        } else {
            this.endDate = this.startDate;
        }
    }

    /**
     * Constructor of a PunctualTimeSlot Object without id
     * @param startDate : represent startDate
     * @param endDate : represent endDate
     */
    public PunctualTimeSlot(LocalDateTime startDate, LocalDateTime endDate) {
        this(-1, startDate, endDate);
    }

    /**
     * Copy constructor of a PunctualTimeSlot Object
     * @param other represent the PunctualTimeSlot object
     */
    public PunctualTimeSlot(PunctualTimeSlot other) {
        this(other.id, other.startDate,other.endDate);
    }

    /**
     * @return this.startDate
     */
    public LocalDateTime getStartDate() {
        return startDate;
    }

    /**
     * @param startDate represent the new startDate
     */
    public void setStartDate(LocalDateTime startDate) {
        if (startDate.isBefore(this.endDate)) {
            this.startDate = startDate.withNano(0).withSecond(0);
        }
    }

    /**
     * @return this.endDate
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * @param endDate represent the new endDate
     */
    public void setEndDate(LocalDateTime endDate) {
        if (endDate.isAfter(this.startDate)) {
            this.endDate = endDate.withNano(0).withSecond(0);
        }
    }

    /**
     * Checks if the 2 timeslots overlaps partially or totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps, or false if it doesn't
     */
    public boolean overlaps(PunctualTimeSlot timeSlot) {
        if(timeSlot == null) return false;
        if(timeSlot == this) return true;
        return this.startDate.isBefore(timeSlot.endDate) && this.endDate.isAfter(timeSlot.startDate);
    }

    /**
     * Checks if the 2 timeslots overlaps totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps totally, or false if it doesn't
     */
    public boolean overlapsCompletely(PunctualTimeSlot timeSlot) {
        if(timeSlot == null) return false;
        if(timeSlot == this) return true;
        return (this.startDate.isBefore(timeSlot.startDate) && this.endDate.isAfter(timeSlot.endDate))
                || (this.startDate.isAfter(timeSlot.startDate) && this.endDate.isBefore(timeSlot.endDate));
    }

    /**
     * @return a copy of this PunctualTimeSlot Object
     */
    @Override
    public PunctualTimeSlot clone() {
        return new PunctualTimeSlot(super.id, this.startDate, this.endDate);
    }

    /**
     * Return a String representation of the PonctualTimeSlot containing all fields
     * @return formatted string with date, id, startTime and endTime
     */
    @Override
    public String toString() {
        return "PonctualTimeSlot{startDate=" + this.startDate.toString() + " endDate=" + this.endDate.toString() + super.toString() + "}";
    }

    /**
     * Check if the time slot have the same data than the current one
     * @param o time slot to compare
     * @return true if it's the same, else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PunctualTimeSlot)) return false;

        PunctualTimeSlot other = (PunctualTimeSlot) o;
        return this.startDate.equals(other.startDate) && this.endDate.equals(other.endDate);
    }

    /**
     * Return the hashcode of PunctualTimeSlot
     * @return an integer whith is the hashcode of PunctualTimeSlot
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.startDate, this.endDate);
    }
}
