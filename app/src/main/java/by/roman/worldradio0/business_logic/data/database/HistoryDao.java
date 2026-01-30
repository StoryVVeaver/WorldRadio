package by.roman.worldradio0.business_logic.data.database;

import static java.lang.String.valueOf;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.HistoryDTO;
import by.roman.worldradio0.business_logic.data.models.History;

public class HistoryDao {
    protected static final String TABLE_HISTORY = "history";
    public static final String COLUMN_ID_HISTORY = "id";
    public static final String COLUMN_USER_ID_HISTORY = "user_id";
    public static final String COLUMN_UUID_STATION_HISTORY = "station_uuid";
    protected static final String CREATE_TABLE_HISTORY = "CREATE TABLE " + TABLE_HISTORY + " (" +
            COLUMN_ID_HISTORY + " INTEGER PRIMARY KEY, "+
            COLUMN_USER_ID_HISTORY +    " INTEGER, "+
            COLUMN_UUID_STATION_HISTORY +   " TEXT, " +
            "FOREIGN KEY (" + COLUMN_USER_ID_HISTORY + ") REFERENCES " + UserDao.TABLE_USER + "(" + UserDao.COLUMN_ID_USER + ") ON DELETE CASCADE"
            + ");";

    private final SQLiteDatabase db;
    public HistoryDao(SQLiteDatabase db) {
        this.db = db;
    }

    public void addToHistory(History history){
        removeFromHistory(history.getUuid(), history.getUser_id());
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_HISTORY, history.getUser_id());
        values.put(COLUMN_UUID_STATION_HISTORY, history.getUuid());
        db.insertWithOnConflict(TABLE_HISTORY, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
    public void removeFromHistory(String uuid, long id){
        db.delete(TABLE_HISTORY, COLUMN_USER_ID_HISTORY + " = ? AND "
                + COLUMN_UUID_STATION_HISTORY + " = ?", new String[]{valueOf(id), uuid});
    }
    public void deleteHistoryByUser(int userId) {
        try {
            db.delete(
                    TABLE_HISTORY,
                    COLUMN_USER_ID_HISTORY + " = ?",
                    new String[]{ String.valueOf(userId) }
            );
            Log.d("HistoryDao", "History deleted for user " + userId);
        } catch (Exception e) {
            Log.e("HistoryDao", "Error deleting history for user " + userId, e);
        }
    }
    public History getLastHistory(int userId) {
        History secondLast = null;

        Cursor cursor = db.query(
                TABLE_HISTORY,
                new String[]{ COLUMN_USER_ID_HISTORY, COLUMN_UUID_STATION_HISTORY },
                COLUMN_USER_ID_HISTORY + " = ?",
                new String[]{ String.valueOf(userId) },
                null,
                null,
                null
        );

        if (cursor != null) {
            try (cursor) {
                if (cursor.moveToLast() && cursor.moveToPrevious()) {
                    int userIdIndex = cursor.getColumnIndex(COLUMN_USER_ID_HISTORY);
                    int uuidIndex = cursor.getColumnIndex(COLUMN_UUID_STATION_HISTORY);
                    if(userIdIndex != -1 && uuidIndex != -1){
                        secondLast = new History(
                                cursor.getInt(userIdIndex),
                                cursor.getString(uuidIndex)
                        );
                    }
                }
            } catch (Exception e) {
                Log.e("HistoryDao", "Error reading second last history for user " + userId, e);
            }
        }

        return secondLast;
    }


    public List<History> getHistoryByUser(int userId, int offset, int pageSize) {
        List<History> histories = new ArrayList<>();

        String query = "SELECT " + COLUMN_USER_ID_HISTORY + ", " + COLUMN_UUID_STATION_HISTORY +
                " FROM " + TABLE_HISTORY +
                " WHERE " + COLUMN_USER_ID_HISTORY + " = ?" +
                " LIMIT " + pageSize + " OFFSET " + offset;

        Cursor cursor = db.rawQuery(query, new String[]{
                valueOf(userId),
        });

        if (cursor != null) {
            try (cursor) {
                while (cursor.moveToNext()) {
                    int userIdIndex = cursor.getColumnIndex(COLUMN_USER_ID_HISTORY);
                    int uuidIndex = cursor.getColumnIndex(COLUMN_UUID_STATION_HISTORY);
                    if(userIdIndex != -1 && uuidIndex != -1){
                        histories.add(new History(
                                cursor.getInt(userIdIndex),
                                cursor.getString(uuidIndex)
                        ));
                    }
                }
            } catch (Exception e) {
                Log.e("HistoryDao", "Error reading history");
            }
        }
        Collections.reverse(histories);
        return histories;
    }
}
