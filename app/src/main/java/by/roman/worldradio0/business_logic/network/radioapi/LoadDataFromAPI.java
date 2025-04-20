package by.roman.worldradio0.business_logic.network.radioapi;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;

public class LoadDataFromAPI {
    private final RemoteDataSource remoteDataSource;

    public LoadDataFromAPI() {
        this.remoteDataSource = new RemoteDataSource();
    }

    public void getStations(StationsCallback callback) {
        remoteDataSource.fetchStations(new StationsCallback() {
            @Override
            public void onSuccess(List<RadioStationDTO> stations) {
                callback.onSuccess(stations);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}

