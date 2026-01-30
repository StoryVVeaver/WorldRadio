package by.roman.worldradio0.business_logic.data.database;

import static java.lang.String.valueOf;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.FavoriteTrack;
import by.roman.worldradio0.business_logic.data.models.RadioStation;

public class StationFilterDao {
    protected static final String TABLE_STATION_FILTER = "station_filter";
    protected static final String COLUMN_ID = "id";
    protected static final String COLUMN_FILTER = "avatar";

    protected static final String CREATE_TABLE_STATION_FILTER = "CREATE TABLE "+ TABLE_STATION_FILTER + " ("+
            COLUMN_ID +            " INTEGER PRIMARY KEY, "+
            COLUMN_FILTER +        " TEXT);";
    private final SQLiteDatabase db;
    public StationFilterDao(SQLiteDatabase db){
        this.db = db;
    }

    public void clearTable(){
        try {
            db.execSQL("DELETE FROM " + TABLE_STATION_FILTER);
        } catch (SQLException e) {
            Log.e("RadioDao", "Error clearing table");
        }
    }

    public void addFilter(String filter){
        ContentValues values = new ContentValues();
        values.put(COLUMN_FILTER, filter);
        db.insertWithOnConflict(TABLE_STATION_FILTER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<String> getAllFilters() {
        List<String> filters = new ArrayList<>();

        Cursor cursor = db.query(TABLE_STATION_FILTER,
                null,
                null,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            try (cursor) {
                while (cursor.moveToNext()) {
                    int filterIndex = cursor.getColumnIndex(COLUMN_FILTER);
                    if (filterIndex != -1) {
                        filters.add(cursor.getString(filterIndex));
                    }
                }
            }
        }
        return filters;
    }
}
