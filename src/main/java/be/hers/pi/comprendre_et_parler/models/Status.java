package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class Status {
    private int id;
    private String designation;
    private int hourQuota;

    /**
     * Constructor of a Status object
     * @param id the unique identifier of the status
     * @param designation the name or description of the status
     * @param hourQuota the associated hour quota
     */
    public Status(int id, String designation, int hourQuota) {
        this.id = id;
        this.designation = designation;
        this.hourQuota = hourQuota;
    }

    /**
     * @param other the object to copy
     */
    public Status(Status other) {
        this.id = other.getId();
        this.designation = other.getDesignation();
        this.hourQuota = other.getHourQuota();
    }

    /**
     * @return the unique identifier of the status
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name or description of the status
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * @param designation the new name or description of the status
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * @return the associated hour quota
     */
    public int getHourQuota() {
        return hourQuota;
    }

    /**
     * @param id the new id
     */
    public void setId(int id) { this.id = id; }

    /**
     * @param hourQuota the new associated hour quota
     */
    public void setHourQuota(int hourQuota) {
        this.hourQuota = hourQuota;
    }

    /**
     * Compare this Status with another Status for equality
     * @param other the Status object to compare with
     * @return true if both objects are of type Status and have identical attributes except for id
     */
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Mission)) return false;
        Status o = (Status) other;
        return (designation.equals(o.getDesignation()) && Objects.equals(hourQuota, o.getHourQuota()));
    }

    /**
     * Return a String representation of the Status containing all fields
     * @return formatted string with id, designation and hourQuota
     */
    @Override
    public String toString() {
        return "Status \"" + designation + "\" (id: " + id + ", hourQuota : " + hourQuota + ")";
    }
}