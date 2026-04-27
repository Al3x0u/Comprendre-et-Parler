package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class PunctualTimeSlot extends TimeSlot {
    private LocalDate startDate;
    private LocalDate endDate;

    public PunctualTimeSlot(int id, LocalTime beginTime, LocalTime endTime, LocalDate startDate, LocalDate endDate)
    {
        super(id, beginTime, endTime);
        this.startDate = startDate;

        if (endDate.isAfter(startDate)) {
            this.endDate = endDate;
        } else {
            this.endDate = startDate;
        }
    }

    public PunctualTimeSlot(LocalTime beginTime, LocalTime endTime, LocalDate startDate, LocalDate endDate)
    {
        super(beginTime, endTime);
        this.startDate = startDate;

        if (endDate.isAfter(startDate)) {
            this.endDate = endDate;
        } else {
            this.endDate = startDate;
        }
    }

    public PunctualTimeSlot(PunctualTimeSlot p) {
        this(p.id, p.startTime, p.endTime, p.startDate, p.endDate);
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if (startDate.isBefore(this.endDate)) {
            this.startDate = startDate;
        }
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate.isAfter(this.startDate)) {
            this.endDate = endDate;
        }
    }

    /**
     * Checks if the 2 timeslots overlaps partially or totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps, or false if it doesn't
     */
    public boolean overlaps(PunctualTimeSlot timeSlot) {
        boolean result = false;
        if(this.startDate.isBefore(timeSlot.endDate) && this.endDate.isAfter(timeSlot.startDate)) {
            result = true;
        } else if(this.endDate.equals(timeSlot.startDate) && super.endTime.isAfter(timeSlot.startTime)) {
            result = true;
        } else if(this.startDate.equals(timeSlot.endDate) && super.startTime.isAfter(timeSlot.endTime)) {
            result = true;
        } else if(this.startDate.equals(timeSlot.startDate) || this.endDate.equals(timeSlot.endDate)) {
            result = super.overlaps(timeSlot);
        }
        return result;
    }

    /**
     * Checks if the 2 timeslots overlaps totally
     * @param timeSlot 2nd timeslot for the check
     * @return true if the 2 time slots overlaps totally, or false if it doesn't
     */
    public boolean overlapsCompletely(PunctualTimeSlot timeSlot) {
        boolean result = false;
        if ((this.startDate.isBefore(timeSlot.startDate) && this.endDate.isAfter(timeSlot.endDate)) || (this.startDate.isAfter(timeSlot.startDate) && this.endDate.isBefore(timeSlot.endDate))) {
            result = true;
        } else if(this.endDate.equals(timeSlot.startDate) && super.endTime.isBefore(timeSlot.endTime) && this.startDate.isBefore(timeSlot.startDate)) {
            result = true;
        } else if(this.endDate.equals(timeSlot.startDate) && super.startTime.isBefore(timeSlot.startTime) && this.endDate.isAfter(timeSlot.endDate)) {
            result = true;
        } else if(this.startDate.equals(timeSlot.endDate) && super.endTime.isAfter(timeSlot.endTime) && this.startDate.isAfter(timeSlot.startDate)) {
            result = true;
        } else if(this.startDate.equals(timeSlot.endDate) && super.startTime.isAfter(timeSlot.startTime) && this.endDate.isBefore(timeSlot.endDate)) {
            result = true;
        } else if(this.startDate.equals(timeSlot.startDate) || this.endDate.equals(timeSlot.endDate)) {
            result = super.overlapsCompletely(timeSlot);
        }
        return result;
    }

    @Override
    public PunctualTimeSlot clone() {
        return new PunctualTimeSlot(super.id, super.startTime, super.endTime, this.startDate, this.endDate);
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
        return super.equals(other) && this.startDate.equals(other.startDate) && this.endDate.equals(other.endDate);
    }

    /**
     * Return the hashcode of PunctualTimeSlot
     * @return an integer whith is the hashcode of PunctualTimeSlot
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.startDate, this.endDate);
    }
}
