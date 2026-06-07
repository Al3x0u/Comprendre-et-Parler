package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class Location {
    private int id = -1;
    private String designation;
    private City city;
    private String street;
    private String streetNumber;
    private int box = 0;

    /**
     * Empty constructor of a Location
     */
    public Location() {}

    /**
     Constructor of a Location
     @param id represent the id
     @param designation represent the designation
     @param city represent the City
     @param street represent the street name
     @param streetNumber represent the street number
     @param box represent the box
     */
    public Location(int id, String designation, City city, String street, String streetNumber, int box){
        if (id > 0) this.id = id;
        this.designation = designation;
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        if (box > 0) this.box = box;
    }

    /**
     Constructor of a Location without id
     @param designation represent the designation
     @param city represent the City object
     @param street represent the street name
     @param streetNumber represent the street number
     @param box represent the box
     */
    public Location(String designation, City city, String street, String streetNumber, int box){
        this(-1, designation, city, street, streetNumber, box);
    }

    /**
     * Copy constructor of a Location
     * @param other the Location to copy, must not be null
     */
    public Location(Location other){
        this(other.id, other.designation, new City(other.city), other.street, other.streetNumber, other.box);
    }

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id represent the new id
     */
    public void setId(int id) {
        if (id >= 0) this.id = id;
    }

    /**
     * @return this.id
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * @param designation represent the new designation
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * @return this.city
     */
    public City getCity() {
        return city;
    }

    /**
     * @param city represent the new city
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * @return this.street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street represent the new street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @return this.streetNumber
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * @param streetNumber represent the new streetNumber
     */
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * @return this.box
     */
    public int getBox() {
        return box;
    }

    /**
     * @param box represent the new box
     */
    public void setBox(int box) {
        if (box >= 0) this.box = box;
    }

    /**
     * Compare this Location with another Object for equality
     * @param o the Object to compare with
     * @return true if both objects have identical designation,
     * city, street, streetNumber and box
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location other = (Location) o;
        return Objects.equals(designation, other.designation) && Objects.equals(city, other.city)
                && Objects.equals(street, other.street) && Objects.equals(streetNumber, other.streetNumber)
                && Objects.equals(box, other.box);
    }

    /**
     * Computes the hash code of this Location
     * two AcademicSkill objects that are equal according to equals() will have the same hash code
     * @return an integer hash code representing this AcademicSkill (id is not taken into account)
     */
    @Override
    public int hashCode() {
        return Objects.hash(designation, city, street, streetNumber, box);
    }

    /**
     * Compare 2 locations based on the city
     * @param l the Location to compare with
     * @post l is unchanged
     * @return 0 if this == l based on city,
     *         1 if this > l based on city,
     *         else -1
     */
    public int compareTo(Location l) {
        if (this == l) return 0;
        return this.city.compareTo(l.city);
    }

    /**
     * Return a String representation of the Location containing all fields
     * @return formatted string with id, designation, city, street, streetNumber and box
     */
    public String toString(){
        return "Location{id = " + id + ", designation = " + designation + ", city = " + city +
                ", street = " + street + ", streetNumber = " + streetNumber + ", box = " + box + "}";
    }
}
