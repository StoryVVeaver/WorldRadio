package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import java.util.List;

import by.roman.worldradio0.business_logic.data.models.MapPoint;

public interface MapRepository {
    List<MapPoint> getPoints();
    MapPoint getPointByUUID(String uuid);
}
