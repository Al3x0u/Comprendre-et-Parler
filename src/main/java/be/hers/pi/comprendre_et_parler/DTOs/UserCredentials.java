package be.hers.pi.comprendre_et_parler.DTOs;

public class UserCredentials {
    private final String login;
    private final String password;
    private final String loginUrl;

    public UserCredentials(String login, String password, String loginUrl) {
        this.login = login;
        this.password = password;
        this.loginUrl = loginUrl;
    }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getLoginUrl() { return loginUrl; }
}
