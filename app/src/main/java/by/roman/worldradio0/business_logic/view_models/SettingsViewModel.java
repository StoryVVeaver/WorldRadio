package by.roman.worldradio0.business_logic.view_models;

import static by.roman.worldradio0.business_logic.settings.SettingsKeys.AGC_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.AUDIO_BALANCE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.CROSSFADE_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.CROSSFADE_TIME;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.DELETE_ACCOUNT;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.EXIT_FROM_ACCOUNT;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.GAIN_BROADCAST;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.GAIN_RECORD;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.GET_USER_DATA;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.NAVIGATION_TYPE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.NETWORK_TYPE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.NOTIFICATION_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.PUT_USER_DATA;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.TIMER_DOTS_TYPE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.TIMER_SECONDS_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.UPDATE_STATIONS_DATA;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.dto.FavoriteStationDTO;
import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;
import by.roman.worldradio0.business_logic.data.models.History;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.models.User;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsGroup;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckWIthSliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.SliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.SwitchItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.TextButtonItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.TextItem;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteStationRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FilterRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.HistoryRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.SettingsRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.network.radio.DataFromRadio;
import by.roman.worldradio0.business_logic.network.radio.StationsCallback;
import by.roman.worldradio0.business_logic.network.userAPI.DataFromUserAPI;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.FavoritesCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.FiltersCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.PutCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.SettingsCallback;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {
    private final MutableLiveData<Boolean> timeToLeave = new MutableLiveData<>();
    private final MutableLiveData<UiState<Integer>> count = new MutableLiveData<>();
    private final MutableLiveData<UiState<Boolean>> sendingData = new MutableLiveData<>();
    private final MutableLiveData<UiState<Boolean>> gettingData = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final SettingsRepository settingsRepository;
    private final RadioRepository radioRepository;
    private final UserRepository userRepository;
    private final FilterRepository filterRepository;
    private final FavoriteStationRepository favoriteStationRepository;
    private final DataFromRadio dataFromRadio;
    private final DataFromUserAPI dataFromUserAPI;
    private Settings settModel;
    private int i = 0;
    private int j = 0;
    private boolean flag_stations = false;

    @Inject
    public SettingsViewModel(@NonNull SettingsRepository settingsRepository, RadioRepository radioRepository,
                             DataFromRadio loadDataFromRadio, DataFromUserAPI dataFromUserAPI, UserRepository userRepository,
                             FilterRepository filterRepository, FavoriteStationRepository favoriteStationRepository){
        this.settingsRepository = settingsRepository;
        this.radioRepository = radioRepository;
        this.dataFromRadio = loadDataFromRadio;
        this.dataFromUserAPI = dataFromUserAPI;
        this.userRepository = userRepository;
        this.filterRepository = filterRepository;
        this.favoriteStationRepository = favoriteStationRepository;
        settModel = settingsRepository.getSettings();
    }
    public LiveData<Boolean> getTimeToLeave(){
        return timeToLeave;
    }
    public LiveData<UiState<Integer>> getStationsCount(){
        return count;
    }
    public LiveData<UiState<Boolean>> getSendingStatus(){
        return sendingData;
    }
    public LiveData<UiState<Boolean>> getGettingStatus(){
        return gettingData;
    }
    private void setSettings(){
        settingsRepository.setSettings(new SettingsDTO().fromModel(settModel));
    }
    public User getUserData(){
        try {
            return userRepository.getUserData();
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error loading user data: " + e.getMessage());
            return null;
        }
    }
    public void toggleChange(@NonNull String key, boolean flag){
        switch (key) {
            case AGC_ENABLED:
                settModel.setAgcEnabled(flag ? 1 : 0);
                setSettings();
                break;

            case CROSSFADE_ENABLED:
                settModel.setCrossfadeEnabled(flag ? 1 : 0);
                setSettings();
                break;


            case TIMER_SECONDS_ENABLED:
                settModel.setTimerSecondsEnabled(flag ? 1 : 0);
                setSettings();
                break;

            case NOTIFICATION_ENABLED:
                settModel.setNotificationEnabled(flag ? 1 : 0);
                setSettings();
                break;
        }
    }
    public void sliderChange(@NonNull String key, int value){
        switch (key) {
            case AUDIO_BALANCE:
                settModel.setAudioBalance(value);
                setSettings();
                break;

            case GAIN_RECORD:
                settModel.setGainRecord(value);
                setSettings();
                break;

            case GAIN_BROADCAST:
                settModel.setGainBroadcast(value);
                setSettings();
                break;

            case CROSSFADE_TIME:
                settModel.setCrossfadeTime(value);
                setSettings();
                break;
        }
    }
    public void clickChange(@NonNull String key){
        switch (key) {
            case GET_USER_DATA:
                if(!flag_stations){
                    loadDataFromUserAPI();
                }
                break;

            case PUT_USER_DATA:
                if(!flag_stations){
                    putDataToUserAPI();
                }
                break;

            case UPDATE_STATIONS_DATA:
                if(!flag_stations){
                    loadFromAPI();
                }
                break;

            case EXIT_FROM_ACCOUNT:
                userRepository.exit();
                timeToLeave.postValue(true);
                break;

            case DELETE_ACCOUNT:
                dataFromUserAPI.deleteUser(userRepository.getUserInSystem());
                userRepository.removeUser();
                timeToLeave.postValue(true);
                break;
        }
    }
    public void switchChange(@NonNull String key, int pos){
        switch (key){
            case NETWORK_TYPE:
                settModel.setNetworkType(pos);
                setSettings();
                break;

            case TIMER_DOTS_TYPE:
                settModel.setTimerDotsType(pos);
                setSettings();
                break;

            case NAVIGATION_TYPE:
                settModel.setNavigationType(pos);
                setSettings();
                break;
        }
    }
    private void loadFromAPI() {
        flag_stations = true;
        count.postValue(UiState.loading(0));
        radioRepository.clearTable();
        executor.execute(() -> dataFromRadio.getStations(new StationsCallback() {
            @Override
            public void onSuccess(List<RadioStationDTO> stations) {
                long i = 0;
                for (RadioStationDTO dto : stations) {
                    try {
                        radioRepository.addRadioStation(dto);
                        i++;
                        count.postValue(UiState.loading((int)(i * 100) / stations.size()));
                    } catch (Exception e) {
                        Log.e("DB", "Ошибка при добавлении: " + dto.getName(), e);
                    }
                }
                count.postValue(UiState.success(100));
                flag_stations = false;
            }
            @Override
            public void onFailure(Throwable t) {
                Log.e("API", "Ошибка загрузки данных", t);
                count.postValue(UiState.error(t.getMessage()));
                flag_stations = false;
            }
            @Override
            public void onLoading(){
                //TODO загрузка станций
            }
        }));
    }
    private void loadDataFromUserAPI(){
        i = 0;
        gettingData.postValue(UiState.loading());
        executor.execute(() -> dataFromUserAPI.getFilters(userRepository.getUserInSystem(), new FiltersCallback() {

            @Override
            public void onSuccess(FilterDTO dto) {
                filterRepository.setFilters(dto);
                i++;
                if(i == 3){
                    gettingData.postValue(UiState.success(true));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("SettingsViewModel", Objects.requireNonNull(t.getMessage()));
                gettingData.postValue(UiState.error(t.getMessage()));
            }
        }));
        executor.execute(() -> dataFromUserAPI.getFavorites(userRepository.getUserInSystem(), new FavoritesCallback() {

            @Override
            public void onSuccess(List<FavoriteStationDTO> favoriteStations) {
                for (FavoriteStationDTO station : favoriteStations) {
                    favoriteStationRepository.addToFavorite(station.getId(), station.getStationUUID());
                }
                i++;
                if(i == 3){
                    gettingData.postValue(UiState.success(true));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("SettingsViewModel", Objects.requireNonNull(t.getMessage()));
                gettingData.postValue(UiState.error(t.getMessage()));
            }
        }));
        executor.execute(() -> dataFromUserAPI.getSettings(userRepository.getUserInSystem(), new SettingsCallback() {

            @Override
            public void onSuccess(SettingsDTO settings) {
                settModel = settings.toModel();
                setSettings();
                i++;
                if(i == 3){
                    gettingData.postValue(UiState.success(true));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("SettingsViewModel", Objects.requireNonNull(t.getMessage()));
                gettingData.postValue(UiState.error(t.getMessage()));
            }
        }));
    }
    private void putDataToUserAPI(){
        j = 0;
        sendingData.postValue(UiState.loading());
        executor.execute(() -> dataFromUserAPI.putSettings(settingsRepository.getSettings(), new PutCallback() {
            @Override
            public void onSuccess(String t) {
                if (t.equals("saved")){
                    j++;
                    if(j == 3){
                        sendingData.postValue(UiState.success(true));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("SettingsViewModel: Settings", Objects.requireNonNull(t.getMessage()));
                sendingData.postValue(UiState.error(t.getMessage()));
            }
        }));
        executor.execute(() -> dataFromUserAPI.putFilters(filterRepository.getFilters(), new PutCallback() {
            @Override
            public void onSuccess(String t) {
                if (t.equals("saved")){
                    j++;
                    if(j == 3){
                        sendingData.postValue(UiState.success(true));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("SettingsViewModel: Filters", Objects.requireNonNull(t.getMessage()));
                sendingData.postValue(UiState.error(t.getMessage()));
            }
        }));
        executor.execute(() -> dataFromUserAPI.putFavorites(favoriteStationRepository.getAllFavorites(), new PutCallback() {
            @Override
            public void onSuccess(String t) {
                if (t.equals("saved")){
                    j++;
                    if(j == 3){
                        sendingData.postValue(UiState.success(true));
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("SettingsViewModel: Favorites", Objects.requireNonNull(t.getMessage()));
                sendingData.postValue(UiState.error(t.getMessage()));
            }
        }));
    }
    public Settings getSettingsModel(){
        return settModel;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
