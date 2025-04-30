package by.roman.radiomanager.service;

import java.util.List;

import by.roman.radiomanager.models.Favorites;
import by.roman.radiomanager.models.Filters;
import by.roman.radiomanager.models.Settings;

public interface UserGetService {
    List<Favorites> getFavorites(Long id);
    Filters getFilter(Long id);
    Settings getSettings(Long id);
}
