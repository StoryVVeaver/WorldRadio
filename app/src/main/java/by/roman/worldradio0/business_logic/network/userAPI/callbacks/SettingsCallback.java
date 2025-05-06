package by.roman.worldradio0.business_logic.network.userAPI.callbacks;


import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;

public interface SettingsCallback {
    void onSuccess(SettingsDTO settings);
    void onFailure(Throwable t);
}
