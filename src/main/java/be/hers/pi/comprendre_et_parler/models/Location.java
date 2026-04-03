package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class Location {
    private int id;
    private String designation;
    private City city;
    private String street;
    private String streetNumber;
    private int box;

    /**
        Constructor of a Location Object
        @param id : represent id
        @param d : represent designation
        @param c : represent the city name
        @param s : represent the street name
        @param sN : represent the street number
     */
    public Location(int id,String d, City c, String s, String sN, int box){
        this.id = id;
        this.designation = d;
        this.city = c;
        this.street = s;
        this.streetNumber = sN;
        this.box = box;
    }

    /**
     * Copy constructor of a Location Object
     * @param l
     */
    public Location(Location l){
        this.id = l.id;
        this.designation = l.designation;
        this.city = l.city;
        this.street = l.street;
        this.streetNumber = l.streetNumber;
        this.box = l.box;
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
     * @return a String which contains the box of the Location
     */
    public int getBox() {
        return box;
    }

    /**
     * @param id : location id
     */
    public void setId(int id) {
        this.id = id;
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
     */
    public void setBox(int box) {
        this.box = box;
    }

    /**
     * @return location information in a string
     */
    public String toString(){
        return "Location{id=" + id + ", designation=" + designation + ", city=" + city + ", street=" + street +
                ", streetNumber=" + streetNumber + ", box=" + box + "}";
    }

    /**
     * Compare if two locations are the same
     * @param o
     * @post o is unchanged
     * @return true if o and this are the same, else false
     * @throws ClassCastException
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        try {
            Location location = (Location) o;
            return location.id == this.id
                    && location.designation.equals(this.designation)
                    && location.city.equals(this.city)
                    && location.street.equals(this.street)
                    && location.streetNumber.equals(this.streetNumber)
                    &&  location.box == this.box;
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * @return hashcode of the location
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, designation, city, street, streetNumber, box);
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

