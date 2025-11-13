package by.roman.radiomanager.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import by.roman.radiomanager.models.*;
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

    @GetMapping("/favoriteStations/{id}")
    public ResponseEntity<?> getFavoriteStations(@PathVariable Long id) {
        try {
            List<FavoriteStation> list = userGetService.getFavoriteStations(id);
            if (list == null) {
                return ResponseEntity.ok().build();
            } else
                return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/favoriteTracks/{id}")
    public ResponseEntity<?> getFavoriteTracks(@PathVariable Long id) {
        try {
            List<FavoriteTrack> list = userGetService.getFavoriteTracks(id);
            if (list == null) {
                return ResponseEntity.ok().build();
            } else
                return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/settings/{id}")
    public ResponseEntity<?> getSettings(@PathVariable Long id) {
        try {
            Settings sett = userGetService.getSettings(id);
            if (sett == null) {
                return ResponseEntity.notFound().build();
            } else
                return ResponseEntity.ok(sett);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
