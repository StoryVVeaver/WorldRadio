package by.roman.radiomanager.service;

import java.util.List;

import by.roman.radiomanager.models.*;

public interface UserPutService {
    Settings saveSettings(Settings sett);
    List<FavoriteStation> saveFavoriteStations(List<FavoriteStation> favorites);
    List<FavoriteTrack> saveFavoriteTracks(List<FavoriteTrack> favorites);
    User saveUser(User user);
    List<FilterStation> saveFilterStation(List<FilterStation> list);
}
