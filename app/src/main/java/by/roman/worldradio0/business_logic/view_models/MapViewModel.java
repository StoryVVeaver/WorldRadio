package by.roman.worldradio0.business_logic.view_models;

import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.osmdroid.util.BoundingBox;

import java.util.List;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.models.MapPoint;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.MapRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

// MapViewModel.java
@HiltViewModel
public class MapViewModel extends ViewModel {
    private final MapRepository repository;
    private final MutableLiveData<UiState<List<MapPoint>>> points = new MutableLiveData<>();

    @Inject
    public MapViewModel(MapRepository mapRepository) {
        this.repository = mapRepository;
    }

    public LiveData<UiState<List<MapPoint>>> getListPoints() {
        return points;
    }

    public void loadPoints(){
        points.postValue(UiState.loading());
        try {
            List<MapPoint> list = repository.getPoints();
            points.postValue(UiState.success(list));
        } catch (Exception e) {
            points.postValue(UiState.error(e.getMessage()));
            Log.e("MapViewModel", "ERROR: " + e.getMessage());
        }
    }
    public MapPoint getMapPointByUUID(String uuid){
        try {
            return repository.getPointByUUID(uuid);
        } catch (Exception e) {
            Log.e("MapViewModel", "ERROR: " + e.getMessage());
            return null;
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }
}