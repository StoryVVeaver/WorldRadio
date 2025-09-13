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
import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;
import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.models.User;
import by.roman.worldradio0.business_logic.data.models.UserRequest;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FilterRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.SettingsRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.network.radio.DataFromRadio;
import by.roman.worldradio0.business_logic.network.radio.Model;
import by.roman.worldradio0.business_logic.network.radio.StationsCallback;
import by.roman.worldradio0.business_logic.network.userAPI.DataFromUserAPI;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.RequestCallback;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AccountViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;
    private final RadioRepository radioRepository;
    private final FilterRepository filterRepository;
    private final DataFromUserAPI dataFromUserAPI;
    private final DataFromRadio dataFromRadio;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final MutableLiveData<UiState<Boolean>> result = new MutableLiveData<>();
    private final MutableLiveData<UiState<Integer>> stationsLoading = new MutableLiveData<>();

    @Inject
    public AccountViewModel(UserRepository userRepository, SettingsRepository settingsRepository,
                            FilterRepository filterRepository, DataFromUserAPI dataFromUserAPI,
                            RadioRepository radioRepository, DataFromRadio dataFromRadio){
        this.userRepository = userRepository;
        this.radioRepository = radioRepository;
        this.dataFromRadio = dataFromRadio;
        this.settingsRepository = settingsRepository;
        this.filterRepository = filterRepository;
        this.dataFromUserAPI = dataFromUserAPI;
    }
    public LiveData<UiState<Boolean>> getUser(){
        return result;
    }
    public LiveData<UiState<Integer>> getStationsLoading() {
        return stationsLoading;
    }
    public int isUserHere(){
        return userRepository.getUserInSystem();
    }
    public boolean hasRecords(){
        return radioRepository.hasRecords();
    }
    public void reg(UserRequest userRequest){
        Log.d("AccountViewModel: reg","Start request");
        result.postValue(UiState.loading());
        executor.execute(() -> dataFromUserAPI.regUser(userRequest, new RequestCallback() {
            @Override
            public void onSuccess(UserDTO dto) {
                try {
                    userRepository.useradd(dto);
                    userRepository.setUserInSystem(dto.getId());
                    settingsRepository.addSettings(new SettingsDTO().fromModel(new Settings(dto.getId())));
                    filterRepository.addFilters(new FilterDTO().fromModel(new Filter(dto.getId(),null,null,null,null,null,0)));
                    result.postValue(UiState.success(true));
                } catch (Exception e) {
                    result.postValue(UiState.error(e.getMessage()));
                    Log.e("AccountViewModel: reg", "Failed get user");
                }
            }
            @Override
            public void onFailure(String text) {
                result.postValue(UiState.error(text));
            }
        }));
    }
    public void enter(UserRequest userRequest){
        Log.d("AccountViewModel: enter","Start request");
        result.postValue(UiState.loading());
        executor.execute(() -> dataFromUserAPI.enterUser(userRequest, new RequestCallback() {
            @Override
            public void onSuccess(UserDTO dto) {
                try {
                    userRepository.useradd(dto);
                    userRepository.setUserInSystem(dto.getId());
                    settingsRepository.addSettings(new SettingsDTO().fromModel(new Settings(dto.getId())));
                    filterRepository.addFilters(new FilterDTO().fromModel(new Filter(dto.getId(),null,null,null,null,null,0)));
                    result.postValue(UiState.success(true));
                } catch (Exception e) {
                    result.postValue(UiState.error(e.getMessage()));
                    Log.e("AccountViewModel: enter", "Failed get user");
                }
            }
            @Override
            public void onFailure(String text) {
                result.postValue(UiState.error(text));
                Log.e("AccountViewModel: enter", "Ошибка загрузки данных" + text);
            }
        }));
    }
    public void useradd(){
        UserDTO dto = new UserDTO();
        dto.fromModel(new User(1,"user","user",null,1));
        userRepository.useradd(dto);
        settingsRepository.addSettings(new SettingsDTO().fromModel(new Settings(dto.getId())));
        filterRepository.addFilters(new FilterDTO().fromModel(new Filter(dto.getId(),null,null,null,null,null,0)));
    }
    public void loadStations(){
        if(!hasRecords()){
            stationsLoading.postValue(UiState.loading(0));
            dataFromRadio.getStations(new StationsCallback() {
                @Override
                public void onLoading() {
                }
                @Override
                public void onSuccess(List<RadioStationDTO> stations) {
                    long i = 0;
                    for (RadioStationDTO dto : stations) {
                        try {
                            radioRepository.addRadioStation(dto);
                            i++;
                            stationsLoading.postValue(UiState.loading((int) (i * 100) / stations.size()));
                        } catch (Exception e) {
                            Log.e("DB", "Ошибка при добавлении: " + dto.getName(), e);
                            stationsLoading.postValue(UiState.error("Уведомите разработчика о ошибке загрузки"));
                        }
                        stationsLoading.postValue(UiState.success(100));
                    }
                }
                @Override
                public void onFailure(Throwable t) {
                    stationsLoading.postValue(UiState.error("Retry load later"));
                }
            });
        } else stationsLoading.postValue(UiState.success(100));
    }
}
