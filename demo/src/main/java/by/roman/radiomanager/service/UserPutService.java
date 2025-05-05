package by.roman.radiomanager.service;

import java.util.List;

import by.roman.radiomanager.models.Favorites;
import by.roman.radiomanager.models.Filters;
import by.roman.radiomanager.models.Settings;

public interface UserPutService {
    String saveFilters(Filters filt);
    String saveSettings(Settings sett);
    String saveFavorites(List<Favorites> favorites);
}
