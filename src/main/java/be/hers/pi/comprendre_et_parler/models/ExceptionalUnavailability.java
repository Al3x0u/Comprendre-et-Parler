package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class ExceptionalUnavailability  {
    private int id;
    private String reason;
    private PunctualTimeSlot ponctualTimeSlot;
    private Interpreter interpreter;

    /**
     * Constructor of a PunctualTimeSlot w
     * @param id            represent the id
     * @param reason        represent the reason of the exceptional unavailability
     * @param interpreter   represent the interpreter which concern this exceptional unavailability
     * @param ponctualTimeSlot represent the ponctual time slot which concern this exceptional unavailability
     */
    public ExceptionalUnavailability(int id, String reason, Interpreter interpreter, PunctualTimeSlot ponctualTimeSlot) {
        this.id = id;
        this.reason = reason;
        this.interpreter = interpreter;
        this.ponctualTimeSlot = ponctualTimeSlot;
    }

    /**
     * @return this.reason
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * @param reason  is new the reason
     */
    public void setReason(String reason){
        this.reason = reason;
    }

    /**
     * @return this.ponctualTimeSlot
     */
    public PunctualTimeSlot getPonctualTimeSlot() {
        return this.ponctualTimeSlot;
    }

    /**
     * @return this.interpreter
     */
    public Interpreter getInterpreter(){
        return this.interpreter;
    }

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id    represent the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Compare this ExceptionalUnavailability with another ExceptionalUnavailability for equality
     * @param other the ExceptionalUnavailability object to compare with
     * @return true if both ExceptionalUnavailability objects have identical id, reason, ponctualTimeSlot and interpreter
     */
    public boolean equals(ExceptionalUnavailability other) {
        return (id == other.id && reason == other.reason &&
                ponctualTimeSlot == other.ponctualTimeSlot && interpreter == other.interpreter);
    }

    /**
     * Return a String representation of the ExceptionalUnavailability containing all fields
     * @return formatted string with id, reason, ponctualTimeSlot and interpreter
     */
    @Override
    public String toString() {
        return null;
    }
}