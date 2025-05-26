package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import java.util.List;

import by.roman.worldradio0.business_logic.data.models.FavoriteStation;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteRepositoryImpl;

public interface FavoriteRepository {
    List<String> getFavoritesById(int currentPage, int pageSize);
    void addToFavorite(int id, String uuid);
    void removeFromFavorite(String uuid);
    boolean isStationFavorite(String uuid);
    void addListener(FavoriteRepositoryImpl.OnFavoritesChangedListener listener);
    void removeListener(FavoriteRepositoryImpl.OnFavoritesChangedListener listener);
    List<FavoriteStation> getAllFavorites();
}
