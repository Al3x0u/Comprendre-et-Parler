package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class Location {
    private int id=-1;
    private String designation;
    private City city;
    private String street;
    private String streetNumber;
    private int box=0;

    /**
     Constructor of a Location Object
     @param id : represent id
     @param d : represent designation
     @param c : represent the City object
     @param s : represent the street name
     @param sN : represent the street number
     @param box : represent the new box
     */
    public Location(int id, String d, City c, String s, String sN, int box){
        if(id > 0) this.id = id;
        this.designation = d;
        this.city = c;
        this.street = s;
        this.streetNumber = sN;
        if(box > 0) this.box = box;
    }


    /**
     Constructor of a Location Object without id
     @param d : represent designation
     @param c : represent the City object
     @param s : represent the street name
     @param sN : represent the street number
     @param box : represent the new box
     */
    public Location(String d, City c, String s, String sN, int box){
        this(-1, d, c, s, sN, box);
    }

    /**
     * Copy constructor of a Location Object
     * @param l the Location object to copy
     */
    public Location(Location l){
        this(l.id, l.designation, new City(l.city), l.street, l.streetNumber, l.box);
    }

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @return a String which contains the designation of the Location
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * @return a String which contains the city of the Location
     */
    public City getCity() {
        return city;
    }

    /**
     * @return a String which contains the street of the Location
     */
    public String getStreet() {
        return street;
    }

    /**
     * @return a String which contains the street number of the Location
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * @return an integer representing the box of the Location
     */
    public int getBox() {
        return box;
    }

    /**
     * @param id : location id
     * @post if id >= 0, id is affected to this.id
     */
    public void setId(int id) {
        if(id >= 0) this.id = id;
    }

    /**
     * @param designation : location designation
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * @param city : location city
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     * @param street : location street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @param streetNumber : location street number
     */
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     * @param box : location box
     * @post if box >= 0, box is affected to this.box
     */
    public void setBox(int box) {
        if(box >= 0) this.box = box;
    }

    /**
     * Return a String representation of the Location containing all fields
     * @return formatted string with id, designation, city, street, streetNumber and box
     */
    public String toString(){
        return "Location{id=" + id + ", designation=" + designation + ", city=" + city + ", street=" + street +
                ", streetNumber=" + streetNumber + ", box=" + box + "}";
    }

    /**
     * Compare this Location with another Location for equality
     * @param o the Location object to compare with
     * @return true if both Location objects have identical designation, city, street, streetNumber and box
     * (id is not compared), else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location other = (Location) o;
        return designation.equals(other.designation)
                && city.equals(other.city)
                && street.equals(other.street)
                && streetNumber.equals(other.streetNumber)
                && box == other.box;
    }

    /**
     * Computes the hash code of this Location.
     * @return an integer hash code value based on designation, city, street, streetNumber and box
     * (id is not taken into account)
     */
    @Override
    public int hashCode() {
        return Objects.hash(designation, city, street, streetNumber, box);
    }

    /**
     * Compare 2 locations based on the city
     * @param l
     * @post l is unchanged
     * @return 0 if this == l based on city,
     *         1 if this > l based on city,
     *         else -1
     */
    public int compareTo(Location l) {
        if (this == l) return 0;
        return this.city.compareTo(l.city);
    }
}
