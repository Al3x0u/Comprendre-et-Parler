package be.hers.pi.comprendre_et_parler.models;

import java.time.LocalDate;

public class Interpreter extends AppliUser{
    private int hourQuotaWeek;
    private int hourQuotaYear;
    private String transportMode;

    /**
     * Constructor of an Interpreter object
     *
     * @param id             represent the id
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
     */
    public Interpreter(String id, int hQW, int hQY,String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email, String phoneNumber,String transportMode) {
        super(id,login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
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
     */
    public void setHourQuotaWeek(int newHourQuotaWeek){
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
     */
    public void setHourQuotaYear(int newHourQuotaYear){
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
}
