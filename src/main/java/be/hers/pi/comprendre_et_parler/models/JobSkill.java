package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class JobSkill {
    private int id = -1;
    private String designation;

    /**
     * Empty constructor of a JobSkill
     */
    public JobSkill() {}

    /**
     * Constructor of a JobSkill
     * @param id represents the id
     * @param designation represents the designation
     */
    public JobSkill(int id, String designation) {
        if(id >= 0) this.id = id;
        this.designation = designation;
    }

    /**
     Constructor of a JobSkill without id
     @param designation : represents designation
     */
    public JobSkill(String designation) {
        this(-1, designation);
    }

    /**
     * Copy constructor of a JobSkill
     * @param other represents the JobSkill to copy, must not be null
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
     * @param id represents the new id
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
     * @param designation represents the new designation
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
     * Compares two JobSkill lexicographically according to their designations
     * @param j The second academic skill to compare to this
     * @return The result is a negative integer if this JobSkill.designation lexicographically precedes the other JobSkill.designation.
     * The result is a positive integer if this JobSkill.designation lexicographically follows the other JobSkill.designation.
     * The result is zero if the equals(Object) method would return true.
     */
    public int compareTo(JobSkill j) {
        if (this == j) return 0;

        return designation.compareTo(j.designation);
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