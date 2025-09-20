package by.roman.worldradio0.business_logic.hilt.modules;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.data.database.FavoriteStationDao;
import by.roman.worldradio0.business_logic.data.database.FavoriteTrackDao;
import by.roman.worldradio0.business_logic.data.database.FilterDao;
import by.roman.worldradio0.business_logic.data.database.HistoryDao;
import by.roman.worldradio0.business_logic.data.database.MapDao;
import by.roman.worldradio0.business_logic.data.database.RadioStationDao;
import by.roman.worldradio0.business_logic.data.database.SQLiteDatabaseManager;
import by.roman.worldradio0.business_logic.data.database.SettingsDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {
    @Provides
    @Singleton
    public SQLiteOpenHelper provideDatabase(@ApplicationContext Context context) {
        return new SQLiteDatabaseManager(context);
    }
    @Provides
    @Singleton
    public SQLiteDatabase provideWritableDatabase(SQLiteOpenHelper helper) {
        return helper.getWritableDatabase();
    }
    @Provides
    @Singleton
    public RadioStationDao provideRadioStationDao(SQLiteDatabase db) {
        return new RadioStationDao(db);
    }
    @Provides
    @Singleton
    public FavoriteStationDao provideFavoriteStationsDao(SQLiteDatabase db) {
        return new FavoriteStationDao(db);
    }
    @Provides
    @Singleton
    public FavoriteTrackDao provideFavoriteTracksDao(SQLiteDatabase db) {
        return new FavoriteTrackDao(db);
    }
    @Provides
    @Singleton
    public FilterDao provideFilterDao(SQLiteDatabase db) {
        return new FilterDao(db);
    }
    @Provides
    @Singleton
    public UserDao provideUserDao(SQLiteDatabase db) {
        return new UserDao(db);
    }
    @Provides
    @Singleton
    public SettingsDao provideSettingsDao(SQLiteDatabase db) {
        return new SettingsDao(db);
    }
    @Provides
    @Singleton
    public HistoryDao provideHistoryDao(SQLiteDatabase db) {
        return new HistoryDao(db);
    }
    @Provides
    @Singleton
    public MapDao provideMapDao(SQLiteDatabase db) {
        return new MapDao(db);
    }
}

