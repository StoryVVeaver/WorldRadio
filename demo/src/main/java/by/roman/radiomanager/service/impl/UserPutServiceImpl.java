package by.roman.radiomanager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.roman.radiomanager.models.Favorites;
import by.roman.radiomanager.models.Filters;
import by.roman.radiomanager.models.Settings;
import by.roman.radiomanager.repository.UserPutRepository;
import by.roman.radiomanager.service.UserPutService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserPutServiceImpl implements UserPutService{
    
    @Autowired
    private final UserPutRepository userPutRepository;

    @Override
    public String saveFilters(Filters filt){
        try {
            if(filt == null){
                return "filt is null";
            } else {
                userPutRepository.saveFilters(filt);
                return "saved";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String saveSettings(Settings sett){
        try {
            if(sett == null){
                return "sett is null";
            } else {
                userPutRepository.saveSettings(sett);
                return "saved";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public String saveFavorites(List<Favorites> favorites){
        try {
            if(favorites == null){
                return "favorites is null";
            } else {
                userPutRepository.saveFavorites(favorites);
                return "saved";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
