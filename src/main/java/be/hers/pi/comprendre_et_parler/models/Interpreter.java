package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;

public class Interpreter extends AppliUser{
    private int hourQuotaWeek;
    private int hourQuotaYear;
    private String transportMode;

    /**
     * Constructor of an Interpreter object
     * @param hQW            represent the hour quota per week
     * @param hQY            represent the hour quota per year
     * @param login          represent the login
     * @param firstName      represent the firstname of the interpreter
     * @param lastName       represent he lastname of the interpreter
     * @param birthDate      represent the birthdate of the interpreter
     * @param hashedPassword represent the hashed password of the interpreter
     * @param email          represent the email of the interpreter
     * @param phoneNumber    represent the phone number of the interpreter
     * @param transportMode  represent the transport mode of the interpreter
     * @throws IllegalArgumentException if hQW or hQY is negative
     */
    public Interpreter(int hQW, int hQY, String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email, String phoneNumber, String transportMode) {
        super(login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        if (hQW < 0 || hQY < 0) {
            throw new IllegalArgumentException("Hour quotas cannot be negative");
        }
        this.hourQuotaWeek = hQW;
        this.hourQuotaYear = hQY;
        this.transportMode = transportMode;
    }

    /**
     @return this.hourQuotaWeek
     */
    public int getHourQuotaWeek() {
        return hourQuotaWeek;
    }

    /**
     * @param newHourQuotaWeek represent the new quota hour
     * @throws IllegalArgumentException if newHourQuotaWeek is negative
     */
    public void setHourQuotaWeek(int newHourQuotaWeek){
        if (newHourQuotaWeek < 0) {
            throw new IllegalArgumentException("Hour quota week cannot be negative");
        }
        this.hourQuotaWeek = newHourQuotaWeek;
    }

    /**
     @return this.hourQuotaYear
     */
    public int getHourQuotaYear() {
        return hourQuotaYear;
    }

    /**
     * @param newHourQuotaYear represent the new quota year
     * @throws IllegalArgumentException if newHourQuotaYear is negative
     */
    public void setHourQuotaYear(int newHourQuotaYear){
        if (newHourQuotaYear < 0) {
            throw new IllegalArgumentException("Hour quota year cannot be negative");
        }
        this.hourQuotaYear = newHourQuotaYear;
    }

    /**
     *
     * @return this.transport
     */
    public String getTransportMode() {
        return transportMode;
    }

    /**
     *
     * @param transportMode represent the new transport mode
     */
    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    /**
     * Return a String representation of the Interpreter containing all fields
     * @return formatted string with hour quotas, transport mode and AppliUser fields
     */
    @Override
    public String toString() {
        return null;
    }
}