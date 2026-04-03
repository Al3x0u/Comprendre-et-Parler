package be.hers.pi.comprendre_et_parler.models;

public class JobSkill {
    private int id;
    private String designation;


    /**
        Constructor of a JobSkill Object
        @param id : represent the id
        @param designation : represent the designation
     */
    public JobSkill(int id, String designation) {
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
