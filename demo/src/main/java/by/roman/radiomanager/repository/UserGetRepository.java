package by.roman.radiomanager.repository;

import java.util.List;
import by.roman.radiomanager.models.Favorites;
import by.roman.radiomanager.models.Filters;
import by.roman.radiomanager.models.Settings;

public interface UserGetRepository {
    List<Favorites> findFavoritesByUserId(Long id);
    Settings findSettingsById(Long id);
    Filters findFiltersById(Long id);
}
