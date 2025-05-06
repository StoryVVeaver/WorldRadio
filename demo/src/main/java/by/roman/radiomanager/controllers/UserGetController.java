package by.roman.radiomanager.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import by.roman.radiomanager.models.Favorites;
import by.roman.radiomanager.models.Filters;
import by.roman.radiomanager.models.Settings;
import by.roman.radiomanager.service.UserGetService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequestMapping("/api/v1/user/get")
@RestController
@AllArgsConstructor
public class UserGetController {
    
    @Autowired
    private final UserGetService userGetService;

    @GetMapping("/favorites/{id}")
    public ResponseEntity<?> getFavorites(@PathVariable Long id) {
        try {
            List<Favorites> list = userGetService.getFavorites(id);
            if(list == null){
                return ResponseEntity.ok().body("Favorites is empty");
            } else return ResponseEntity.ok(list);
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/settings/{id}")
    public ResponseEntity<?> getSettings(@PathVariable Long id) {
        try {
            Settings sett = userGetService.getSettings(id);
            if(sett == null){
                return ResponseEntity.ok().body("Settings is empty");
            } else return ResponseEntity.ok(sett);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/filters/{id}")
    public ResponseEntity<?> getFilters(@PathVariable Long id){
        try {
            Filters filt = userGetService.getFilter(id);
            if(filt == null){
                return ResponseEntity.ok().body("Filters is empty");
            } else return ResponseEntity.ok(filt);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
