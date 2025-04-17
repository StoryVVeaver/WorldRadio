package by.roman.worldradio0.business_logic.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.FavoriteStationDTO;

public class FavoriteDao {

    protected static final String TABLE_FAVORITE = "favorites";
    protected static final String COLUMN_USER_ID_FAVORITE = "user_id";
    protected static final String COLUMN_STATION_UUID_FAVORITE = "station_id";
    protected static final String CREATE_TABLE_FAVORITE = "CREATE TABLE " + TABLE_FAVORITE + " (" +
            COLUMN_USER_ID_FAVORITE +    " INTEGER, " +
            COLUMN_STATION_UUID_FAVORITE + " TEXT, " +
            "PRIMARY KEY (" + COLUMN_USER_ID_FAVORITE + ", " + COLUMN_STATION_UUID_FAVORITE + "), " +
            "FOREIGN KEY (" + COLUMN_USER_ID_FAVORITE + ") REFERENCES " + UserDao.TABLE_USER + "(" + UserDao.COLUMN_ID_USER + ") ON DELETE CASCADE, " +
            "FOREIGN KEY (" + COLUMN_STATION_UUID_FAVORITE + ") REFERENCES " + RadioStationDao.TABLE_RADIO_STATION + "(" + RadioStationDao.COLUMN_UUID_STATION + ") ON DELETE CASCADE);";

    private final SQLiteDatabase db;
    public FavoriteDao(SQLiteDatabase db){
        this.db = db;
    }
    public void addFavorite(int id,String UUID) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FAVORITE, id);
        values.put(COLUMN_STATION_UUID_FAVORITE, UUID);
        db.insertWithOnConflict(TABLE_FAVORITE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public void removeFavorite(int id, String UUID) {
        db.delete(TABLE_FAVORITE, COLUMN_USER_ID_FAVORITE + " = ? "
                + COLUMN_STATION_UUID_FAVORITE + " = ?", new String[]{String.valueOf(id), UUID});
    }
    public boolean isFavorite(int id,String UUID){
        boolean isFavorite = false;
        Cursor cursor = db.query(
                TABLE_FAVORITE,
                new String[]{COLUMN_STATION_UUID_FAVORITE},
                COLUMN_STATION_UUID_FAVORITE + " = ? AND " +
                        COLUMN_USER_ID_FAVORITE + " = ?",
                new String[]{UUID, String.valueOf(id)},
                null, null, null
        );
        try (cursor) {
            if (cursor != null && cursor.moveToNext()) {
                isFavorite = cursor.getCount() > 0;
            }
        }
        return isFavorite;
    }
    public List<String> getFavoritesByUser(int id) {
        List<String> favorites = new ArrayList<>();
        String query = "SELECT " + COLUMN_STATION_UUID_FAVORITE + " FROM " + TABLE_FAVORITE +
                " WHERE " + COLUMN_USER_ID_FAVORITE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        if (cursor != null) {
            try (cursor) {
                while (cursor.moveToNext()) {
                    String uuid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATION_UUID_FAVORITE));
                    favorites.add(uuid);
                }
            }
        }
        return favorites;
    }
}