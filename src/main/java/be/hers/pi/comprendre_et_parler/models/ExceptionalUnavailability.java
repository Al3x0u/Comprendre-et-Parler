package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class ExceptionalUnavailability {
    private int id;
    private String reason;
    private PunctualTimeSlot timeSlot;
    private Interpreter interpreter;

    public ExceptionalUnavailability(int id, String reason, PunctualTimeSlot timeSlot, Interpreter interpreter) {
        if (id >= 0) {
            this.id = id;
        } else {
            this.id = 0;
        }
        this.reason = reason;
        this.timeSlot = timeSlot.clone();
        this.interpreter = interpreter;
    }

    public ExceptionalUnavailability(String reason, PunctualTimeSlot timeSlot, Interpreter interpreter) {
        this.id = -1;
        this.reason = reason;
        this.timeSlot = timeSlot.clone();
        this.interpreter = interpreter;
    }

    public ExceptionalUnavailability(ExceptionalUnavailability e) {
        this(e.id, e.reason, new PunctualTimeSlot(e.timeSlot), new Interpreter(e.interpreter));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id >= 0) {
            this.id = id;
        }
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public PunctualTimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(PunctualTimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Interpreter getInterpreter() {
        return this.interpreter;
    }

    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public ExceptionalUnavailability clone() {
        return new ExceptionalUnavailability(this.id, this.reason, this.timeSlot, this.interpreter);
    }

    /**
     * Return a String representation of the ExeptionalUnavailability containing all fields
     * @return formatted string with id, reason, timeSlot and interpreter
     */
    @Override
    public String toString() {
        return "ExeptionnalUnavailability{id=" + this.id + " reason=" + this.reason + " timeSlot=" + this.timeSlot.toString() + " interpreter=" + this.interpreter.toString() + "}";
    }

    /**
     * Check if the unavailability have the same data than the current one
     * @param o to compare
     * @return true if it's the same, else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExceptionalUnavailability)) return false;

        ExceptionalUnavailability other = (ExceptionalUnavailability) o;
        return this.reason.equals(other.reason) && this.timeSlot.equals(other.timeSlot) && this.interpreter.equals(other.interpreter);
    }

    /**
     * Return the hashcode of ExeptionnalUnavailability
     * @return an integer whith is the hashcode of ExeptionnalUnavailability
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.reason, this.timeSlot, this.interpreter);
    }
}
