package by.roman.worldradio0.business_logic.network.userAPI.callbacks;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.FavoriteStationDTO;

public interface DeleteCallback {
    void onSuccess(String t);
    void onFailure(Throwable t);
}
