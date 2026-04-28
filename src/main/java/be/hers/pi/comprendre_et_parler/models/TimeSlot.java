package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalTime;
import java.util.Objects;

public abstract class TimeSlot {
    protected int id;

    public TimeSlot(int id) {
        if (id >= 0) {
            this.id = id;
        } else {
            this.id = 0;
        }
    }

    public TimeSlot() {
        this.id = -1;
    }

    public TimeSlot(TimeSlot timeslot) {
        this.id = timeslot.id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id >= 0) {
            this.id = id;
        }
    }

    public abstract TimeSlot clone();

    /**
     * Return a String representation of the TimeSlot containing all fields
     * @return formatted string with id, startTime and endTime
     */
    @Override
    public String toString() {
        return "TimeSlot{id=" + this.id + "}";
    }

    /**
     * Check if the time slot have the same data than the current one
     * @param o time slot to compare
     * @return true if it's the same, else false
     */
    @Override
    public abstract boolean equals(Object o);

    /**
     * Return the hashcode of TimeSlot
     * @return an integer whith is the hashcode of TimeSlot
     */
    @Override
    public abstract int hashCode();
}
