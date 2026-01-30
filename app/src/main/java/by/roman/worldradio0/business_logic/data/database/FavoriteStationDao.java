package by.roman.worldradio0.business_logic.data.database;

import static java.lang.String.valueOf;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.business_logic.data.models.FavoriteStation;

public class FavoriteStationDao {

    protected static final String TABLE_FAVORITE_STATION = "favorites";
    protected static final String COLUMN_ID_FAVORITE = "id";
    protected static final String COLUMN_USER_ID_FAVORITE = "user_id";
    protected static final String COLUMN_STATION_UUID_FAVORITE = "station_id";
    protected static final String CREATE_TABLE_FAVORITE_STATION = "CREATE TABLE " + TABLE_FAVORITE_STATION + " (" +
            COLUMN_ID_FAVORITE +           " INTEGER, " +
            COLUMN_USER_ID_FAVORITE +      " INTEGER, " +
            COLUMN_STATION_UUID_FAVORITE + " TEXT, " +
            "PRIMARY KEY (" + COLUMN_USER_ID_FAVORITE + ", " + COLUMN_STATION_UUID_FAVORITE + "), " +
            "FOREIGN KEY (" + COLUMN_USER_ID_FAVORITE + ") REFERENCES " + UserDao.TABLE_USER + "(" + UserDao.COLUMN_ID_USER + ") ON DELETE CASCADE, " +
            "FOREIGN KEY (" + COLUMN_STATION_UUID_FAVORITE + ") REFERENCES " + RadioStationDao.TABLE_RADIO_STATION + "(" + RadioStationDao.COLUMN_UUID_STATION + ") ON DELETE CASCADE);";

    private final SQLiteDatabase db;
    public FavoriteStationDao(SQLiteDatabase db){
        this.db = db;
    }
    public void addFavorite(int id, int userId, String UUID) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_FAVORITE, id);
        values.put(COLUMN_USER_ID_FAVORITE, userId);
        values.put(COLUMN_STATION_UUID_FAVORITE, UUID);
        db.insertWithOnConflict(TABLE_FAVORITE_STATION, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public void removeFavorite(int userId, String UUID) {
        db.delete(TABLE_FAVORITE_STATION, COLUMN_USER_ID_FAVORITE + " = ? AND "
                + COLUMN_STATION_UUID_FAVORITE + " = ?", new String[]{valueOf(userId), UUID});
    }
    public boolean isFavorite(int userId, String UUID) {
        boolean isFavorite = false;

        if (UUID == null) {
            return false;
        }

        Cursor cursor = db.query(
                TABLE_FAVORITE_STATION,
                new String[]{COLUMN_STATION_UUID_FAVORITE},
                COLUMN_STATION_UUID_FAVORITE + " = ? AND " +
                        COLUMN_USER_ID_FAVORITE + " = ?",
                new String[]{UUID, String.valueOf(userId)},
                null, null, null
        );

        if (cursor != null) {
            try {
                isFavorite = cursor.getCount() > 0;
            } finally {
                cursor.close();
            }
        }
        return isFavorite;
    }
    public List<String> getFavoritesByUser(int userId) {
        List<String> favorites = new ArrayList<>();

        String query = "SELECT " + COLUMN_STATION_UUID_FAVORITE + " FROM " + TABLE_FAVORITE_STATION +
                " WHERE " + COLUMN_USER_ID_FAVORITE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                valueOf(userId),
        });

        if (cursor != null) {
            try (cursor) {
                while (cursor.moveToNext()) {
                    String uuid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATION_UUID_FAVORITE));
                    favorites.add(uuid);
                }
            } catch (Exception e) {
                Log.e("FavoriteDao", "Error reading favorite id");
            }
        }
        return favorites;
    }
    public List<FavoriteStation> getAllFavorites(int userId){
        List<FavoriteStation> list = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_FAVORITE_STATION + " WHERE " + COLUMN_USER_ID_FAVORITE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null){
            try (cursor){
                while (cursor.moveToNext()){
                    int idIndex = cursor.getColumnIndex(COLUMN_ID_FAVORITE);
                    int userIdIndex = cursor.getColumnIndex(COLUMN_USER_ID_FAVORITE);
                    int stationIndex = cursor.getColumnIndex(COLUMN_STATION_UUID_FAVORITE);
                    if(idIndex != -1 && userIdIndex != -1 && stationIndex != -1){
                        list.add(new FavoriteStation(
                                cursor.getInt(idIndex),
                                cursor.getInt(userIdIndex),
                                cursor.getString(stationIndex)
                        ));
                    }
                }
            }
        }
        return list;
    }
}