package be.hers.pi.comprendre_et_parler.models;

import java.util.Objects;

public class City {
    private int id=0;
    private String designation;
    private int postalCode=1000;

    /**
        Constructor of a City Object
        @param id : represent the id
        @param d : represent the designation
        @param pC : represent the postal code
     */
    public City(int id,String d, int pC ){
        if(id > 0) this.id = id;
        this.designation = d;
        if(pC >= 1000 && pC <= 10000) this.postalCode = pC;
    }

    /**
     * Copy constructor of a City Object
     * @param city
     */
    public City(City city) {
        this.id = city.id;
        this.designation = city.designation;
        this.postalCode = city.postalCode;
    }

    /**
     * @return this.id
     */
    public int getId() {
        return id;
    }

    /**
     * @return this.designation
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * @return this.postalCode
     */
    public int getPostalCode() {
        return postalCode;
    }


    /**
     * @param id : city id
     * @post if id >= 0, id is affected to this.id
     */
    public void setId(int id) {
       if(id >= 0) this.id = id;
    }

    /**
     * @param designation : city name
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * @param postalCode : city postal code
     * @post if 1000 <= postalCode <= 10000, postalCode is affected to this.postalCode
     */
    public void setPostalCode(int postalCode) {
        if(postalCode >= 1000 && postalCode <= 10000) this.postalCode = postalCode;
    }

    /**
     * @return city information in a string
     */
    @Override
    public String toString() {
        return "City{id=" + id + ", designation=" + designation + ", postalCode=" + postalCode + "}";
    }

    /**
     * Compare this City with another City for equality
     * @param o the City object to compare with
     * @return true if both City objects have identical designation and postalCode
     * (id is not compared), else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof City)) return false;

        City other = (City) o;
        return designation.equals(other.designation)
                && postalCode == other.postalCode;
    }

    /**
     * Computes the hash code of this City.
     * @return an integer hash code value based on designation and postalCode
     * (id is not taken into account)
     */
    @Override
    public int hashCode() {
        return Objects.hash(designation, postalCode);
    }

    /**
     * Compare 2 cities based on the postal code and the city name
     * @param city
     * @post city is unchanged
     * @return 0 if this == city based on postal code and city name,
     *         1 if this > city based on postal code and city name,
     *         else -1
     */
    public int compareTo(City city) {
        if (this == city) return 0;

        int postalCodeComparison = Integer.compare(this.postalCode, city.postalCode);
        if (postalCodeComparison != 0) {
            return postalCodeComparison;
        }
        return this.designation.compareTo(city.designation);
    }


}
