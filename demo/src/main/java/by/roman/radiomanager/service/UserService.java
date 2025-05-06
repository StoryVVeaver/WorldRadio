package by.roman.radiomanager.service;

import by.roman.radiomanager.models.User;
import by.roman.radiomanager.models.UserRequest;

public interface UserService {
    User findRegistredUser(String login, String password);
    User entranceUser(UserRequest userRequest);
}
