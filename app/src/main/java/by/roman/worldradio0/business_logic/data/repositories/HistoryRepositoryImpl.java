package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import java.util.List;

import by.roman.worldradio0.business_logic.data.database.FilterDao;
import by.roman.worldradio0.business_logic.data.database.HistoryDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.dto.HistoryDTO;
import by.roman.worldradio0.business_logic.data.models.History;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.HistoryRepository;

public class HistoryRepositoryImpl implements HistoryRepository {
    private final HistoryDao historyDao;
    private final UserDao userDao;
    public HistoryRepositoryImpl(HistoryDao historyDao, UserDao userDao){
        this.historyDao = historyDao;
        this.userDao = userDao;
    }
    @Override
    public void addToHistory(HistoryDTO dto) {
        try {
            historyDao.addToHistory(dto);
        } catch (Exception e) {
            Log.e("HistoryRepositoryImpl", "Failed add to history");
        }
    }

    @Override
    public void removeFromHistory(History history) {
        try {
            historyDao.removeFromHistory(history);
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
    public List<History> getHistoryList(int page, int page_size) {
        try {
            return historyDao.getHistoryByUser(userDao.getIdUserInSystem(), page,page_size);
        } catch (Exception e) {
            Log.e("HistoryRepositoryImpl", "Failed get history list");
            return null;
        }
    }
}
