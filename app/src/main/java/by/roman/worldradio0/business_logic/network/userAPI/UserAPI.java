package by.roman.worldradio0.business_logic.network.userAPI;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.collection.ObjectFloatMapKt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.data.dto.FavoriteStationDTO;
import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;
import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.FavoriteStation;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.models.User;
import by.roman.worldradio0.business_logic.data.models.UserRequest;
import by.roman.worldradio0.business_logic.network.radio.Model;
import by.roman.worldradio0.business_logic.network.radio.TagsAdapter;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.FavoritesCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.FiltersCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.PutCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.RequestCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.SettingsCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Singleton
public class UserAPI {
    //private static final String API_URL = "http://192.168.43.146:8080/api/v1/user";
    //private static final String API_URL = "http://192.168.0.85:8080/api/v1/user";
    private static final String API_URL = "https://shiny-snails-go.loca.lt/api/v1/user";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public UserAPI(){

    };
    public void fetchFilters(int id, FiltersCallback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL + "/get/filters/" + id)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try (response){
                        String jsonResponse = response.body().string();
                        if (jsonResponse.isEmpty()) {
                            Log.e("UserAPI: filters", "Empty response body.");
                            callback.onFailure(new Exception("Empty response body"));
                            return;
                        }
                        Log.d("UserAPI: filters", "Response: " + jsonResponse);

                        if (response.code() != 200) {
                            Log.e("UserAPI: filters", "Response code: " + response.code());
                            return;
                        }
                        Gson gson = new GsonBuilder().create();
                        try {
                            if(jsonResponse.startsWith("{")){
                                Filter filter = gson.fromJson(jsonResponse, Filter.class);
                                FilterDTO dto = new FilterDTO();
                                dto.fromModel(filter);
                                callback.onSuccess(dto);
                            }
                        } catch (Exception e) {
                            Log.e("UserAPI: filters", "JSON parsing error", e);
                            callback.onFailure(e);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure(e);
                    }
                } else {
                    Log.e("UserAPI: filters", "Request failed with code: " + response.code());
                    callback.onFailure(new Exception("Request failed with code: " + response.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }
    public void fetchFavorites(int id, FavoritesCallback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL + "/get/favorites/" + id)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try (response){
                        assert response.body() != null;
                        String jsonResponse = response.body().string();
                        Log.d("UserAPI: favorites", "Response: " + jsonResponse);

                        if (response.code() != 200) {
                            Log.e("UserAPI: favorites", "Response code: " + response.code());
                            return;
                        }
                        if (jsonResponse.isEmpty()) {
                            Log.e("UserAPI: favorites", "Empty response body.");
                            callback.onFailure(new Exception("Empty response body"));
                            return;
                        }
                        Gson gson = new GsonBuilder().create();
                        try {
                            if(jsonResponse.startsWith("[")){
                                FavoriteStation[] favoriteStations = gson.fromJson(jsonResponse, FavoriteStation[].class);
                                List<FavoriteStationDTO> dto = new ArrayList<>();
                                for (FavoriteStation i : favoriteStations) {
                                    dto.add(new FavoriteStationDTO().fromModel(i));
                                }
                                callback.onSuccess(dto);
                            }
                        } catch (Exception e) {
                            Log.e("UserAPI: filters", "JSON parsing error", e);
                            callback.onFailure(e);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure(e);
                    }
                } else {
                    Log.e("UserAPI: filters", "Request failed with code: " + response.code());
                    callback.onFailure(new Exception("Request failed with code: " + response.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }
    public void fetchSettings(int id, SettingsCallback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL + "/get/settings/" + id)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try (response){
                        assert response.body() != null;
                        String jsonResponse = response.body().string();
                        Log.d("UserAPI: settings", "Response: " + jsonResponse);

                        if (response.code() != 200) {
                            Log.e("UserAPI: settings", "Response code: " + response.code());
                            return;
                        }
                        if (jsonResponse.isEmpty()) {
                            Log.e("UserAPI: settings", "Empty response body.");
                            callback.onFailure(new Exception("Empty response body"));
                            return;
                        }
                        Gson gson = new GsonBuilder().create();
                        try {
                            if(jsonResponse.startsWith("{")){
                                Settings settings = gson.fromJson(jsonResponse, Settings.class);
                                SettingsDTO dto = new SettingsDTO();
                                dto.fromModel(settings);
                                callback.onSuccess(dto);
                            }
                        } catch (Exception e) {
                            Log.e("UserAPI: settings", "JSON parsing error", e);
                            callback.onFailure(e);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callback.onFailure(e);
                    }
                } else {
                    Log.e("UserAPI: settings", "Request failed with code: " + response.code());
                    callback.onFailure(new Exception("Request failed with code: " + response.code()));
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }
    public void regUser(UserRequest userRequest,RequestCallback callback){
        Gson gson = new Gson();
        String jsonBody;
        jsonBody = gson.toJson(userRequest);
        Log.d("UserAPI","JSON created" + jsonBody);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(jsonBody,JSON);
        Request request = new Request.Builder()
                .url(API_URL + "/reg")
                .post(requestBody)
                .build();
        Log.d("UserAPI","Request created");
        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response){
                if (response.isSuccessful()) {
                    try (response){
                        assert response.body() != null;
                        String jsonResponse = response.body().string();
                        Log.d("UserAPI: reg", "Response: " + jsonResponse);

                        if (response.code() != 200) {
                            Log.e("UserAPI: reg", "Response code: " + response.code());
                            callback.onFailure("code: " + response.code());
                            return;
                        }
                        if (jsonResponse.isEmpty()) {
                            Log.e("UserAPI: reg", "Empty response body.");
                            callback.onFailure("Empty response body");
                            return;
                        }
                        Gson gson = new GsonBuilder().create();
                        try {
                            Log.d("UserAPI: reg", jsonResponse);
                            if (jsonResponse.startsWith("{")) {
                                User user = gson.fromJson(jsonResponse, User.class);
                                callback.onSuccess(new UserDTO().fromModel(user));
                            } else {
                                Log.e("UserAPI: reg", "Unexpected response: " + jsonResponse);
                                callback.onFailure(jsonResponse);
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e("UserAPI: reg", "JSON parsing error", e);
                            callback.onFailure(e.getMessage());
                        }
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e.getMessage());
            }
        });
    }
    public void enterUser(UserRequest userRequest, RequestCallback callback){
        Gson gson = new Gson();
        String jsonBody;
        jsonBody = gson.toJson(userRequest);
        Log.d("UserAPI: enter","JSON created" + jsonBody);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(jsonBody,JSON);
        Request request = new Request.Builder()
                .url(API_URL + "/enter")
                .post(requestBody)
                .build();
        Log.d("UserAPI: enter","Request created");

        client.newCall(request).enqueue(new Callback(){
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response){
                if (response.isSuccessful()) {
                    try (response){
                        assert response.body() != null;
                        String jsonResponse = response.body().string();
                        Log.d("UserAPI: enter", "Response: " + jsonResponse);

                        if (response.code() != 200) {
                            callback.onFailure("code: " + response.code());
                            Log.e("UserAPI: enter", "Response code: " + response.code());
                            return;
                        }
                        if (jsonResponse.isEmpty()) {
                            Log.e("UserAPI: enter", "Empty response body.");
                            callback.onFailure("Empty response body");
                            return;
                        }
                        Gson gson = new GsonBuilder().create();
                        try {
                            Log.d("UserAPI: enter", jsonResponse);
                            if (jsonResponse.startsWith("{")) {
                                User user = gson.fromJson(jsonResponse, User.class);
                                callback.onSuccess(new UserDTO().fromModel(user));
                            } else {
                                Log.e("UserAPI: enter", "Unexpected response: " + jsonResponse);
                                callback.onFailure(jsonResponse);
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e("UserAPI: enter", "JSON parsing error", e);
                            callback.onFailure(e.getMessage());
                        }
                    } catch (IOException e) {
                        callback.onFailure(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                callback.onFailure(e.getMessage());
            }
        });
    }
    public void putFilters(Filter filter, PutCallback callback){
        Gson gson = new Gson();
        String jsonBody;
        jsonBody = gson.toJson(filter);
        Log.d("UserAPI","JSON created" + jsonBody);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(jsonBody,JSON);
        Request request = new Request.Builder()
                .url(API_URL + "/put/filters")
                .put(requestBody)
                .build();
        Log.d("UserAPI","Request created");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    try (response){
                        if(response.body() != null){
                            if(response.body().string().equals("saved")){
                                callback.onSuccess("saved");
                            } else callback.onFailure(new Exception(response.body().string()));
                        } else callback.onFailure(new Exception("error"));
                    } catch (Exception e) {
                        callback.onFailure(e);
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }
        });
    }
    public void putFavorites(List<FavoriteStation> list, PutCallback callback){
        Gson gson = new Gson();
        String jsonBody;
        jsonBody = gson.toJson(list);
        Log.d("UserAPI","JSON created" + jsonBody);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(jsonBody,JSON);
        Request request = new Request.Builder()
                .url(API_URL + "/put/favorites")
                .put(requestBody)
                .build();
        Log.d("UserAPI","Request created");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()){
                    try (response){
                        if(response.body() != null){
                            if(response.body().string().equals("saved")){
                                callback.onSuccess("saved");
                            } else callback.onFailure(new Exception(response.body().string()));
                        } else callback.onFailure(new Exception("error"));
                    } catch (Exception e) {
                        callback.onFailure(e);
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }
        });
    }
    public void putSettings(Settings settings, PutCallback callback){
        Gson gson = new Gson();
        String jsonBody;
        jsonBody = gson.toJson(settings);
        Log.d("UserAPI","JSON created" + jsonBody);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(jsonBody,JSON);
        Request request = new Request.Builder()
                .url(API_URL + "/put/settings")
                .put(requestBody)
                .build();
        Log.d("UserAPI","Request created");
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()){
                    try (response){
                        if(response.body() != null){
                            if(response.body().string().equals("saved")){
                                callback.onSuccess("saved");
                            } else callback.onFailure(new Exception(response.body().string()));
                        } else callback.onFailure(new Exception("error"));
                    } catch (Exception e) {
                        callback.onFailure(e);
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }
        });
    }
    public void deleteUser(int id){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL + "/delete/" + id)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    try (response){
                        if(response.body() != null){
                            String jsonResponse = response.body().string();
                            if (jsonResponse.isEmpty()) {
                                Log.e("UserAPI: delete", "Empty response body.");
                                return;
                            }
                            Log.d("UserAPI: delete", "Response: " + jsonResponse);

                            if (response.code() != 200) {
                                Log.e("UserAPI: delete", "Response code: " + response.code());
                                return;
                            }
                        } else Log.e("UserAPI: delete", "Empty response body.");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
        });
    }
}