package be.hers.pi.comprendre_et_parler.models;

public abstract class TimeSlot {
    protected int id = -1;

    /**
     * Constructor of a TimeSlot
     * @param id represent the id
     */
    public TimeSlot(int id) {
        if (id >= 0) this.id = id;
    }

    /**
     * Constructor of a TimeSlot without id
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

    @Override
    public abstract TimeSlot clone();

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    /**
     * Return a String representation of the TimeSlot containing all fields
     * @return formatted string with id
     */
    @Override
    public String toString() {
        return "TimeSlot{id = " + id + "}";
    }
}