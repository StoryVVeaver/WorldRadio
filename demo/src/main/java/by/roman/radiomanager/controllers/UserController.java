package by.roman.radiomanager.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import by.roman.radiomanager.models.User;
import by.roman.radiomanager.models.UserRequest;
import by.roman.radiomanager.service.UserService;
import lombok.AllArgsConstructor;


@RequestMapping("/api/v1/user")
@RestController()
@AllArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/reg")
    public ResponseEntity<?> regUser(@RequestBody UserRequest userRequest) {
        try{
            if(userRequest.getLogin().isEmpty() || userRequest.getPassword().isEmpty()){
                return ResponseEntity.badRequest().build();
            }
            User user = userService.findRegistredUser(userRequest.getLogin(), userRequest.getPassword());
            if(user == null){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else return ResponseEntity.ok(user);
        } catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/enter")
    public ResponseEntity<?> entranceUser(@RequestBody UserRequest userRequest) {
        try {
            if(userRequest.getLogin().isEmpty() || userRequest.getPassword().isEmpty()){
                return ResponseEntity.badRequest().build();
            }
            User user = userService.entranceUser(userRequest);
            if(user == null){
                return ResponseEntity.notFound().build();
            } else return ResponseEntity.ok(user);
           
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            String response = userService.deleteUser(id);
            if(response != null){
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    

    @GetMapping
    public String isCorrect(){
        return "Correct";
    }
}
