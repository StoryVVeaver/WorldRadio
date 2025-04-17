package by.roman.worldradio0.business_logic.data.dto;

import by.roman.worldradio0.business_logic.data.models.User;

public class UserDTO {
    private int id;
    private String login;
    private String password;
    private String playing;
    private int inSystem;
    public User toModel(){
        return new User(id,login,password,playing,inSystem);
    }
    public UserDTO fromModel(User user){
        UserDTO dto = new UserDTO();
        dto.id = user.getId();
        dto.login = user.getLogin();
        dto.password = user.getPassword();
        dto.playing = user.getPlaying();
        dto.inSystem = user.getInSystem();
        return dto;
    }
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
        return playing;
    }
    public int getInSystem() {
        return inSystem;
    }
}
