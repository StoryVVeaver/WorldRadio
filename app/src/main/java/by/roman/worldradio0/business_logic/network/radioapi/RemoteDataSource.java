package by.roman.worldradio0.business_logic.network.radioapi;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public class RemoteDataSource {
    private static final String API_URL = "http://at1.api.radio-browser.info/json/stations";


    public RemoteDataSource() {
    }

    public void fetchStations (StationsCallback callback) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    try (response) {
                        assert response.body() != null;
                        String jsonResponse = response.body().string();
                        Log.d("RadioAPI", "Response: " + jsonResponse);

                        if (response.code() != 200) {
                            Log.e("RadioAPI", "Response code isn't 200");
                            return;
                        }
                        if (jsonResponse.isEmpty()) {
                            Log.e("RadioAPI", "Empty response body.");
                            callback.onFailure(new Exception("Empty response body"));
                            return;
                        }

                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(new TypeToken<List<String>>() {
                                }.getType(), new TagsAdapter())
                                .create();

                        List<Model> stationList = new ArrayList<>();

                        try {
                            if (jsonResponse.startsWith("[")) {
                                Model[] stations = gson.fromJson(jsonResponse, Model[].class);
                                stationList.addAll(Arrays.asList(stations));
                                List<RadioStationDTO> dto = new ArrayList<>();
                                for (Model i : stations) {
                                    dto.add(new RadioStationDTO().fromModel(i));
                                }
                                callback.onSuccess(dto);
                            } else {
                                Log.e("RadioAPI", "Unexpected response: " + jsonResponse);
                                callback.onFailure(new Exception("Unexpected response: " + jsonResponse));
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e("RadioAPI", "JSON parsing error", e);
                            callback.onFailure(e);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure(e);
                    }
                } else {
                    Log.e("RadioAPI", "Request failed with code: " + response.code());
                    callback.onFailure(new Exception("Request failed with code: " + response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }
}
