package by.story_weaver.worldradiomonitoring.logic.models;

public class User {
    private final int id;
    private final String login;
    private final String password;
    private final String playing;
    private String avatar;
    private final int inSystem;

    public User(int id, String login, String password,String playing,int inSystem, String avatar) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.playing = playing;
        this.inSystem = inSystem;
        this.avatar = avatar;
    }
    @Override
    public String toString(){
        return "id: " + id + "\n" +
                "login: " + login + "\n" +
                "pass: " + password + "\n" +
                "avatar: " + avatar;
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
    public String getAvatar(){
        return avatar;
    }

    public void setAvatar(String avatar){
        this.avatar = avatar;
    }
}
