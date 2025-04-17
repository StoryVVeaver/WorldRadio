package by.roman.worldradio0.business_logic.data.repositories;

import by.roman.worldradio0.business_logic.data.models.User;

public interface UserRepository {
    User getUserData();
    boolean entrance(User user);
    int getUserInSystem();    //
    void removeUser();  //
    void exit();              //
}
