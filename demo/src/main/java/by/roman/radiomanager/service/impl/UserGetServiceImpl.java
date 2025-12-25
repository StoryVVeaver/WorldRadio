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
    private UserRepository userRepository;
    
    @Autowired
    private FavoriteStationsRepository favoriteStationsRepository;

    @Autowired
    private FavoriteTrackRepository favoriteTrackRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private FilterStationRepository filterStationRepository;

    @Override
    public List<FavoriteStation> getFavoriteStations(Long id){
        try {
            List<FavoriteStation> list = favoriteStationsRepository.findByUserId(id);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<FavoriteTrack> getFavoriteTracks(Long id){
        try {
            List<FavoriteTrack> list = favoriteTrackRepository.findByUserId(id);
            return list;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Settings getSettings(Long id){
        try {
            Settings sett = settingsRepository.findSettingsById(id);
            return sett;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<FilterStation> getFilterStations(){
        try {
            return filterStationRepository.findAll();
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Long getCountUsers(){
        try {
            return userRepository.count();
        } catch (Exception e) {
            throw e;
        }
    }
}
