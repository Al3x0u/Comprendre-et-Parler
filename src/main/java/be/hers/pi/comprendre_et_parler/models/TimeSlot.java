package be.hers.pi.comprendre_et_parler.models;

public abstract class TimeSlot {
    protected int id = -1;

    /**
     * Constructor of a TimeSlot Object
     * @param id : represent id
     */
    public TimeSlot(int id) {
        if (id >= 0)
            this.id = id;
    }

    /**
     * Constructor of a TimeSlot Object without id
     */
    public TimeSlot() {}

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id represent the new id
     */
    public void setId(int id) {
        if (id >= 0)
            this.id = id;
    }

    /**
     * @return a copy of this TimeSlot Object
     */
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
     * Check if the time slot have the same data as the current one
     * @param o time slot to compare
     * @return true if it's the same, else false
     */
    @Override
    public abstract boolean equals(Object o);

    /**
     * Return the hashcode of TimeSlot
     * @return an integer with the hashcode of TimeSlot
     */
    @Override
    public abstract int hashCode();
}
