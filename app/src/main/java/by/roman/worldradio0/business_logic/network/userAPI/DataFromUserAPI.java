package by.roman.worldradio0.business_logic.network.userAPI;


import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.FavoriteStationDTO;
import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;
import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.FavoriteStation;
import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.models.User;
import by.roman.worldradio0.business_logic.data.models.UserRequest;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.FavoriteStationsCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.FilterCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.PutCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.RequestCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.SettingsCallback;

public class DataFromUserAPI {
    private final UserAPI userAPI;

    public DataFromUserAPI(){
        this.userAPI = new UserAPI();
    }
    public void getSettings(int id, SettingsCallback callback){
        userAPI.fetchSettings(id, new SettingsCallback() {
            @Override
            public void onSuccess(SettingsDTO settings) {
                callback.onSuccess(settings);
            }
            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }
    public void getFavoriteStations(int id, FavoriteStationsCallback callback){
        userAPI.fetchFavoriteStations(id, new FavoriteStationsCallback() {
            @Override
            public void onSuccess(List<FavoriteStationDTO> favoriteStations) {
                callback.onSuccess(favoriteStations);
            }
            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }
    public void getStationsFilter(FilterCallback callback){
        userAPI.fetchFilters(new FilterCallback() {
            @Override
            public void onSuccess(List<String> t) {
                callback.onSuccess(t);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }
    public void regUser(UserRequest userRequest, RequestCallback callback){
        userAPI.regUser(userRequest, new RequestCallback() {
            @Override
            public void onSuccess(UserDTO dto) {
                callback.onSuccess(dto);
            }

            @Override
            public void onFailure(String text) {
                callback.onFailure(text);
            }
        });
    }
    public void enterUser(UserRequest userRequest, RequestCallback callback){
        userAPI.enterUser(userRequest, new RequestCallback() {
            @Override
            public void onSuccess(UserDTO dto) {
                callback.onSuccess(dto);
            }

            @Override
            public void onFailure(String text) {
                callback.onFailure(text);
            }
        });
    }
    public void putSettings(Settings settings, PutCallback callback){
        userAPI.putSettings(settings, new PutCallback() {
            @Override
            public void onSuccess(String t) {
                callback.onSuccess(t);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }
    public void putFavoriteStations(List<FavoriteStation> list, PutCallback callback){
        userAPI.putFavoriteStations(list, new PutCallback() {
            @Override
            public void onSuccess(String t) {
                callback.onSuccess(t);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }
    public void putUser(User user, PutCallback callback){
        userAPI.putUser(user, new PutCallback() {
            @Override
            public void onSuccess(String t) {
                callback.onSuccess(t);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }
    public void deleteUser(int id){
        userAPI.deleteUser(id);
    }
}
