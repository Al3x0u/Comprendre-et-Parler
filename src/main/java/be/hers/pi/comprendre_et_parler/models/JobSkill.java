package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class JobSkill {
    private int id = -1;
    private String designation;

    /**
     * Constructor of a JobSkill
     * @param id represent the id
     * @param designation represent the designation
     */
    public JobSkill(int id, String designation) {
        if(id >= 0)
            this.id = id;
        this.designation = designation;
    }

    /**
     Constructor of a JobSkill without id
     @param designation : represent designation
     */
    public JobSkill(String designation) {
        this(-1, designation);
    }

    /**
     * Copy constructor of a JobSkill
     * @param other represent the JobSkill to copy, must not be null
     */
    public JobSkill(JobSkill other) {
        this(other.id, other.designation);
    }

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
        if(id >= 0)
            this.id = id;
    }

    /**
     * @return this.designation
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * @param designation represent the new designation
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * Compare this JobSkill with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical designation
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobSkill)) return false;

        JobSkill other = (JobSkill) o;
        return Objects.equals(designation, other.designation);
    }

    /**
     * Computes the hash code of this JobSkill
     * two JobSkill objects that are equal according to equals() will have the same hash code
     * @return an integer hash code representing this JobSkill (id is not taken into account)
     */
    @Override
    public int hashCode(){
        return Objects.hash(designation);
    }


    /**
     * Return a String representation of the JobSkill containing all fields
     * @return formatted string with id and designation
     */

    @Override
    public String toString() {
        return "JobSkill{id = " + id + ", designation = " + designation + "}";
    }
}