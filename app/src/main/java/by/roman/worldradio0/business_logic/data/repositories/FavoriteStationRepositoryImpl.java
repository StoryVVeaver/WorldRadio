package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.roman.worldradio0.business_logic.data.database.FavoriteStationDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.models.FavoriteStation;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteStationRepository;

public class FavoriteStationRepositoryImpl implements FavoriteStationRepository {
    private final FavoriteStationDao favoriteStationDao;
    private final UserDao userDao;
    private final List<OnFavoriteStationsChangedListener> listeners = new ArrayList<>();
    public FavoriteStationRepositoryImpl(FavoriteStationDao favoriteStationDao, UserDao userDao) {
        this.favoriteStationDao = favoriteStationDao;
        this.userDao = userDao;
    }
    public interface OnFavoriteStationsChangedListener {
        void onFavoriteStationsChanged();
    }
    @Override
    public void addListener(OnFavoriteStationsChangedListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    @Override
    public void removeListener(OnFavoriteStationsChangedListener listener) {
        listeners.remove(listener);
    }
    private void notifyFavoritesChanged() {
        for (OnFavoriteStationsChangedListener listener : new ArrayList<>(listeners)) {
            listener.onFavoriteStationsChanged();
        }
    }


    @Override
    public void addToFavorite(int id, String UUID){
        try {
            favoriteStationDao.addFavorite(id, userDao.getIdUserInSystem(),UUID);
            notifyFavoritesChanged();
        } catch (Exception e) {
            Log.e("FavoriteStationRepositoryImpl","Failed add to favorite: " + e.getMessage());
        }
    }
    @Override
    public void removeFromFavorite(String UUID){
        try {
            favoriteStationDao.removeFavorite(userDao.getIdUserInSystem(),UUID);
            notifyFavoritesChanged();
        } catch (Exception e) {
            Log.e("FavoriteStationRepositoryImpl","Failed remove from favorite: " + e.getMessage());
        }
    }
    @Override
    public boolean isStationFavorite(String UUID){
        try {
            return favoriteStationDao.isFavorite(userDao.getIdUserInSystem(),UUID);
        } catch (Exception e) {
            Log.e("FavoriteStationRepositoryImpl","Failed check favorite: " + e.getMessage());
            return false;
        }
    }
    @Override
    public List<FavoriteStation> getAllFavorites(){
        try {
            return favoriteStationDao.getAllFavorites(userDao.getIdUserInSystem());
        } catch (Exception e) {
            Log.e("FavoriteStationRepositoryImpl","Failed get all favorites: " + e.getMessage());
            return null;
        }
    }
}
