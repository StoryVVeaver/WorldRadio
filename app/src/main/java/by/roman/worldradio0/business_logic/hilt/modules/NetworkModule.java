package by.roman.worldradio0.business_logic.hilt.modules;

import android.content.Context;

import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.network.radio.DataFromRadio;
import by.roman.worldradio0.business_logic.network.radio.radio;
import by.roman.worldradio0.business_logic.network.userAPI.DataFromUserAPI;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    @Provides
    @Singleton
    public static radio provideradio(@ApplicationContext Context context) {
        return new radio(context);
    }
    @Provides
    @Singleton
    public static DataFromRadio provideLoadDataFromAPI(radio radio) {
        return new DataFromRadio(radio);
    }
    @Provides
    @Singleton
    public static DataFromUserAPI provideDataFromUserAPI(){
        return new DataFromUserAPI();
    }
}
