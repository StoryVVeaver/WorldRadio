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
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.network.radio.DataFromRadio;
import by.roman.worldradio0.business_logic.network.radio.StationsCallback;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private final RadioRepository radioRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<UiState<List<RadioStation>>> stations = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private int currentPage = 0;
    private boolean isLastPage = false;
    private final int pageSize = 50;
    public boolean getIsLastPage() {
        return isLastPage;
    }
    @Inject
    public HomeViewModel(RadioRepository radioRepository, DataFromRadio loadDataFromRadio, UserRepository userRepository){
        this.radioRepository = radioRepository;
        this.userRepository = userRepository;
        loadAll();
    }
    public LiveData<UiState<List<RadioStation>>> getAllStations() {
        return stations;
    }
    private void loadAll(){
        stations.setValue(UiState.loading());
        executor.execute(() -> {
            try {
                Log.d("HomeVW", "before_page: " + currentPage);
                List<RadioStation> list = radioRepository.getAllStations(currentPage,pageSize);
                list.isEmpty(); // вызов ошибки
                stations.postValue(UiState.success(list));
                Log.d("HomeVW", "after_page: " + currentPage);
            } catch (Exception e) {
                stations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
    }
    public void loadNextPage() {
        if (isLastPage) return;
        executor.execute(() -> {
            try {
                Log.d("HomeVW", "before_page: " + currentPage);
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
                    Log.d("HomeVW", "after_page: " + currentPage);
                }
            } catch (Exception e) {
                stations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
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
