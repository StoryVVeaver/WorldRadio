package by.roman.radiomanager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.roman.radiomanager.models.Favorites;
import by.roman.radiomanager.models.Filters;
import by.roman.radiomanager.models.Settings;
import by.roman.radiomanager.repository.UserGetRepository;
import by.roman.radiomanager.service.UserGetService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserGetServiceImpl implements UserGetService{
    
    @Autowired
    private final UserGetRepository userGetRepository;

    @Override
    public List<Favorites> getFavorites(Long id){
        try {
            List<Favorites> list = userGetRepository.findFavoritesByUserId(id);
            if(list == null){
                return null;
            } else return list;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Settings getSettings(Long id){
        try {
            Settings sett = userGetRepository.findSettingsById(id);
            if(sett == null){
                return null;
            } else return sett;
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public Filters getFilter(Long id){
        try {
            Filters filt = userGetRepository.findFiltersById(id);
            if(filt == null){
                return null;
            } else return filt;
        } catch (Exception e) {
            return null;
        }
    }
}
