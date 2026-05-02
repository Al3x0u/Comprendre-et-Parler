package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class ExceptionalUnavailability {
    private String reason;
    private PunctualTimeSlot timeSlot;
    private Interpreter interpreter;

    /**
     * Constructor of a ExceptionalUnavailability Object
     * @param reason : represent reason
     * @param timeSlot : represent timeSlot
     * @param interpreter : represent interpreter
     */
    public ExceptionalUnavailability(String reason, PunctualTimeSlot timeSlot, Interpreter interpreter) {
        this.reason = reason;
        this.timeSlot = timeSlot.clone();
        this.interpreter = interpreter;
    }

    /**
     * Copy constructor of a ExceptionalUnavailability Object
     * @param other represent the PunctualTimeSlot object
     */
    public ExceptionalUnavailability(ExceptionalUnavailability other) {
        this(other.reason, new PunctualTimeSlot(other.timeSlot), new Interpreter(other.interpreter));
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
     * @return this.interpreter
     */
    public Interpreter getInterpreter() {
        return this.interpreter;
    }

    /**
     * @param interpreter represent the new interpreter
     */
    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    /**
     * Return a String representation of the ExeptionalUnavailability containing all fields
     * @return formatted string with reason, timeSlot and interpreter
     */
    @Override
    public String toString() {
        return "ExeptionnalUnavailability{ reason=" + this.reason + " timeSlot=" + this.timeSlot.toString() + " interpreter=" + this.interpreter.toString() + "}";
    }

    /**
     * Check if the unavailability have the same data as the current one
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
     * @return an integer which is the hashcode of ExeptionnalUnavailability
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.reason, this.timeSlot, this.interpreter);
    }
}
