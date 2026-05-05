package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class AcademicSkill {
    private  int id = -1;
    private String designation;

    /**
     * Constructor of a AcademicSkill
     * @param id represent the id
     * @param designation represent the designation
    */
    public AcademicSkill(int id, String designation) {
        if (id >= 0)
            this.id = id;
        this.designation = designation;
    }

    /**
     * Constructor of a AcademicSkill without id
     * @param designation represent the designation
     */
    public AcademicSkill(String designation) {
       this(-1, designation);
    }

    /**
     * Copy constructor of a AcademicSkill
     * @param other the AcademicSkill to copy, must not be null
     */
    public AcademicSkill(AcademicSkill other) {
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
        if (id > 0)
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
     * Compare this AcademicSkill with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical designation
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AcademicSkill)) return false;

        AcademicSkill other = (AcademicSkill) o;
        return Objects.equals(designation, other.designation);
    }

    /**
     * Computes the hash code of this AcademicSkill
     * two AcademicSkill objects that are equal according to equals() will have the same hash code
     * @return an integer hash code representing this AcademicSkill (id is not taken into account)
     */
    @Override
    public int hashCode() {
        return Objects.hash(designation);
    }

    /**
     * Return a String representation of this AcademicSkill containing all fields
     * @return formatted string with id and designation
     */
    @Override
    public String toString() {
        return "AcademicSkill{id = " + id + ", designation = " + designation + "}";
    }
}
