package by.roman.worldradio0.business_logic.network.userAPI;


import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.FavoriteStationDTO;
import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;
import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.UserRequest;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.FavoritesCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.FiltersCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.RequestCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.SettingsCallback;

public class DataFromUserAPI {
    private final UserAPI userAPI;

    public DataFromUserAPI(){
        this.userAPI = new UserAPI();
    }
    public void getFilters(int id, FiltersCallback callback){
        userAPI.fetchFilters(id, new FiltersCallback() {
            @Override
            public void onSuccess(FilterDTO dto) {
                callback.onSuccess(dto);
            }
            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
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
    public void getFavorites(int id, FavoritesCallback callback){
        userAPI.fetchFavorites(id, new FavoritesCallback() {
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
}
