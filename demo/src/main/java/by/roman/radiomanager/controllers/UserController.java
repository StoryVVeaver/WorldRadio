package by.roman.radiomanager.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import by.roman.radiomanager.UserRequest;
import by.roman.radiomanager.models.User;
import by.roman.radiomanager.service.UserService;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;



@RequestMapping("/api/v1/user")
@RestController()
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/check")
    public String authenticateUser(@RequestBody UserRequest authRequest) {
        try{
            if(authRequest.getLogin().isEmpty() || authRequest.getPassword().isEmpty()){
                return "null";
            }
            if (userService.findRegistredUser(authRequest.getLogin(), authRequest.getPassword())) {
                return "Authentication successful";
            } else {
                return "Invalid credentials";
            }
        } catch(Exception e){
            return e.toString();
        }
        
    }

    @PostMapping("/enter")
    public String entranceUser(@RequestBody User user) {
        return userService.entranceUser(user);
    }

    @GetMapping("/isCorrect")
    public String isCorrect(){
        return "Correct";
    }

    @PostMapping("/post")
    public String postMethodName() {
        return "entity";
    }
    
}
