package by.roman.demo.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import by.roman.demo.models.User;
import by.roman.demo.service.UserService;


@RequestMapping("/api/v1/user")
@RestController()
public class UserController {

    private final UserService userService;

    @PostMapping
    public boolean checkRegistredUser(@RequestBody User user){
        return userService.checkRegistredUser(user);
    }

    

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
}
