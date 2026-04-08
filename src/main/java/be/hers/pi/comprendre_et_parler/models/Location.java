package be.hers.pi.comprendre_et_parler.models;

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
     @param c : represent the City object
     @param s : represent the street name
     @param sN : represent the street number
     @param box : represent the new box
     */
    public Location(int id, String d, City c, String s, String sN, int box){
        this.id = id;
        this.designation = d;
        this.city = c;
        this.street = s;
        this.streetNumber = sN;
        this.box = box;
    }

    /**
     @return a City which contains the city of the Location
     */
    public City getCity() {
        return city;
    }

    /**
     * @param city represent the new City object
     */
    public void setCity(City city) {
        this.city = city;
    }

    /**
     @return a String which contains the designation of the Location
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
     @return a String which contains the street number of the Location
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
     * @param streetNumber represent the new street number
     */
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    /**
     @return a String which contains the street of the Location
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
     @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id represent the new id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return an int which contains the box of the Location (can be null)
     */
    public int getBox() {
        return box;
    }

    /**
     * @param box a new String which contains the box of the Location (can be null)
     */
    public void setBox(int box) {
        this.box = box;
    }

    /**
     * Compare this Location with another Location for equality
     * @param other the Location object to compare with
     * @return true if both Location objects have identical id, designation, city, street, streetNumber and box
     */
    public boolean equals(Location other) {
        return (id == other.id && designation == other.designation && city == other.city &&
                street == other.street && streetNumber == other.streetNumber && box == other.box);
    }
    /**
     * Return a String representation of the Location containing all fields
     * @return formatted string with id, designation, city, street, streetNumber and box
     */
    @Override
    public String toString() {
        return null;
    }
}