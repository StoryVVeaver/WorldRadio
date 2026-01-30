package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import java.util.List;

import by.roman.worldradio0.business_logic.data.database.FilterDao;
import by.roman.worldradio0.business_logic.data.database.HistoryDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.dto.HistoryDTO;
import by.roman.worldradio0.business_logic.data.models.History;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.HistoryRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;

public class HistoryRepositoryImpl implements HistoryRepository {
    private final HistoryDao historyDao;
    private final UserDao userDao;
    public HistoryRepositoryImpl(HistoryDao historyDao, UserDao userDao){
        this.historyDao = historyDao;
        this.userDao = userDao;
    }
    @Override
    public void addToHistory(History history) {
        try {
            historyDao.addToHistory(history);
        } catch (Exception e) {
            Log.e("HistoryRepositoryImpl", "Failed add to history");
        }
    }

    @Override
    public void removeFromHistory(String uuid) {
        try {
            historyDao.removeFromHistory(uuid, userDao.getIdUserInSystem());
        } catch (Exception e) {
            Log.e("HistoryRepositoryImpl", "Failed remove from history");
        }
    }
    @Override
    public void removeFromHistoryById(int userId){
        try {
            historyDao.deleteHistoryByUser(userId);
        } catch (Exception e) {
            Log.e("HistoryRepositoryImpl", "Failed remove by id");
        }
    }
    @Override
    public List<History> getHistoryList(int offset, int page_size) {
        try {
            return historyDao.getHistoryByUser(userDao.getIdUserInSystem(), offset,page_size);
        } catch (Exception e) {
            Log.e("HistoryRepositoryImpl", "Failed get history list");
            return null;
        }
    }

    @Override
    public History getLastHistory() {
        try {
            return historyDao.getLastHistory(userDao.getIdUserInSystem());
        } catch (Exception e) {
            Log.e("HistoryRepositoryImpl", "Failed get last history");
            return null;
        }
    }

}
