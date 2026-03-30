package be.hers.pi.comprendre_et_parler.models;

public class Status {
    private int id;
    private String designation;
    private int hourQuota;

    /**
        Constructor of a Status object
        @param id represent the id
        @param designation represent the designation
        @param hourQuota represent the quota of hours
     */
    public Status(int id, String designation, int hourQuota) {
        this.id = id;
        this.designation = designation;
        this.hourQuota = hourQuota;
    }

    /**
     * @return this.id
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
     * @param designation represent the new designation
     */
    public void setDesignation(String designation){
        this.designation = designation;
    }
    /**
        @return this.hourQuota
     */
    public int getHourQuota() {
        return hourQuota;
    }

    /**
     * @param newQuota represent the new quota
     */
    public void setHourQuota(int newQuota){
        this.hourQuota = newQuota;
    }
}
