package by.roman.worldradio0.business_logic.data.models;

public class UserRequest {
    private final String login;
    private final String password;
    public UserRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public String getLogin() {
        return login;
    }
}
