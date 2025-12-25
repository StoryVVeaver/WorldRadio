package by.roman.radiomanager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import by.roman.radiomanager.models.*;
import by.roman.radiomanager.service.UserPutService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/api/v1/user/put")
@RestController
@AllArgsConstructor
public class UserPutController {

    @Autowired
    private final UserPutService userPutService;

    @PutMapping("/settings")
    public ResponseEntity<?> putSettings(@RequestBody Settings sett) {
        try {
            if (sett == null) {
                return ResponseEntity.badRequest().build();
            }
            Settings responce = userPutService.saveSettings(sett);
            if (responce != null) {
                return ResponseEntity.ok("saved");
            } else
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/favoriteStations")
    public ResponseEntity<?> putFavoriteStations(@RequestBody List<FavoriteStation> list) {
        try {
            if (list == null) {
                return ResponseEntity.badRequest().build();
            }
            List<FavoriteStation> responce = userPutService.saveFavoriteStations(list);
            if (responce != null) {
                return ResponseEntity.ok("saved");
            } else
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/favoriteTracks")
    public ResponseEntity<?> putFavoriteTracks(@RequestBody List<FavoriteTrack> list) {
        try {
            if (list == null) {
                return ResponseEntity.badRequest().build();
            }
            List<FavoriteTrack> responce = userPutService.saveFavoriteTracks(list);
            if (responce != null) {
                return ResponseEntity.ok("saved");
            } else
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/user")
    public ResponseEntity<?> putUser(@RequestBody User user) {
        try {
            if(user == null){
                return ResponseEntity.badRequest().body("empty request");
            }
            User responce = userPutService.saveUser(user);
            return responce != null ? ResponseEntity.ok("saved") : ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping("/filter")
    public ResponseEntity<?> putStationsFilter(@RequestBody List<FilterStation> list) {
        try {
            return ResponseEntity.ok().body(userPutService.saveFilterStation(list));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
