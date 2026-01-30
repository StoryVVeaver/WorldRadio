package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import java.util.Collections;
import java.util.List;

import by.roman.worldradio0.business_logic.data.database.StationFilterDao;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.StationFilterRepository;

public class StationFilterRepositoryImpl implements StationFilterRepository {
    private final StationFilterDao stationFilterDao;

    public StationFilterRepositoryImpl(StationFilterDao stationFilterDao){
        this.stationFilterDao = stationFilterDao;
    }
    @Override
    public void clearTable() {
        try {
            stationFilterDao.clearTable();
        } catch (Exception e) {
            Log.e("StationFilterDao", "clearTable" + e.getMessage());
        }
    }

    @Override
    public List<String> getAllFilters() {
        try {
            return stationFilterDao.getAllFilters();
        } catch (Exception e) {
            Log.e("StationFilterDao", "getFilters" + e.getMessage());
            return null;
        }
    }

    @Override
    public void addFilter(String filter) {
        try {
            stationFilterDao.addFilter(filter);
        } catch (Exception e) {
            Log.e("StationFilterDao", "addFilter" +e.getMessage());
        }
    }
}
