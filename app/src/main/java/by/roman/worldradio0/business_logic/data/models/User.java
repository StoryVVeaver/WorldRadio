package by.roman.worldradio0.business_logic.data.models;

public class User {
    private final int id;
    private final String login;
    private final String password;
    private final String playing;
    private final int inSystem;

    public User(int id, String login, String password,String playing,int inSystem) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.playing = playing;
        this.inSystem = inSystem;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
    public String getPlaying() {
        return  playing;
    }
    public int getInSystem() {
        return inSystem;
    }
}
