package be.hers.pi.comprendre_et_parler.models;

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
}
