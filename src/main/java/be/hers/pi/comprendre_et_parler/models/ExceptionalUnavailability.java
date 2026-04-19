package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class ExceptionalUnavailability  {
    private String reason;
    private PunctualTimeSlot ponctualTimeSlot;
    private Interpreter interpreter;

    /**
     * Constructor of a PunctualTimeSlot
     * @param reason        represent the reason of the exceptional unavailability
     * @param interpreter   represent the interpreter which concern this exceptional unavailability
     * @param ponctualTimeSlot represent the ponctual time slot which concern this exceptional unavailability
     */
    public ExceptionalUnavailability(String reason,  PunctualTimeSlot ponctualTimeSlot, Interpreter interpreter) {
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

}