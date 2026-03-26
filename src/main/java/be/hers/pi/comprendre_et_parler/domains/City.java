package be.hers.pi.comprendre_et_parler.domains;

public class City {
    private String id;
    private String designation;
    private String postalCode;

    /**
        Constructor of a City Object
        @param id : represent the id
        @param d : represent the designation
        @param pC : represent the postal code
     */
    public City(String id,String d, String pC ){
        this.id = id;
        this.designation = d;
        this.postalCode = pC;
    }

    /**
        @return this.postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
        @return this.designation
     */
    public String getDesignation() {
        return designation;
    }

    /**
        @return this.id
     */
    public String getId() {
        return id;
    }
}
