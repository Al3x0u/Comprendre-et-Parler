package be.hers.pi.comprendre_et_parler.domains;

public class Location {
    private String id;
    private String designation;
    private String city;
    private String street;
    private String streetNumber;

    /**
        Constructor of a Location Object
        @param id : represent id
        @param d : represent designation
        @param c : represent the city name
        @param s : represent the street name
        @param sN : represent the street number
     */
    public Location(String id,String d, String c, String s, String sN){
        this.id = id;
        this.designation = d;
        this.city = c;
        this.street = s;
        this.streetNumber = sN;

    }

    /**
        @return a String who contains the city of the Location
     */
    public String getCity() {
        return city;
    }

    /**
        @return a String who contains the designation of the Location
     */
    public String getDesignation() {
        return designation;
    }

    /**
        @return a String who contains the street number of the Location
     */
    public String getStreetNumber() {
        return streetNumber;
    }

    /**
        @return a String who contains the street of the Location
     */
    public String getStreet() {
        return street;
    }

    /**
        @return this.id
     */
    public String getId() {
        return id;
    }
}

