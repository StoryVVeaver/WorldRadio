package by.roman.worldradio0.business_logic.view_models;

import static by.roman.worldradio0.business_logic.settings.SettingsKeys.AGC_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.AUDIO_BALANCE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.CROSSFADE_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.CROSSFADE_TIME;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.GAIN_BROADCAST;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.GAIN_RECORD;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.GET_USER_DATA;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.NETWORK_TYPE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.PUT_USER_DATA;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.RADIO_MODULE_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.TIMER_DOTS_TYPE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.TIMER_SECONDS_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.UPDATE_STATIONS_DATA;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.data.dto.FavoriteStationDTO;
import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;
import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsGroup;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckWIthSliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.SliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.SwitchItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.TextButtonItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.TextItem;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FilterRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.SettingsRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.network.radio.DataFromRadio;
import by.roman.worldradio0.business_logic.network.radio.StationsCallback;
import by.roman.worldradio0.business_logic.network.userAPI.DataFromUserAPI;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.FavoritesCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.FiltersCallback;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.SettingsCallback;
import by.roman.worldradio0.business_logic.settings.SettingsKeys;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final SettingsRepository settingsRepository;
    private final RadioRepository radioRepository;
    private final UserRepository userRepository;
    private final FilterRepository filterRepository;
    private final FavoriteRepository favoriteRepository;
    private final DataFromRadio dataFromRadio;
    private final DataFromUserAPI dataFromUserAPI;
    private Settings settModel;

    @Inject
    public SettingsViewModel(SettingsRepository settingsRepository, RadioRepository radioRepository, DataFromRadio loadDataFromRadio,
                             DataFromUserAPI dataFromUserAPI, UserRepository userRepository, FilterRepository filterRepository,
                             FavoriteRepository favoriteRepository){
        this.settingsRepository = settingsRepository;
        this.radioRepository = radioRepository;
        this.dataFromRadio = loadDataFromRadio;
        this.dataFromUserAPI = dataFromUserAPI;
        this.userRepository = userRepository;
        this.filterRepository = filterRepository;
        this.favoriteRepository = favoriteRepository;
    }
    public List<SettingsGroup> getSettingsList(){
        settModel = settingsRepository.getSettings();

        List<SettingsGroup> groups = new ArrayList<>();

        try {
            List<SettingsItem> audioItems = new ArrayList<>();
            audioItems.add(new SliderItem(AUDIO_BALANCE,"Баланс:",-10, 10,settModel.getAudioBalance(),"L","R",true));
            audioItems.add(new TextItem("Усиление"));
            audioItems.add(new SliderItem(GAIN_RECORD,"     Запись:", 0, 100, settModel.getGainRecord(),true));
            audioItems.add(new SliderItem(GAIN_BROADCAST,"     Эфир:", 0, 100, settModel.getGainBroadcast(),true));
            audioItems.add(new CheckItem(AGC_ENABLED,"AGC: ",settModel.getAgcEnabled() == 1));
            audioItems.add(new CheckWIthSliderItem(CROSSFADE_ENABLED,CROSSFADE_TIME,"Crossfade: ",0, 20,settModel.getCrossfadeTime(),settModel.getCrossfadeEnabled() == 1));
            groups.add(new SettingsGroup("Аудио", audioItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list audio settings");
        }

        try {
            List<SettingsItem> networkItems = new ArrayList<>();
            List<String> network_types = new ArrayList<>();
            network_types.add("Только Wi-fi");
            network_types.add("Только мобильная сеть");
            network_types.add("Любое");
            networkItems.add(new SwitchItem(NETWORK_TYPE, "Подключение:", network_types,settModel.getNetworkType()));
            networkItems.add(new CheckItem(RADIO_MODULE_ENABLED,"Использовать радиомодуль:",settModel.getRadioModuleEnabled() == 1));
            groups.add(new SettingsGroup("Сетевые параметры", networkItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list network settings");
        }

        try {
            List<SettingsItem> viewItems = new ArrayList<>();
            viewItems.add(new CheckItem(TIMER_SECONDS_ENABLED,"Использовать секунды:",settModel.getTimerSecondsEnabled() == 1));
            List<String> dots_types = new ArrayList<>();
            dots_types.add("Круг");
            dots_types.add("Ромб");
            viewItems.add(new SwitchItem(TIMER_DOTS_TYPE,"Вид разделителя:",dots_types,settModel.getTimerDotsType()));
            groups.add(new SettingsGroup("Оформление", viewItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list view settings");
        }

        try {
            List<SettingsItem> dataItems = new ArrayList<>();
            dataItems.add(new TextButtonItem(GET_USER_DATA, PUT_USER_DATA,"Синхронизация:", "[Загрузить]", "[Выгрузить]"));
            dataItems.add(new TextButtonItem(UPDATE_STATIONS_DATA,"Станции:", "[Обновить]"));
            dataItems.add(new TextButtonItem("9","История:", "[Очистить]")); //TODO
            groups.add(new SettingsGroup("Данные", dataItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list data settings");
        }

        return groups;
    }
    private void setSettings(){
        settingsRepository.setSettings(new SettingsDTO().fromModel(settModel));
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

            case RADIO_MODULE_ENABLED:
                settModel.setRadioModuleEnabled(flag ? 1 : 0);
                setSettings();
                break;

            case TIMER_SECONDS_ENABLED:
                settModel.setTimerSecondsEnabled(flag ? 1 : 0);
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
                loadDataFromUserAPI();
                break;

            case PUT_USER_DATA:
                break;

            case UPDATE_STATIONS_DATA:
                loadFromAPI();
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
        }
    }
    private void loadFromAPI() {
        radioRepository.clearTable();
        executor.execute(() -> dataFromRadio.getStations(new StationsCallback() {
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
    private void loadDataFromUserAPI(){
        executor.execute(() -> dataFromUserAPI.getFilters(userRepository.getUserInSystem(), new FiltersCallback() {

            @Override
            public void onSuccess(FilterDTO dto) {
                filterRepository.setFilters(dto);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("SettingsViewModel", Objects.requireNonNull(t.getMessage()));
            }
        }));
        executor.execute(() -> dataFromUserAPI.getFavorites(userRepository.getUserInSystem(), new FavoritesCallback() {

            @Override
            public void onSuccess(List<FavoriteStationDTO> favoriteStations) {
                for (FavoriteStationDTO station : favoriteStations) {
                    favoriteRepository.addToFavorite(station.getStationUUID());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("SettingsViewModel", Objects.requireNonNull(t.getMessage()));
            }
        }));
        executor.execute(() -> dataFromUserAPI.getSettings(userRepository.getUserInSystem(), new SettingsCallback() {

            @Override
            public void onSuccess(SettingsDTO settings) {
                settModel = settings.toModel();
                setSettings();
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("SettingsViewModel", Objects.requireNonNull(t.getMessage()));
            }
        }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
