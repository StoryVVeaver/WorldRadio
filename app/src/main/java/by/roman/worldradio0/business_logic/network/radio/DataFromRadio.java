package by.roman.worldradio0.business_logic.network.radio;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.network.radio.callbacks.ClickCallback;
import by.roman.worldradio0.business_logic.network.radio.callbacks.StationsCallback;
import by.roman.worldradio0.business_logic.network.radio.callbacks.VoteCallback;

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
    public void click(String uuid, ClickCallback callback){
        radio.click(uuid, new ClickCallback() {
            @Override
            public void onSuccess(ClickModel t) {
                callback.onSuccess(t);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }
    public void vote(String uuid, VoteCallback callback){
        radio.vote(uuid, new VoteCallback() {
            @Override
            public void onSuccess(VoteModel t) {
                callback.onSuccess(t);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}

