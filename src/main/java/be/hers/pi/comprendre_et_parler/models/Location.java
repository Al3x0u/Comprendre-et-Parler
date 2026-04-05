package be.hers.pi.comprendre_et_parler.models;

public class Location {
    private int id;
    private String designation;
    private String city;
    private String street;
    private String room;
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
    public Location(int id,String designation, String city, String street, String room,
                    String streetNum, int box){
        this.id = id;
        this.designation = designation;
        this.city = city;
        this.street = street;
        this.room = room;
        streetNumber = streetNum;
        this.box = box;

    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public void setDesignation(final String designation) {
        this.designation = designation;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public String getRoom() {
        return this.room;
    }

    public void setRoom(final String room) {
        this.room = room;
    }

    public void setStreetNumber(final String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public void setBox(final int box) {
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

