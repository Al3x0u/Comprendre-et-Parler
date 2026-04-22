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
        if(id >= 0){
            this.id = id;
        }else{
            this.id = -1;
        }
        this.designation = designation;
    }

    /**
     Constructor of a JobSkill Object
     @param designation : represent designation
     */
    public JobSkill(String designation) {
        this.id = -1;
        this.designation = designation;
    }

    /**
     * Copy constructor of a JobSkill Object
     * @param other represent the JobSkill object
     */
    public JobSkill(JobSkill other) {
        this.id = other.id;
        this.designation = other.designation;
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
        if(id >= 0){
            this.id = id;
        }
    }

    /**
     * @param designation represent the new designation
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * Compare this JobSkill with another Object for equality
     * @param o the Object  to compare with
     * @return true if both Object objects have identical designation
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobSkill)) return false;

        JobSkill other = (JobSkill) o;
        return Objects.equals(designation, other.designation);
    }

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
        return "JobSkill{id=" + id + ", designation=" + designation + "}";
    }
}