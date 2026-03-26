package be.hers.pi.comprendre_et_parler.DAOs;

public class AcademicSkillCompetence {
    private String id;
    private String designation;

    /*
        Constructor of a AcademicSkillCompetence Object
        @param id : represent id
        @param designation : represent designation
    */
    public AcademicSkillCompetence(String id, String designation) {
        this.id = id;
        this.designation = designation;
    }

    /*
        @return this.id
     */
    public String getId() {
        return id;
    }

    /*
        @return this.designation
     */
    public String getDesignation() {
        return designation;
    }
}
