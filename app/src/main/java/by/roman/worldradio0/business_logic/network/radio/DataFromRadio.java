package by.roman.worldradio0.business_logic.network.radio;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;

public class DataFromRadio {
    private final radio radio;

    public DataFromRadio() {
        this.radio = new radio();
    }
    public void getStations(StationsCallback callback) {
        radio.fetchStations(new StationsCallback() {
            @Override
            public void onSuccess(List<RadioStationDTO> stations) {
                callback.onSuccess(stations);
            }
            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
            @Override
            public void onLoading(){
                callback.onLoading();
            }
        });
    }
}

