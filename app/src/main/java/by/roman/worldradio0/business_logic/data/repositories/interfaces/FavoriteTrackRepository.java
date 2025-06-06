package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import java.util.List;

import by.roman.worldradio0.business_logic.data.models.FavoriteStation;
import by.roman.worldradio0.business_logic.data.models.FavoriteTrack;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteTrackRepositoryImpl;

public interface FavoriteTrackRepository {
    void addToFavorite(int id, String track);
    void removeFromFavorite(String track);
    boolean isStationFavorite(String track);
    void addListener(FavoriteTrackRepositoryImpl.OnFavoriteTracksChangedListener listener);
    void removeListener(FavoriteTrackRepositoryImpl.OnFavoriteTracksChangedListener listener);
    List<FavoriteTrack> getFavorites(int currentPage, int pageSize);
}
