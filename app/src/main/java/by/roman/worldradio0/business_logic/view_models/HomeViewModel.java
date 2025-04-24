package by.roman.worldradio0.business_logic.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.models.User;
import by.roman.worldradio0.business_logic.data.repositories.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.UserRepository;
import by.roman.worldradio0.business_logic.network.radioapi.LoadDataFromAPI;
import by.roman.worldradio0.business_logic.network.radioapi.StationsCallback;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private final RadioRepository radioRepository;
    private final UserRepository userRepository;
    private final LoadDataFromAPI loadDataFromAPI;
    private final MutableLiveData<UiState<List<RadioStation>>> stations = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private int currentPage = 0;
    private boolean isLastPage = false;
    private final int pageSize = 50;
    public boolean getIsLastPage() {
        return isLastPage;
    }
    @Inject
    public HomeViewModel(RadioRepository radioRepository, LoadDataFromAPI loadDataFromAPI, UserRepository userRepository){
        this.radioRepository = radioRepository;
        this.userRepository = userRepository;
        this.loadDataFromAPI = loadDataFromAPI;
        loadAll();
    }
    public LiveData<UiState<List<RadioStation>>> getAllStations() {
        return stations;
    }
    private void loadAll(){
        stations.setValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<RadioStation> list = radioRepository.getAllStations(currentPage,pageSize);
                list.isEmpty(); // вызов ошибки
                stations.postValue(UiState.success(list));
                currentPage++;
            } catch (Exception e) {
                stations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
    }
    public void loadNextPage() {
        if (isLastPage) return;
        executor.execute(() -> {
            try {
                List<RadioStation> list = radioRepository.getAllStations(currentPage, pageSize);
                if (list.isEmpty()) {
                    isLastPage = true;
                } else {
                    List<RadioStation> currentList = stations.getValue() != null && stations.getValue().data != null
                            ? new ArrayList<>(stations.getValue().data)
                            : new ArrayList<>();
                    currentList.addAll(list);
                    stations.postValue(UiState.success(currentList));
                    currentPage++;
                }
            } catch (Exception e) {
                stations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
    }
    public void loadFromAPI() {
        executor.execute(() -> loadDataFromAPI.getStations(new StationsCallback() {
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
        }));

    }
    public void useradd(){
        UserDTO dto = new UserDTO();
        dto.fromModel(new User(1,"user","user",null,1));
        userRepository.useradd(dto);
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
