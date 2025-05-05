package by.roman.radiomanager.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import by.roman.radiomanager.models.Favorites;
import by.roman.radiomanager.models.Filters;
import by.roman.radiomanager.models.Settings;
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
    public String putSettings(@RequestBody Settings sett) {
        try {
            if(sett == null){
                return "sett is null";
            }
            String str = userPutService.saveSettings(sett);
            if(str != null){
                return str;
            } else return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    @PutMapping("/filters")
    public String putFilters(@RequestBody Filters filt) {
        try {
            if(filt == null){
                return "filt is null";
            }
            String str = userPutService.saveFilters(filt);
            if(str != null){
                return str;
            } else return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    @PutMapping("/favorites")
    public String putFavorites(@RequestBody List<Favorites> list) {
        try {
            if(list == null){
                return "list is null";
            }
            String str = userPutService.saveFavorites(list);
            if(str != null){
                return str;
            } else return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
