package by.roman.worldradio0.business_logic.data.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.business_logic.data.models.MapPoint;

public class MapDao {
    private final SQLiteDatabase db;
    public MapDao(SQLiteDatabase db) {
        this.db = db;
    }
    public List<MapPoint> getPoints() {
        List<MapPoint> result = new ArrayList<>();
        Cursor cursor = null;
        try {
            String selection = RadioStationDao.COLUMN_GEO_LATITUDE_STATION + " IS NOT NULL AND "
                    + RadioStationDao.COLUMN_GEO_LONGITUDE_STATION + " IS NOT NULL AND "
                    + "ABS(" + RadioStationDao.COLUMN_GEO_LATITUDE_STATION + ") > 1e-9 AND "
                    + "ABS(" + RadioStationDao.COLUMN_GEO_LONGITUDE_STATION + ") > 1e-9";

            cursor = db.query(
                    RadioStationDao.TABLE_RADIO_STATION,
                    new String[] {
                            RadioStationDao.COLUMN_UUID_STATION,
                            RadioStationDao.COLUMN_GEO_LONGITUDE_STATION,
                            RadioStationDao.COLUMN_GEO_LATITUDE_STATION
                    },
                    selection,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                final int idxUuid = cursor.getColumnIndexOrThrow(RadioStationDao.COLUMN_UUID_STATION);
                final int idxLat = cursor.getColumnIndexOrThrow(RadioStationDao.COLUMN_GEO_LATITUDE_STATION);
                final int idxLon = cursor.getColumnIndexOrThrow(RadioStationDao.COLUMN_GEO_LONGITUDE_STATION);

                while (cursor.moveToNext()) {
                    String uuid = cursor.getString(idxUuid);
                    double lat = cursor.getDouble(idxLat);
                    double lon = cursor.getDouble(idxLon);
                    result.add(new MapPoint(lat, lon, uuid));
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return result;
    }

}
