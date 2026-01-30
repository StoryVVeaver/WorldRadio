package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import java.util.List;

import by.roman.worldradio0.business_logic.data.models.FavoriteStation;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteStationRepositoryImpl;

public interface FavoriteStationRepository {
    void addToFavorite(int id, String uuid);
    void removeFromFavorite(String uuid);
    boolean isStationFavorite(String uuid);
    void addListener(FavoriteStationRepositoryImpl.OnFavoriteStationsChangedListener listener);
    void removeListener(FavoriteStationRepositoryImpl.OnFavoriteStationsChangedListener listener);
    List<FavoriteStation> getAllFavorites();
}
