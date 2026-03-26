package be.hers.pi.comprendre_et_parler.domains;

public class Status {
    private String id;
    private String designation;
    private int hourQuota;

    /**
        Constructor of a Status object
        @param id represent the id
        @param designation represent the designation
        @param hourQuota represent the quota of hours
     */
    public Status(String id, String designation, int hourQuota) {
        this.id = id;
        this.designation = designation;
        this.hourQuota = hourQuota;
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
        @return this.hourQuota
     */
    public int getHourQuota() {
        return hourQuota;
    }
}
