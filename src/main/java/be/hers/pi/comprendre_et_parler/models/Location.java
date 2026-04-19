package be.hers.pi.comprendre_et_parler.models;

public class Location {
    private int id=0;
    private String designation;
    private String city;
    private String street;
    private String room;
    private String streetNumber;
    private int box=0;

    /**
        Constructor of a Location Object
        @param box : represent the box
        @param designation : represent designation
        @param city : represent the city name
        @param street : represent the street name
        @param streetNum : represent the street number
     */
    public Location(int id,String d, City c, String s, String sN, int box){
        if(id > 0) this.id = id;
        this.designation = d;
        this.city = new City(c);
        this.street = s;
        this.streetNumber = sN;
        if(box > 0) this.box = box;
    }

    /**
     * Copy constructor of a Location Object
     * @param l
     */
    public Location(Location l){
        this.id = l.id;
        this.designation = l.designation;
        this.city = new City(l.city);
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
        return new City(city);
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
        this.city = new City(city);
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
     * @return location information in a string
     */
    public String toString(){
        return "Location{id=" + id + ", designation=" + designation + ", city=" + city + ", street=" + street +
                ", streetNumber=" + streetNumber + ", box=" + box + "}";
    }

    /**
     * Compare if two locations are the same
     * @param location
     * @post location is unchanged
     * @return true if location and this are the same, else false
     */
    public boolean equals(Location location) {
        if (this == location) return true;
        if (location == null) return false;
        return location.id == this.id
                && location.designation.equals(this.designation)
                && location.city.equals(this.city)
                && location.street.equals(this.street)
                && location.streetNumber.equals(this.streetNumber)
                &&  location.box == this.box;
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
