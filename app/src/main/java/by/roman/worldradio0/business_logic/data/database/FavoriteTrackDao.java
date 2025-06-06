package by.roman.worldradio0.business_logic.data.database;

import static java.lang.String.valueOf;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.business_logic.data.models.FavoriteTrack;

public class FavoriteTrackDao {
    protected static final String TABLE_FAVORITE_TRACK = "tracks";
    protected static final String COLUMN_ID_TRACK = "id";
    protected static final String COLUMN_USER_ID_TRACK = "userId";
    protected static final String COLUMN_TRACK_TRACK = "track";
    protected static final String CREATE_TABLE_FAVORITE_TRACK = "CREATE TABLE " + TABLE_FAVORITE_TRACK + " (" +
            COLUMN_ID_TRACK +           " INTEGER, " +
            COLUMN_USER_ID_TRACK +      " INTEGER, " +
            COLUMN_TRACK_TRACK + " TEXT, " +
            "FOREIGN KEY (" + COLUMN_USER_ID_TRACK + ") REFERENCES " + UserDao.TABLE_USER + "(" + UserDao.COLUMN_ID_USER + ") ON DELETE CASCADE;";
    protected final SQLiteDatabase db;

    public FavoriteTrackDao(SQLiteDatabase db) {
        this.db = db;
    }
    public void addFavorite(int id, int userId, String track) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID_TRACK, id);
        values.put(COLUMN_USER_ID_TRACK, userId);
        values.put(COLUMN_TRACK_TRACK, track);
        db.insertWithOnConflict(TABLE_FAVORITE_TRACK, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public void removeFavorite(int userId, String track) {
        db.delete(TABLE_FAVORITE_TRACK, COLUMN_USER_ID_TRACK + " = ? AND "
                + COLUMN_TRACK_TRACK + " = ?", new String[]{valueOf(userId), track});
    }
    public boolean isFavorite(int userId,String track){
        boolean isFavorite = false;
        Cursor cursor = db.query(
                TABLE_FAVORITE_TRACK,
                new String[]{COLUMN_TRACK_TRACK},
                COLUMN_TRACK_TRACK + " = ? AND " +
                        COLUMN_USER_ID_TRACK + " = ?",
                new String[]{track, valueOf(userId)},
                null, null, null
        );
        try (cursor) {
            if (cursor != null && cursor.moveToNext()) {
                isFavorite = cursor.getCount() > 0;
            }
        }
        return isFavorite;
    }
    public List<FavoriteTrack> getFavoritesByUser(int userId, int currentPage, int pageSize) {
        List<FavoriteTrack> favorites = new ArrayList<>();
        int offset = currentPage * pageSize;

        String query = "SELECT " + COLUMN_TRACK_TRACK + " FROM " + TABLE_FAVORITE_TRACK +
                " WHERE " + COLUMN_USER_ID_TRACK + " = ?" +
                " LIMIT ? OFFSET ?";

        Cursor cursor = db.rawQuery(query, new String[]{
                valueOf(userId),
                valueOf(pageSize),
                valueOf(offset)
        });

        if (cursor != null) {
            try (cursor) {
                while (cursor.moveToNext()) {
                    int idIndex = cursor.getColumnIndex(COLUMN_ID_TRACK);
                    int userIdIndex = cursor.getColumnIndex(COLUMN_USER_ID_TRACK);
                    int trackIndex = cursor.getColumnIndex(COLUMN_TRACK_TRACK);
                    if(idIndex != -1 && userIdIndex != -1 && trackIndex != -1){
                        favorites.add(new FavoriteTrack(
                                cursor.getInt(idIndex),
                                cursor.getInt(userIdIndex),
                                cursor.getString(trackIndex)
                        ));
                    }
                }
            } catch (Exception e) {
                Log.e("FavoriteDao", "Error reading favorite id");
            }
        }
        return favorites;
    }
}
