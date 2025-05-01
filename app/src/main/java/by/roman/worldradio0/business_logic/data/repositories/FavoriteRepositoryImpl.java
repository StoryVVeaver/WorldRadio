package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import java.util.List;

import by.roman.worldradio0.business_logic.data.database.FavoriteDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteRepository;

public class FavoriteRepositoryImpl implements FavoriteRepository {
    private final FavoriteDao favoriteDao;
    private final UserDao userDao;
    public FavoriteRepositoryImpl(FavoriteDao favoriteDao, UserDao userDao) {
        this.favoriteDao = favoriteDao;
        this.userDao = userDao;
    }
    @Override
    public void addToFavorite(String UUID){
        try {
            favoriteDao.addFavorite(userDao.getIdUserInSystem(),UUID);
        } catch (Exception e) {
            Log.e("FavoriteRepositoryImp","Failed add to favorite");
        }
    }
    @Override
    public void removeFromFavorite(String UUID){
        try {
            favoriteDao.removeFavorite(userDao.getIdUserInSystem(),UUID);
        } catch (Exception e) {
            Log.e("FavoriteRepositoryImp","Failed remove from favorite");
        }
    }
    @Override
    public boolean isStationFavorite(String UUID){
        try {
            return favoriteDao.isFavorite(userDao.getIdUserInSystem(),UUID);
        } catch (Exception e) {
            Log.e("FavoriteRepositoryImp","Failed check favorite");
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
