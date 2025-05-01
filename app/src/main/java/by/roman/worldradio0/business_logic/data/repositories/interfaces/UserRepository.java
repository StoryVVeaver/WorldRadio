package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.User;

public interface UserRepository {
    User getUserData();
    boolean entrance(User user);
    int getUserInSystem();
    String getPlayingUUID();
    void setPlayingUUID(String UUID);
    void useradd(UserDTO dto);
    void removeUser();
    void exit();
}
