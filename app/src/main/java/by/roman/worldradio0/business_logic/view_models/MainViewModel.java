package by.roman.worldradio0.business_logic.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.RadioRepository;
import by.roman.worldradio0.business_logic.network.radioapi.LoadDataFromAPI;
import by.roman.worldradio0.business_logic.network.radioapi.StationsCallback;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private final RadioRepository radioRepository;
    private final LoadDataFromAPI loadDataFromAPI;
    private final MutableLiveData<UiState<List<RadioStation>>> allStations = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public LiveData<UiState<List<RadioStation>>> getAllStations() {
        return allStations;
    }
    @Inject
    public MainViewModel(RadioRepository radioRepository, LoadDataFromAPI loadDataFromAPI){
        this.radioRepository = radioRepository;
        this.loadDataFromAPI = loadDataFromAPI;
        loadAll();
    }
    public void loadAll(){
        allStations.setValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<RadioStation> list = radioRepository.getAllStations();
                allStations.postValue(UiState.success(list));
            } catch (Exception e) {
                allStations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
    }
    public List<RadioStation> getAll(){
        return radioRepository.getAllStations();
    }
    public void loadFromAPI() {
        new Thread(() -> {
            loadDataFromAPI.getStations(new StationsCallback() {
                @Override
                public void onSuccess(List<RadioStationDTO> stations) {
                    for (RadioStationDTO dto : stations) {
                        try {
                            radioRepository.addRadioStation(dto);
                        } catch (Exception e) {
                            Log.e("DB", "Ошибка при добавлении: " + dto.getName(), e);
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e("API", "Ошибка загрузки данных", t);
                }
            });
        }).start();

    }
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
