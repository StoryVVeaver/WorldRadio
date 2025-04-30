package by.roman.radiomanager.service.impl;

import org.springframework.stereotype.Service;

import by.roman.radiomanager.UserRequest;
import by.roman.radiomanager.models.User;
import by.roman.radiomanager.repository.UserRepository;
import by.roman.radiomanager.service.UserService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
class UserServiceImpl implements UserService{
     
    private final UserRepository userRepository;

    @Override
    public User findRegistredUser(String login, String password) {
        try {
            User user = userRepository.findByLogin(login);
        
            if (user == null) {
                userRepository.save(new User(login,password,null,0));
                System.out.println("User created");
                return userRepository.findByLoginAndPassword(login, password);
            } else return null;
        } catch (Exception e) {
            System.out.println("Error in registration: " + e.getMessage());
            return null;
        }
    
    }
    @Override
    public User entranceUser(UserRequest userRequest){
        try{
            User user = userRepository.findByLoginAndPassword(userRequest.getLogin(), userRequest.getPassword());

            if(user == null){
                System.out.println("User not found");
                return null;
            } else return user;
        } catch (Exception e){
            System.out.println("Error in entrance: " + e.getMessage());
            return null;
        }
    }


}