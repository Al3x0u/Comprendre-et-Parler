package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class City {
    private int id = -1;
    private String designation;
    private int postalCode = 1000;

    /**
     * Empty constructor of a City
     */
    public City() {}

    /**
     * Constructor of a City
     * @param id represents the id
     * @param designation represents the designation
     * @param postalCode represents the postal code
     */
    public City(int id, String designation, int postalCode) {
        if (id >= 0) this.id = id;
        this.designation = designation;
        if (postalCode >= 1000 && postalCode < 10000) this.postalCode = postalCode;
    }

    /**
     * Constructor of a City without id
     * @param designation represents the designation
     * @param postalCode represents the postal code
     */
    public City(String designation, int postalCode ){
        this(-1, designation, postalCode);
    }

    /**
     * Copy constructor of a City
     * @param other the City to copy, must not be null
     */
    public City(City other) {
        this(other.id, other.designation, other.postalCode);
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
     * @return this.postalCode
     */
    public int getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode represents the new postal code
     */
    public void setPostalCode(int postalCode) {
        if (postalCode >= 1000 && postalCode < 10000) this.postalCode = postalCode;
    }

    /**
     * Compare this City with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical designation and postalCode
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;

        City other = (City) o;
        return Objects.equals(designation, other.designation) && Objects.equals(postalCode, other.postalCode);
    }

    /**
     * Computes the hash code of this City
     * two City objects that are equal according to equals() will have the same hash code
     * @return an integer hash code representing this City (id is not taken into account)
     */
    @Override
    public int hashCode() {
        return Objects.hash(designation, postalCode);
    }

    /**
     * Compare 2 cities based on the postal code and the city name
     * @param city the City object to compare with
     * @post city is unchanged
     * @return 0 if this == city based on postal code and city name,
     *         1 if this > city based on postal code and city name,
     *         else -1
     */
    public int compareTo(City city) {
        if (this == city) return 0;

        int postalCodeComparison = Integer.compare(this.postalCode, city.postalCode);
        if (postalCodeComparison != 0)
            return postalCodeComparison;
        return this.designation.compareTo(city.designation);
    }

    /**
     * Return a String representation of this City containing all fields
     * @return formatted string with id, designation and postalCode
     */
    @Override
    public String toString() {
        return "City{id = " + id + ", designation = " + designation + ", postalCode = " + postalCode + "}";
    }
}
