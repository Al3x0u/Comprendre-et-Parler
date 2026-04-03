package be.hers.pi.comprendre_et_parler.models;

public class Location {
    private String id;
    private String designation;
    private City city;
    private String street;
    private String streetNumber;
    private String box;

    /**
     Constructor of a Location Object
     @param id : represent id
     @param d : represent designation
     @param c : represent the City object
     @param s : represent the street name
     @param sN : represent the street number
     @param box : represent the new box
     */
    public Location(String id, String d, City c, String s, String sN, String box){
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
    public String getId() {
        return id;
    }

    /**
     * @param id represent the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return a String which contains the box of the Location (can be null)
     */
    public String getBox() {
        return box;
    }

    /**
     * @param box a new String which contains the box of the Location (can be null)
     */
    public void setBox(String box) {
        this.box = box;
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