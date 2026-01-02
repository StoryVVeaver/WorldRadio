package by.roman.worldradio0.business_logic.network.userAPI.callbacks;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.FavoriteTrackDTO;

public interface FavoriteTrackCallback {
    void onSuccess(List<FavoriteTrackDTO> favoriteTracks);
    void onFailure(Throwable t);
}
