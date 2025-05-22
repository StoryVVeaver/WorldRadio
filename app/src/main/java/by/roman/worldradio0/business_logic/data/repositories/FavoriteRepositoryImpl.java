package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.business_logic.data.database.FavoriteDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteRepository;

public class FavoriteRepositoryImpl implements FavoriteRepository {
    private final FavoriteDao favoriteDao;
    private final UserDao userDao;
    private final List<OnFavoritesChangedListener> listeners = new ArrayList<>();
    public FavoriteRepositoryImpl(FavoriteDao favoriteDao, UserDao userDao) {
        this.favoriteDao = favoriteDao;
        this.userDao = userDao;
    }
    public interface OnFavoritesChangedListener {
        void onFavoritesChanged();
    }
    @Override
    public void addListener(OnFavoritesChangedListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    @Override
    public void removeListener(OnFavoritesChangedListener listener) {
        listeners.remove(listener);
    }
    private void notifyFavoritesChanged() {
        for (OnFavoritesChangedListener listener : new ArrayList<>(listeners)) {
            listener.onFavoritesChanged();
        }
    }
    @Override
    public void addToFavorite(String UUID){
        try {
            favoriteDao.addFavorite(userDao.getIdUserInSystem(),UUID);
            notifyFavoritesChanged();
        } catch (Exception e) {
            Log.e("FavoriteRepositoryImp","Failed add to favorite");
        }
    }
    @Override
    public void removeFromFavorite(String UUID){
        try {
            favoriteDao.removeFavorite(userDao.getIdUserInSystem(),UUID);
            notifyFavoritesChanged();
        } catch (Exception e) {
            Log.e("FavoriteRepositoryImp","Failed remove from favorite: " + e.getMessage());
        }
    }
    @Override
    public boolean isStationFavorite(String UUID){
        try {
            return favoriteDao.isFavorite(userDao.getIdUserInSystem(),UUID);
        } catch (Exception e) {
            Log.e("FavoriteRepositoryImp","Failed check favorite: " + e.getMessage());
            return false;
        }
    }
    @Override
    public List<String> getFavoritesById(int currentPage, int pagSize){
        try {
            return favoriteDao.getFavoritesByUser(userDao.getIdUserInSystem(),currentPage,pagSize);
        } catch (Exception e) {
            Log.e("FavoriteRepositoryImp","Failed get favorites list");
            return null;
        }
    }
}
