package be.hers.pi.comprendre_et_parler.models;
import java.util.Objects;

public class City {
    private int id;
    private String designation;
    private int postalCode;

    /**
        Constructor of a City Object
        @param id : represent the id
        @param d : represent the designation
        @param pC : represent the postal code
     */
    public City(int id,String d, int pC ){
        this.id = id;
        this.designation = d;
        this.postalCode = pC;
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
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param designation : city name
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * @param postalCode : city postal code
     */
    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return city information in a string
     */
    @Override
    public String toString() {
        return id + " - " + designation + " (" + postalCode + ")";
    }

    /**
     * Compare if two cities are the same
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
            City city = (City) o;
            return city.id == this.id
                    && city.designation.equals(this.designation)
                    && city.postalCode == this.postalCode;
        } catch (ClassCastException e) {
            return false;
        }
    }

    /**
     * @return hashcode of the city
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, designation, postalCode);
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
        if (postalCodeComparison < 0) {
            return -1;
        } else if (postalCodeComparison > 0) {
            return 1;
        } else {
            int designationComparison = this.designation.compareTo(city.designation);
            if (designationComparison < 0) {
                return -1;
            } else if (designationComparison > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }


}
