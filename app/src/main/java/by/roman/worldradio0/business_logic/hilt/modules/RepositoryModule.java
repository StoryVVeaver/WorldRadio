package by.roman.worldradio0.business_logic.hilt.modules;

import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.data.database.FavoriteDao;
import by.roman.worldradio0.business_logic.data.database.FilterDao;
import by.roman.worldradio0.business_logic.data.database.RadioStationDao;
import by.roman.worldradio0.business_logic.data.database.SettingsDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteRepository;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FilterRepository;
import by.roman.worldradio0.business_logic.data.repositories.FilterRepositoryImpl;
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
    public RadioRepository provideRadioRepository(RadioStationDao radioStationDao, FavoriteDao favoriteDao, UserDao userDao, FilterDao filterDao) {
        return new RadioRepositoryImpl(radioStationDao, favoriteDao, userDao, filterDao);
    }

    @Provides
    @Singleton
    public FavoriteRepository provideFavoriteRepository(FavoriteDao favoriteDao, UserDao userDao) {
        return new FavoriteRepositoryImpl(favoriteDao, userDao);
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
}

