package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class PunctualTimeSlot extends TimeSlot {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    /**
     * Empty constructor of a PunctualTimeSlot
     */
    public PunctualTimeSlot() {}

    /**
     * Constructor of a PunctualTimeSlot
     * @param id represents the id
     * @param startDate represents the startDate
     * @param endDate represents the endDate
     */
    public PunctualTimeSlot(int id, LocalDateTime startDate, LocalDateTime endDate) {
        super(id);

        this.startDate = startDate.withNano(0).withSecond(0);

        if (endDate.isAfter(startDate))
            this.endDate = endDate.withNano(0).withSecond(0);
        else
            this.endDate = this.startDate;
    }

    /**
     * Constructor of a PunctualTimeSlot without id
     * @param startDate represents the startDate
     * @param endDate represents the endDate
     */
    public PunctualTimeSlot(LocalDateTime startDate, LocalDateTime endDate) {
        this(-1, startDate, endDate);
    }

    /**
     * Copy constructor of a PunctualTimeSlot
     * @param other the PunctualTimeSlot to copy, must not be null
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
     * @param startDate represents the new startDate
     */
    public void setStartDate(LocalDateTime startDate) {
        if (startDate.isBefore(this.endDate))
            this.startDate = startDate.withNano(0).withSecond(0);
    }

    /**
     * @return this.endDate
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * @param endDate represents the new endDate
     */
    public void setEndDate(LocalDateTime endDate) {
        if (endDate.isAfter(this.startDate))
            this.endDate = endDate.withNano(0).withSecond(0);
    }

    /**
     * @return a copy of this PunctualTimeSlot
     */
    @Override
    public PunctualTimeSlot clone() {
        return new PunctualTimeSlot(super.id, this.startDate, this.endDate);
    }

    /**
     * Checks if the 2 timeslots overlaps partially or totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps, or false if it doesn't
     */
    public boolean overlaps(PunctualTimeSlot timeSlot) {
        if (timeSlot == null) return false;
        if (timeSlot == this) return true;

        return startDate.isBefore(timeSlot.endDate) && endDate.isAfter(timeSlot.startDate);
    }

    /**
     * Checks if the 2 timeslots overlaps totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps totally, or false if it doesn't
     */
    public boolean overlapsCompletely(PunctualTimeSlot timeSlot) {
        if (timeSlot == null) return false;
        if (timeSlot == this) return true;

        return (startDate.isBefore(timeSlot.startDate) && endDate.isAfter(timeSlot.endDate))
                || (startDate.isAfter(timeSlot.startDate) && endDate.isBefore(timeSlot.endDate));
    }

    /**
     * Compare this PunctualTimeSlot with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical startDate ans endDate
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PunctualTimeSlot)) return false;

        PunctualTimeSlot other = (PunctualTimeSlot) o;
        return Objects.equals(startDate, other.startDate) && Objects.equals(endDate, other.endDate);
    }

    /**
     * Computes the hash code of this PunctualTimeSlot
     * two PunctualTimeSlot objects that are equal according to equals() will have the same hash code
     * @return an integer hash code representing this PunctualTimeSlot (id is not taken into account)
     */
    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    /**
     * Return a String representation of the PonctualTimeSlot containing all fields
     * @return formatted string with super, startDate and endDate
     */
    @Override
    public String toString() {
        return "PonctualTimeSlot{" + super.toString() + ", startDate = " + startDate + ", endDate = " + endDate + "}";
    }
}