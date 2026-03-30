package be.hers.pi.comprendre_et_parler.models;

public class Transportation {
    private final int id;
    private String designation;

    public Transportation(int id , String designation){
        this.id = id;
        this.designation = designation;
    }

    public int getId() {
        return id;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
