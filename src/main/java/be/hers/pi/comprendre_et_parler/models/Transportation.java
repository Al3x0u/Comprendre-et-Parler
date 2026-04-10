package be.hers.pi.comprendre_et_parler.models;

public class Transportation {
    private int id;
    private String designation;

    /**
     * Constructor of a Transportation object
     * @param id the unique identifier of the transportation
     * @param designation the name or description of the transportation
     */
    public Transportation(int id, String designation) {
        this.id = id;
        this.designation = designation;
    }

    /**
     * @return the unique identifier of the transportation
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name or description of the transportation
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * @param designation the new name or description of the transportation
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * Compare this Transportation with another Transportation for equality
     * @param other the Transportation object to compare with
     * @return true if both Transportation objects have identical id and designation
     */
    public boolean equals(Transportation other) {
        return (id == other.id && designation == other.designation);
    }

    /**
     * Return a String representation of the Transportation containing all fields
     * @return formatted string with id and designation
     */
    @Override
    public String toString() {
        return null;
    }
}