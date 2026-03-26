package be.hers.pi.comprendre_et_parler.domains;

import java.time.LocalDate;

public class Interpreter extends User{
    private Boolean isManager;
    private int hourQuotaWeek;
    private int hourQuotaYear;

    /*
        Constructor of an Interpreter object
        @param id represent the id
        @param isManager is True if manager else false
        @param hQW represent the hour quota per week
        @param hQY represent the hour quota per year
        @param login represent the login
        @param firstName represent the firstname of the interpreter
        @param lastName represent he lastname of the interpreter
        @param birthdate represent the birthdate of the interpreter
        @param hashedPassword represent the hashed password of the interpreter
        @param email represent the email of the interpreter
        @param phoneNumber represent the phone number of the interpreter
     */
    public Interpreter(String id, Boolean isManager, int hQW, int hQY,String login, String firstName, String lastName,
                       LocalDate birthDate, String hashedPassword, String email, String phoneNumber) {
        super(id,login, firstName, lastName, birthDate, hashedPassword, email, phoneNumber);
        this.isManager = isManager;
        this.hourQuotaWeek = hQW;
        this.hourQuotaYear = hQY;
    }

    /*
        @return this.isManager
     */
    public Boolean getManager() {
        return isManager;
    }

    /*
        @return this.hourQuotaWeek
     */
    public int getHourQuotaWeek() {
        return hourQuotaWeek;
    }

    /*
        @return this.hourQuotaYear
     */
    public int getHourQuotaYear() {
        return hourQuotaYear;
    }
}
