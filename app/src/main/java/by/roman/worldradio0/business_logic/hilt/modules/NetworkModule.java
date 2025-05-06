package by.roman.worldradio0.business_logic.hilt.modules;

import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.network.radio.DataFromRadio;
import by.roman.worldradio0.business_logic.network.userAPI.DataFromUserAPI;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    @Provides
    @Singleton
    public static DataFromRadio provideLoadDataFromAPI() {
        return new DataFromRadio();
    }
    @Provides
    @Singleton
    public static DataFromUserAPI provideDataFromUserAPI(){
        return new DataFromUserAPI();
    }
}
