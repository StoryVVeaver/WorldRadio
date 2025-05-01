package by.roman.worldradio0.business_logic.hilt.modules;

import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.network.radioapi.LoadDataFromAPI;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    @Provides
    @Singleton
    public static LoadDataFromAPI provideLoadDataFromAPI() {
        return new LoadDataFromAPI();
    }

}
