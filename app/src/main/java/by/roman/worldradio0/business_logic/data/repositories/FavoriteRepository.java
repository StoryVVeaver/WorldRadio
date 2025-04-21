package by.roman.worldradio0.business_logic.data.repositories;

import java.util.List;

import by.roman.worldradio0.business_logic.data.models.FavoriteStation;
import by.roman.worldradio0.business_logic.data.models.RadioStation;

public interface FavoriteRepository {
    List<String> getFavoritesById(int currentPage, int pageSize);
    void addToFavorite(String uuid);
    void removeFromFavorite(String uuid);
    boolean isStationFavorite(String uuid);
}
