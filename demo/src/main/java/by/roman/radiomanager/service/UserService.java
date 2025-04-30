package by.roman.radiomanager.service;

import by.roman.radiomanager.UserRequest;
import by.roman.radiomanager.models.User;

public interface UserService {
    User findRegistredUser(String login, String password);
    User entranceUser(UserRequest userRequest);
}
