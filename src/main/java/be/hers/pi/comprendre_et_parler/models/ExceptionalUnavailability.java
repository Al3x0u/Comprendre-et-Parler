package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class ExceptionalUnavailability {
    private String reason;
    private PunctualTimeSlot timeSlot;

    /**
     * Constructor of a ExceptionalUnavailability
     * @param reason represent the reason
     * @param timeSlot represent the timeSlot
     */
    public ExceptionalUnavailability(String reason, PunctualTimeSlot timeSlot) {
        this.reason = reason;
        this.timeSlot = timeSlot;
    }

    /**
     * Copy constructor of a ExceptionalUnavailability
     * @param other the ExceptionalUnavailability to copy, must not be null
     */
    public ExceptionalUnavailability(ExceptionalUnavailability other) {
        this(other.reason, new PunctualTimeSlot(other.timeSlot));
    }

    /**
     * @return this.reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason represent the new reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return this.timeSlot
     */
    public PunctualTimeSlot getTimeSlot() {
        return timeSlot;
    }

    /**
     * @param timeSlot represent the new timeSlot
     */
    public void setTimeSlot(PunctualTimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    /**
     * Compare this ExceptionalUnavailability with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical reason and timeSlot
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExceptionalUnavailability)) return false;

        ExceptionalUnavailability other = (ExceptionalUnavailability) o;
        return this.reason.equals(other.reason) && this.timeSlot.equals(other.timeSlot);
    }

    /**
     * Computes the hash code of this ExeptionnalUnavailability
     * two ExeptionnalUnavailability objects that are equal according to equals() will have the same hash code
     * @return an integer hash code representing this ExeptionnalUnavailability
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.reason, this.timeSlot);
    }

    /**
     * Return a String representation of this ExeptionnalUnavailability containing all fields
     * @return formatted string with reason and timeSlot
     */
    @Override
    public String toString() {
        return "ExeptionnalUnavailability{reason = " + reason + ", timeSlot = " + timeSlot + "}";
    }
}
