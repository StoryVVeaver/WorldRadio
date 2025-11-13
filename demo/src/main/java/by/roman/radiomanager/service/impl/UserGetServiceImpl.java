package by.roman.radiomanager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.roman.radiomanager.models.*;
import by.roman.radiomanager.repository.*;
import by.roman.radiomanager.service.UserGetService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserGetServiceImpl implements UserGetService{
    
    @Autowired
    private FavoriteStationsRepository favoriteStationsRepository;

    @Autowired
    private FavoriteTrackRepository favoriteTrackRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Override
    public List<FavoriteStation> getFavoriteStations(Long id){
        try {
            List<FavoriteStation> list = favoriteStationsRepository.findByUserId(id);
            if(list == null){
                return null;
            } else return list;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<FavoriteTrack> getFavoriteTracks(Long id){
        try {
            List<FavoriteTrack> list = favoriteTrackRepository.findByUserId(id);
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
            Settings sett = settingsRepository.findSettingsById(id);
            if(sett == null){
                return null;
            } else return sett;
        } catch (Exception e) {
            return null;
        }
    }
}
