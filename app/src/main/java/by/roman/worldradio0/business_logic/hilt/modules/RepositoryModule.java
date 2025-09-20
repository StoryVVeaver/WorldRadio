package by.roman.worldradio0.business_logic.hilt.modules;

import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.data.database.FavoriteStationDao;
import by.roman.worldradio0.business_logic.data.database.FavoriteTrackDao;
import by.roman.worldradio0.business_logic.data.database.FilterDao;
import by.roman.worldradio0.business_logic.data.database.HistoryDao;
import by.roman.worldradio0.business_logic.data.database.MapDao;
import by.roman.worldradio0.business_logic.data.database.RadioStationDao;
import by.roman.worldradio0.business_logic.data.database.SettingsDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteTrackRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.HistoryRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.MapRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteStationRepository;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteStationRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteTrackRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FilterRepository;
import by.roman.worldradio0.business_logic.data.repositories.FilterRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.HistoryRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.MapRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.RadioRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.SettingsRepository;
import by.roman.worldradio0.business_logic.data.repositories.SettingsRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.data.repositories.UserRepositoryImpl;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class RepositoryModule {

    @Provides
    @Singleton
    public RadioRepository provideRadioRepository(RadioStationDao radioStationDao, FavoriteStationDao favoriteStationDao, UserDao userDao, FilterDao filterDao) {
        return new RadioRepositoryImpl(radioStationDao, favoriteStationDao, userDao, filterDao);
    }

    @Provides
    @Singleton
    public FavoriteStationRepository provideFavoriteStationRepository(FavoriteStationDao favoriteStationDao, UserDao userDao) {
        return new FavoriteStationRepositoryImpl(favoriteStationDao, userDao);
    }

    @Provides
    @Singleton
    public FavoriteTrackRepository provideFavoriteTrackRepository(FavoriteTrackDao FavoriteTrackDao, UserDao userDao) {
        return new FavoriteTrackRepositoryImpl(FavoriteTrackDao,userDao);
    }

    @Provides
    @Singleton
    public FilterRepository provideFilterRepository(FilterDao filterDao, UserDao userDao) {
        return new FilterRepositoryImpl(filterDao, userDao);
    }

    @Provides
    @Singleton
    public UserRepository provideUserRepository(UserDao userDao) {
        return new UserRepositoryImpl(userDao);
    }

    @Provides
    @Singleton
    public SettingsRepository provideSettingsRepository(SettingsDao settingsDao, UserDao userDao) {
        return new SettingsRepositoryImpl(settingsDao, userDao);
    }
    @Provides
    @Singleton
    public HistoryRepository provideHistoryRepository(HistoryDao historyDao, UserDao userDao) {
        return new HistoryRepositoryImpl(historyDao, userDao);
    }

    @Provides
    @Singleton
    public MapRepository provideMapRepository(MapDao mapDao){
        return new MapRepositoryImpl(mapDao);
    }
}

