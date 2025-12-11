package by.story_weaver.worldradiomonitoring.logic.modules;

import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import by.story_weaver.worldradiomonitoring.logic.network.DynamicBaseUrlInterceptor;
import by.story_weaver.worldradiomonitoring.logic.network.RadioApi;
import by.story_weaver.worldradiomonitoring.logic.network.UrlProvider;
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

    @Provides
    @Singleton
    @Named("staticClient")
    OkHttpClient provideStaticClient() {
        return new OkHttpClient.Builder().build();
    }

    @Provides
    @Singleton
    @Named("staticRetrofit")
    Retrofit provideStaticRetrofit(@Named("staticClient") OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("https://kkvxmvg9-8080.euw.devtunnels.ms/api/v1/user/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    UserApi provideUserApi(@Named("staticRetrofit") Retrofit retrofit) {
        return retrofit.create(UserApi.class);
    }

    @Provides
    @Singleton
    UrlProvider provideUrlProvider() {
        return new UrlProvider();
    }

    @Provides
    @Singleton
    @Named("dynamicClient")
    OkHttpClient provideDynamicClient(UrlProvider provider) {
        return new OkHttpClient.Builder()
                .addInterceptor(new DynamicBaseUrlInterceptor(provider))
                .build();
    }

    @Provides
    @Singleton
    @Named("dynamicRetrofit")
    Retrofit provideDynamicRetrofit(@Named("dynamicClient") OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("https://placeholder/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    RadioApi provideRadioApi(@Named("dynamicRetrofit") Retrofit retrofit) {
        return retrofit.create(RadioApi.class);
    }
}


