package by.roman.demo.service.impl;

import org.springframework.stereotype.Service;

import by.roman.demo.models.User;
import by.roman.demo.repository.UserRepository;
import by.roman.demo.service.UserService;

@Service
class UserServiceImpl implements UserService{
     
    private final UserRepository userRepository;

    @Override
    public boolean checkRegistredUser(User user){
        //todo
        return false;
    }

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
}