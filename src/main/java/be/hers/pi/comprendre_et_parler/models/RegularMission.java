package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class RegularMission extends PunctualTimeSlot {
    MissionState stateOfMission;

    /**
     * Constructor of a RegularMission
     * @param id represent the id
     * @param startDate represent the startDate
     * @param endDate represent he endDate
     * @param stateOfMission represent the stateOfMission
     */
    public RegularMission(int id, LocalDateTime startDate, LocalDateTime endDate, MissionState stateOfMission) {
        super(id, startDate, endDate);
        this.stateOfMission = stateOfMission;
    }

    /**
     * Constructor of a RegularMission without id
     * @param startDate represent the startDate
     * @param endDate represent the endDate
     * @param stateOfMission represent he stateOfMission
     */
    public RegularMission(LocalDateTime startDate, LocalDateTime endDate, MissionState stateOfMission) {
        super(startDate, endDate);
        this.stateOfMission = stateOfMission;
    }

    /**
     * Constructor of a RegularMission from a PunctualTimeSlot
     * @param punctualTimeSlot represent he punctualTimeSlot
     * @param stateOfMission represent the stateOfMission
     */
    public RegularMission(PunctualTimeSlot punctualTimeSlot, MissionState stateOfMission) {
        super(punctualTimeSlot);
        this.stateOfMission = stateOfMission;
    }

    /**
     * Copy constructor of a RegularMission
     * @param other the RegularMission to copy, must not be null
     */
    public RegularMission(RegularMission other) {
        super(other);
        this.stateOfMission = other.stateOfMission;
    }

    /**
     * @return this.stateOfMission
     */
    public MissionState getStateOfMission() {
        return stateOfMission;
    }

    /**
     * @param state represent the new mission state
     */
    public void setStateOfMission(MissionState state){
        this.stateOfMission = state;
    }

    /**
     * Compare this RegularMission with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical super and stateOfMission
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegularMission)) return false;

        RegularMission other = (RegularMission) o;
        return super.equals(other) && Objects.equals(stateOfMission, other.stateOfMission);
    }

    /**
     * Computes the hash code of this RegularMission
     * two RegularMission objects that are equal according to equals() will have the same hash code
     * @return an integer hash code representing this RegularMission (id is not taken into account)
     */
    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), stateOfMission);
    }

    /**
     * Return a String representation of the RegularMission containing all fields
     * @return formatted string with super and stateOfMission
     */
    @Override
    public String toString(){
        return "RegularMission{" + super.toString() + ", stateOfMission = " + stateOfMission + "}";
    }
}
