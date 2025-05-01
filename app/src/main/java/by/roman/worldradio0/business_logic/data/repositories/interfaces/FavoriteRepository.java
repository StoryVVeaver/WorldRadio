package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import java.util.List;

public interface FavoriteRepository {
    List<String> getFavoritesById(int currentPage, int pageSize);
    void addToFavorite(String uuid);
    void removeFromFavorite(String uuid);
    boolean isStationFavorite(String uuid);
}
