package by.roman.worldradio0.business_logic.network.userAPI.callbacks;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.FavoriteStationDTO;


public interface FavoritesCallback {
    void onSuccess(List<FavoriteStationDTO> favoriteStations);
    void onFailure(Throwable t);
}
