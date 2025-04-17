package by.roman.worldradio0.business_logic.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.Settings;

public class SettingsDao {
    protected static final String TABLE_SETTINGS = "settings";
    protected static final String COLUMN_USER_ID_SETTINGS = "id";
    public static final String COLUMN_THEME_SETTINGS = "theme";
    public static final String COLUMN_MAP_SETTINGS = "map";
    public static final String COLUMN_TIMER_SECONDS_SETTINGS = "seconds";
    public static final String COLUMN_TIMER_DOTS_SETTINGS = "dots";
    public static final String COLUMN_FILTER_SETTINGS = "filter";

    protected static final String CREATE_TABLE_SETTINGS = "CREATE TABLE " + TABLE_SETTINGS + " (" +
            COLUMN_USER_ID_SETTINGS +       " INTEGER, " +
            COLUMN_THEME_SETTINGS +         " INTEGER, " +
            COLUMN_MAP_SETTINGS +           " INTEGER, " +
            COLUMN_TIMER_SECONDS_SETTINGS + " INTEGER, " +
            COLUMN_TIMER_DOTS_SETTINGS +    " INTEGER, " +
            COLUMN_FILTER_SETTINGS +        " INTEGER, " +
            "FOREIGN KEY (" + COLUMN_USER_ID_SETTINGS + ") REFERENCES " + UserDao.TABLE_USER + "(" + UserDao.COLUMN_ID_USER + ") ON DELETE CASCADE" + ");";
    private final SQLiteDatabase db;
    public SettingsDao(SQLiteDatabase db){
        this.db = db;
    }
    public void setSettings(@NonNull SettingsDTO dto){
        ContentValues values = new ContentValues();
        values.put(COLUMN_THEME_SETTINGS,dto.getTheme());
        values.put(COLUMN_MAP_SETTINGS,dto.getMapEnabled());
        values.put(COLUMN_TIMER_SECONDS_SETTINGS,dto.getTimerSeconds());
        values.put(COLUMN_TIMER_DOTS_SETTINGS,dto.getTimerDots());
        values.put(COLUMN_FILTER_SETTINGS,dto.getFilterEnabled());
        String selection = COLUMN_USER_ID_SETTINGS + " = ?";
        String[] selectionArgs = {String.valueOf(dto.getId())};
        db.update(TABLE_SETTINGS, values, selection, selectionArgs);
    }
    public Settings getSetting(int id) {
        Cursor cursor = db.query(TABLE_SETTINGS,
                new String[]{COLUMN_USER_ID_SETTINGS, COLUMN_THEME_SETTINGS,
                        COLUMN_MAP_SETTINGS, COLUMN_TIMER_SECONDS_SETTINGS,
                        COLUMN_TIMER_DOTS_SETTINGS,COLUMN_FILTER_SETTINGS},
                COLUMN_USER_ID_SETTINGS + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        if(cursor != null && cursor.moveToNext()){
            try  (cursor) {
                int idIndex = cursor.getColumnIndex(COLUMN_USER_ID_SETTINGS);
                int themeIndex = cursor.getColumnIndex(COLUMN_THEME_SETTINGS);
                int mapIndex = cursor.getColumnIndex(COLUMN_MAP_SETTINGS);
                int timerSecondsIndex = cursor.getColumnIndex(COLUMN_TIMER_SECONDS_SETTINGS);
                int timerDotsIndex = cursor.getColumnIndex(COLUMN_TIMER_DOTS_SETTINGS);
                int filterIndex = cursor.getColumnIndex(COLUMN_FILTER_SETTINGS);
                if(idIndex != -1 && themeIndex != -1 && mapIndex != -1 && timerSecondsIndex != -1 && timerDotsIndex != -1){
                    return new Settings(
                            cursor.getInt(idIndex),
                            cursor.getInt(themeIndex),
                            cursor.getInt(mapIndex),
                            cursor.getInt(timerSecondsIndex),
                            cursor.getInt(timerDotsIndex),
                            cursor.getInt(filterIndex)
                    );
                }
            }
        }
        return null;
    }
    public void removeSettings(int id){
        ContentValues values = new ContentValues();
        values.put(COLUMN_THEME_SETTINGS,0);
        values.put(COLUMN_MAP_SETTINGS,0);
        values.put(COLUMN_TIMER_SECONDS_SETTINGS,0);
        values.put(COLUMN_TIMER_DOTS_SETTINGS,0);
        values.put(COLUMN_FILTER_SETTINGS,0);
        String selection = COLUMN_USER_ID_SETTINGS + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.update(TABLE_SETTINGS, values, selection, selectionArgs);
    }
    public void addSettings(@NonNull SettingsDTO dto){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_SETTINGS,dto.getId());
        values.put(COLUMN_THEME_SETTINGS,dto.getTheme());
        values.put(COLUMN_MAP_SETTINGS,dto.getMapEnabled());
        values.put(COLUMN_TIMER_SECONDS_SETTINGS,dto.getTimerSeconds());
        values.put(COLUMN_TIMER_DOTS_SETTINGS,dto.getTimerDots());
        values.put(COLUMN_FILTER_SETTINGS,dto.getFilterEnabled());
        db.insertWithOnConflict(TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
