package by.roman.worldradio0.business_logic.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.models.MapPoint;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.MapRepository;
import by.roman.worldradio0.business_logic.network.radio.DataFromRadio;
import by.roman.worldradio0.business_logic.network.radio.callbacks.RadioCallback;
import dagger.hilt.android.lifecycle.HiltViewModel;

// Обновленный MapViewModel.java
@HiltViewModel
public class MapViewModel extends ViewModel {
    private final DataFromRadio dataFromRadio;
    private final MutableLiveData<UiState<List<MapPoint>>> points = new MutableLiveData<>();
    // Кэш для хранения всех загруженных точек (чтобы не пропадали при сдвиге)
    private final java.util.Map<String, MapPoint> allPointsCache = new java.util.concurrent.ConcurrentHashMap<>();

    @Inject
    public MapViewModel(DataFromRadio dataFromRadio) {
        this.dataFromRadio = dataFromRadio;
    }

    public LiveData<UiState<List<MapPoint>>> getListPoints() {
        return points;
    }

    public void loadPointsByLocation(double lat, double lon, double distance) {
        int limit = (distance > 500000) ? 500 : 100;

        dataFromRadio.getStationsByLocation(limit, lat, lon, distance, new RadioCallback<>() {
            @Override
            public void onSuccess(List<RadioStation> list) {
                if (list != null) {
                    for (RadioStation p : list) {
                        allPointsCache.put(p.getStationUuid(), MapPoint.createFrom(p));
                    }
                    points.postValue(UiState.success(new ArrayList<>(allPointsCache.values())));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("MapViewModel", "API Error: " + t.getMessage());
                if (allPointsCache.isEmpty()) {
                    points.postValue(UiState.error(t.getMessage()));
                }
            }

            @Override
            public void onLoading() {
                if (allPointsCache.isEmpty()) {
                    points.postValue(UiState.loading());
                }
            }
        });
    }

    public void loadPoints() {
    }

    public MapPoint getMapPointByUUID(String uuid) {
        return allPointsCache.get(uuid);
    }
}