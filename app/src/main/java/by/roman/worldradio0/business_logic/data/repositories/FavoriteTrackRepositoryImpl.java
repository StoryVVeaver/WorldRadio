package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.roman.worldradio0.business_logic.data.database.FavoriteStationDao;
import by.roman.worldradio0.business_logic.data.database.FavoriteTrackDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.models.FavoriteStation;
import by.roman.worldradio0.business_logic.data.models.FavoriteTrack;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteTrackRepository;

public class FavoriteTrackRepositoryImpl implements FavoriteTrackRepository {
    private final FavoriteTrackDao favoriteTrackDao;
    private final UserDao userDao;
    private final List<FavoriteTrackRepositoryImpl.OnFavoriteTracksChangedListener> listeners = new ArrayList<>();
    public FavoriteTrackRepositoryImpl(FavoriteTrackDao favoriteTrackDao, UserDao userDao) {
        this.favoriteTrackDao = favoriteTrackDao;
        this.userDao = userDao;
    }
    @Override
    public void addToFavorite(int id, String track) {
        try {
            favoriteTrackDao.addFavorite(id, userDao.getIdUserInSystem(), track);
            notifyFavoritesChanged();
        } catch (Exception e) {
            Log.e("FavoriteRepositoryImpl","Failed add to favorite: " + e.getMessage());
        }
    }

    @Override
    public void removeFromFavorite(String track) {
        try {
            favoriteTrackDao.removeFavorite(userDao.getIdUserInSystem(), track);
            notifyFavoritesChanged();
        } catch (Exception e) {
            Log.e("FavoriteRepositoryImp","Failed remove from favorite: " + e.getMessage());
        }
    }

    @Override
    public boolean isStationFavorite(String track) {
        try {
            return favoriteTrackDao.isFavorite(userDao.getIdUserInSystem(), track);
        } catch (Exception e) {
            Log.e("FavoriteRepositoryImpl","Failed check favorite: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void addListener(OnFavoriteTracksChangedListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(OnFavoriteTracksChangedListener listener) {
        listeners.remove(listener);
    }

    @Override
    public List<FavoriteTrack> getFavorites(int currentPage, int pageSize) {
        try {
            return favoriteTrackDao.getFavoritesByUser(userDao.getIdUserInSystem(), currentPage, pageSize);
        } catch (Exception e) {
            Log.e("FavoriteRepositoryImpl","Failed get favorites list: " + e.getMessage());
            return null;
        }
    }

    private void notifyFavoritesChanged() {
        for (FavoriteTrackRepositoryImpl.OnFavoriteTracksChangedListener listener : new ArrayList<>(listeners)) {
            listener.onFavoriteTracksChanged();
        }
    }
    public interface OnFavoriteTracksChangedListener {
        void onFavoriteTracksChanged();
    }
}
