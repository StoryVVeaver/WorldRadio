package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.User;

public interface UserRepository {
    User getUserData();
    boolean isTableEmpty();
    boolean setUserAvatar(String avatar);
    boolean entrance(User user);
    int getUserInSystem();
    void setUserInSystem(int id);
    void useradd(UserDTO dto);
    void removeUser();
    boolean exit();
}
