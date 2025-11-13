package by.roman.radiomanager.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.roman.radiomanager.models.*;
import by.roman.radiomanager.repository.*;
import by.roman.radiomanager.service.UserPutService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserPutServiceImpl implements UserPutService{
    
    @Autowired
    private FavoriteStationsRepository favoriteStationsRepository;

    @Autowired
    private FavoriteTrackRepository favoriteTrackRepository;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Settings saveSettings(Settings sett){
        try {
            if(sett == null){
                return null;
            } else {
                return settingsRepository.save(sett);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public List<FavoriteStation> saveFavoriteStations(List<FavoriteStation> favorites){
        try {
            if(favorites == null){
                return null;
            } else {
                return favoriteStationsRepository.saveAll(favorites);
            }
        } catch (Exception e) {
            throw e;
        }
    }
    @Override
    public List<FavoriteTrack> saveFavoriteTracks(List<FavoriteTrack> favorites){
        try {
            if(favorites == null){
                return null;
            } else {
                return favoriteTrackRepository.saveAll(favorites);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public User saveUser(User user){
        try {
            if(user == null){
                return null;
            } else {
                return userRepository.save(user);
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
