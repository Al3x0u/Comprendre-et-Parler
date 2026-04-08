package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class JobSkill {
    private int id;
    private String designation;

    /**
     * Constructor of a JobSkill Object
     * @param id represent the id
     * @param designation represent the designation
     */
    public JobSkill(int id, String designation) {
        this.id = id;
        this.designation = designation;
    }

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @return this.designation
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * @param id represent the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param designation represent the new designation
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * Compare this JobSkill with another JobSkill for equality
     * @param other the JobSkill object to compare with
     * @return true if both JobSkill objects have identical id and designation
     */
    public boolean equals(JobSkill other) {
        return false;
    }

    /**
     * Return a String representation of the JobSkill containing all fields
     * @return formatted string with id and designation
     */
    @Override
    public String toString() {
        return null;
    }
}