package by.roman.worldradio0.business_logic.network.radio;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;

public interface StationsCallback {
    void onSuccess(List<RadioStationDTO> stations);
    void onFailure(Throwable t);
}

