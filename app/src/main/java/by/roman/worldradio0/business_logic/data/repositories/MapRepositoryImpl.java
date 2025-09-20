package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.data.database.MapDao;
import by.roman.worldradio0.business_logic.data.models.MapPoint;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.MapRepository;

public class MapRepositoryImpl implements MapRepository {
    private final MapDao mapDao;

    public MapRepositoryImpl(MapDao mapDao) {
        this.mapDao = mapDao;
    }

    @Override
    public List<MapPoint> getPoints() {
        try {
            return mapDao.getPoints();
        } catch (Exception e) {
            Log.e("MapRepositoryImpl", "ERROR: " + e.getMessage());
            return null;
        }
    }
}
