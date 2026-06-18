package be.hers.pi.comprendre_et_parler.DTO;

public class UserCredentials {
    private final String firstName;
    private final String login;
    private final String password;
    private final String loginUrl = "/login";
    private final String email;

    /**
     * Constructor of a UserCredentials
     * @param firstName represent the firstname
     * @param login represent the login
     * @param password represent the password
     * @param email represent the email
     */
    public UserCredentials(String firstName, String login, String password, String email) {
        this.firstName = firstName;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    /**
     * @return this.firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return this.login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @return this.password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return this.loginUrl
     */
    public String getLoginUrl() {
        return loginUrl;
    }

    /**
     * @return this.email
     */
    public String getEmail() {
        return email;
    }
}