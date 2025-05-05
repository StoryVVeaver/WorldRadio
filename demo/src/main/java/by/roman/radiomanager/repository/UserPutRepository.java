package by.roman.radiomanager.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import by.roman.radiomanager.models.Favorites;
import by.roman.radiomanager.models.Filters;
import by.roman.radiomanager.models.Settings;

@Component
public interface UserPutRepository {
    String saveFilters(Filters filt);
    String saveSettings(Settings sett);
    String saveFavorites(List<Favorites> list);
}
