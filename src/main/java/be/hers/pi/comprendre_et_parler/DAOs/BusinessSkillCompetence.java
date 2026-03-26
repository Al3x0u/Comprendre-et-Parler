package be.hers.pi.comprendre_et_parler.DAOs;

public class BusinessSkillCompetence {
    private String id;
    private String designation;


    /*
        Constructor of a BusinessSkillCompetence Object
        @param id : represent the id
        @param designation : represent the designation
     */
    public BusinessSkillCompetence(String id, String designation) {
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
