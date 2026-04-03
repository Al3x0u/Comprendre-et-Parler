package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class City {
    private String id;
    private String designation;
    private String postalCode;

    /**
        Constructor of a City Object
        @param id : represent the id
        @param d : represent the designation
        @param pC : represent the postal code
     */
    public City(String id,String d, String pC ){
        this.id = id;
        this.designation = d;
        this.postalCode = pC;
    }

    /**
        @return this.postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
        @return this.designation
     */
    public String getDesignation() {
        return designation;
    }

    /**
        @return this.id
     */
    public String getId() {
        return id;
    }

    /**
     * Compare this City with another City for equality
     * @param other the City object to compare with
     * @return true if both City objects have identical id, designation and postalCode
     */
    public boolean equals(City other) {
        return (id == other.id && designation == other.designation && postalCode == other.postalCode);
    }

    /**
     * Return a String representation of the City containing all fields
     * @return formatted string with id, designation and postalCode
     */
    @Override
    public String toString() {
        return null;
    }
}
