package by.roman.radiomanager.service;

import java.util.List;

import by.roman.radiomanager.models.*;

public interface UserGetService {
    List<FavoriteStation> getFavoriteStations(Long id);
    List<FavoriteTrack> getFavoriteTracks(Long id);
    Settings getSettings(Long id);
    List<FilterStation> getFilterStations();
    Long getCountUsers();
}
