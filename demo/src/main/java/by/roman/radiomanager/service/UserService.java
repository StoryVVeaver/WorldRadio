package by.roman.radiomanager.service;

import by.roman.radiomanager.models.User;

public interface UserService {
    boolean findRegistredUser(String login, String password);
    String entranceUser(User user);
}
