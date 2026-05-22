package be.hers.pi.comprendre_et_parler.DTO;

public class UserCredentials {
    private final String firstName;
    private final String login;
    private final String password;
    private final String loginUrl = "/login";
    private final String email;

    public UserCredentials(String firstName, String login, String password, String email) {
        this.firstName = firstName;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public String getFirstName() { return firstName; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getLoginUrl() { return loginUrl; }
    public String getEmail() { return email;}
}
