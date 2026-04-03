package be.hers.pi.comprendre_et_parler.models;

public class AcademicSkill {
    private String id;
    private String designation;

    /**
        Constructor of a AcademicSkill Object
        @param id : represent id
        @param designation : represent designation
    */
    public AcademicSkill(String id, String designation) {
        this.id = id;
        this.designation = designation;
    }

    /**
        @return this.id
     */
    public String getId() {
        return id;
    }

    /**
        @return this.designation
     */
    public String getDesignation() {
        return designation;
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
