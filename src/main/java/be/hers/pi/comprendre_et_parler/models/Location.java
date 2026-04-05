package be.hers.pi.comprendre_et_parler.models;

public class Location {
    private String designation;
    private String city;
    private String street;
    private String streetNumber;
    private int box;

    /**
        Constructor of a Location Object
        @param box : represent the box
        @param designation : represent designation
        @param city : represent the city name
        @param street : represent the street name
        @param streetNum : represent the street number
     */
    public Location(String designation, String city, String street,
                    String streetNum, int box){
        this.designation = designation;
        this.city = city;
        this.street = street;
        streetNumber = streetNum;
        this.box = box;

    }

    /**
        @return a String which contains the city of the Location
     */
    public String getCity() {
        return city;
    }

    /**
        @return a String which contains the designation of the Location
     */
    public String getDesignation() {
        return designation;
    }

    /**
        @return a String which contains the street number of the Location
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
        @return a String which contains the street of the Location
     */
    public String getStreet() {
        return street;
    }

    /**
        @return this.box
     */
    public int getBox() {
        return box;
    }
}

