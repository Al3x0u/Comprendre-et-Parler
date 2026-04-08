package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class AcademicSkill {
    private int id;
    private String designation;

    /**
        Constructor of a AcademicSkill Object
        @param id : represent id
        @param designation : represent designation
    */
    public AcademicSkill(int id, String designation) {
        this.id = id;
        this.designation = designation;
    }

    /**
        @return this.id
     */
    public int getId() {
        return id;
    }

    /**
        @return this.designation
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
     * Compare this AcademicSkill with another AcademicSkill for equality
     * @param other the AcademicSkill object to compare with
     * @return true if both AcademicSkill objects have identical id and designation
     */
    public boolean equals(AcademicSkill other) {
        return false;
    }

    /**
     * Return a String representation of the AcademicSkill containing all fields
     * @return formatted string with id and designation
     */
    @Override
    public String toString() {
        return null;
    }
}
