package by.roman.worldradio0.business_logic.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

public class SQLiteDatabaseManager extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "radio_app.db";
    private static final int DATABASE_VERSION = 1;
    public SQLiteDatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(RadioStationDao.CREATE_TABLE_RADIO_STATION);
        db.execSQL(UserDao.CREATE_TABLE_USER);
        db.execSQL(FilterDao.CREATE_TABLE_FILTER);
        db.execSQL(SettingsDao.CREATE_TABLE_SETTINGS);
        db.execSQL(FavoriteDao.CREATE_TABLE_FAVORITE);
    }
    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RadioStationDao.TABLE_RADIO_STATION);
        db.execSQL("DROP TABLE IF EXISTS " + UserDao.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + FilterDao.TABLE_FILTER);
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteDao.TABLE_FAVORITE);
        db.execSQL("DROP TABLE IF EXISTS " + SettingsDao.TABLE_SETTINGS);
        onCreate(db);
    }
}
