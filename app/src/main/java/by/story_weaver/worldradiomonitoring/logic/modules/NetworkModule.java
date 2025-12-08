package by.story_weaver.worldradiomonitoring.logic.modules;

import javax.inject.Singleton;

import by.story_weaver.worldradiomonitoring.logic.network.UserApi;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    //private static final String BASE_URL = "http://192.168.0.85:8080/api/";
    private static final String BASE_URL = "https://kkvxmvg9-8080.euw.devtunnels.ms/api/";
    //private static final String BASE_URL = "http://192.168.43.146:8080/api/";

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public UserApi provideUserApi(Retrofit retrofit) {
        return retrofit.create(UserApi.class);
    }

}