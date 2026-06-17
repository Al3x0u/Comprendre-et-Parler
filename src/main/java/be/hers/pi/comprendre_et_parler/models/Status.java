package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class Status {
    private int id = -1;
    private String designation;
    private int hourQuota = 0;

    /**
     * Empty constructor of a Status
     */
    public Status() {}

    /**
     * Constructor of a Status
     * @param id represents the id
     * @param designation represents the designation
     * @param hourQuota represents the hourQuota
     */
    public Status(int id, String designation, int hourQuota) {
        if (id >= 0) this.id = id;
        this.designation = designation;
        if (hourQuota >= 0) this.hourQuota = hourQuota;
    }

    /**
     * Constructor of a Status without id
     * @param designation represents the designation
     * @param hourQuota represents the hourQuota
     */
    public Status(String designation, int hourQuota) {
        this(-1, designation, hourQuota);
    }

    /**
     * Copy constructor of a Status
     * @param other the Status to copy, must not be null
     */
    public Status(Status other) {
        this(other.id, other.designation, other.hourQuota);
    }

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id represents the new id
     */
    public void setId(int id) {
        if (id >= 0) this.id = id;
    }

    /**
     * @return this.designation
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * @param designation represents the new designation
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * @return this.hourQuota
     */
    public int getHourQuota() {
        return hourQuota;
    }

    /**
     * @param hourQuota represents the new hourQuota
     */
    public void setHourQuota(int hourQuota) {
        if (hourQuota >= 0) this.hourQuota = hourQuota;
    }

    /**
     * Compare this Status with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical designation and hourQuota
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Status)) return false;

        Status other = (Status) o;
        return (Objects.equals(designation, other.designation) && Objects.equals(hourQuota, other.hourQuota));
    }

    /**
     * Compares two Status lexicographically according to their designations
     * @param s The second status to compare to this
     * @return The result is a negative integer if this Status.designation lexicographically precedes the other Status.designation.
     * The result is a positive integer if this Status.designation lexicographically follows the other Status.designation.
     * The result is zero if the Status.designation.equals(other Status.designation) method would return true.
     */
    public int compareTo(Status s) {
        if (this == s) return 0;

        return designation.compareTo(s.designation);
    }

    /**
     * Computes the hash code of this Status
     * two Status objects that are equal according to equals() will have the same hash code
     * @return an integer hash code representing this Status (id is not taken into account)
     */
    @Override
    public int hashCode() {
        return Objects.hash(designation, hourQuota);
    }

    /**
     * Return a String representation of this Status containing all fields
     * @return formatted string with id, designation and hourQuota
     */
    @Override
    public String toString() {
        return "Status{id = " + id + ", designation = " + designation + ", hourQuota = " + hourQuota +"}";
    }
}