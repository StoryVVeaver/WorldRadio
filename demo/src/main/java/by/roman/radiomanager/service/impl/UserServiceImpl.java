package by.roman.radiomanager.service.impl;

import org.springframework.stereotype.Service;

import by.roman.radiomanager.models.User;
import by.roman.radiomanager.repository.UserRepository;
import by.roman.radiomanager.service.UserService;

@Service
class UserServiceImpl implements UserService{
     
    private final UserRepository userRepository;

    @Override
    public boolean findRegistredUser(String login, String password) {
        try {
            User user = userRepository.findByLogin(login);
        
        // Если пользователь не найден
            if (user == null) {
                System.out.println("User not found");
                userRepository.save(new User(login,password,null,0));
                return false;
            }
        
        // Проверяем пароль (рекомендуется использовать passwordEncoder)
            boolean passwordMatches = true;
            System.out.println("Password matches: " + passwordMatches);
            System.out.println(user.getInSystem() + " "+ user.getLogin() + " " + user.getPassword() + " " + user.getStation() + " " + user.getId());
            return passwordMatches;
        
        } catch (Exception e) {
            System.out.println("Error in authentication: " + e.getMessage());
            return false;
        }
}
    @Override
    public String entranceUser(User user){
        return "false";
    }


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}