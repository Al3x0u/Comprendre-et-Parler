package be.hers.pi.comprendre_et_parler.model;

public class Status {
    private int id;
    private String designation;
    private int hourQuota;

    public Status(int id, String designation, int hourQuota) {
        this.id = id;
        this.designation = designation;
        this.hourQuota = hourQuota;
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

    public int getHourQuota() {
        return hourQuota;
    }

    public void setHourQuota(int hourQuota) {
        this.hourQuota = hourQuota;
    }

    public boolean equals(Status other) {
        return (id == other.id && designation == other.designation && hourQuota == other.hourQuota);
    }
}
