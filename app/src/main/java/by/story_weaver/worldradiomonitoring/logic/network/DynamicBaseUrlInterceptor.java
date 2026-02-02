package by.story_weaver.worldradiomonitoring.logic.network;


import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class DynamicBaseUrlInterceptor implements Interceptor {

    private final UrlProvider provider;

    public DynamicBaseUrlInterceptor(UrlProvider provider) {
        this.provider = provider;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        String randomUrl = provider.getRandom();
        Log.v("Interceptor",randomUrl);
        HttpUrl currentBase = HttpUrl.parse(randomUrl);

        if (currentBase == null) {
            return chain.proceed(original);
        }

        HttpUrl newUrl = original.url().newBuilder()
                .scheme(currentBase.scheme())
                .host(currentBase.host())
                .port(currentBase.port())
                .build();

        Request newRequest = original.newBuilder()
                .url(newUrl)
                .build();

        try {
            return chain.proceed(newRequest);
        } catch (Exception e) {
            provider.switchOnError();
            throw e;
        }
    }
}


