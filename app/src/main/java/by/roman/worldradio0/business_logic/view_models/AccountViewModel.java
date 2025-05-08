package by.roman.worldradio0.business_logic.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;
import by.roman.worldradio0.business_logic.data.dto.UserDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.models.User;
import by.roman.worldradio0.business_logic.data.models.UserRequest;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FilterRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.SettingsRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.network.userAPI.DataFromUserAPI;
import by.roman.worldradio0.business_logic.network.userAPI.callbacks.RequestCallback;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AccountViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;
    private final FavoriteRepository favoriteRepository;
    private final FilterRepository filterRepository;
    private final DataFromUserAPI dataFromUserAPI;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final MutableLiveData<UiState<Boolean>> result = new MutableLiveData<>();

    @Inject
    public AccountViewModel(UserRepository userRepository,SettingsRepository settingsRepository,FavoriteRepository favoriteRepository,FilterRepository filterRepository, DataFromUserAPI dataFromUserAPI){
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
        this.favoriteRepository = favoriteRepository;
        this.filterRepository = filterRepository;
        this.dataFromUserAPI = dataFromUserAPI;
    }
    public LiveData<UiState<Boolean>> getUser(){
        return result;
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
                    settingsRepository.addSettings(new SettingsDTO().fromModel(new Settings(dto.getId(),0,0,0,0,0)));
                    filterRepository.addFilters(new FilterDTO().fromModel(new Filter(dto.getId(),null,null,null,null,null,0)));
                    result.postValue(UiState.success(true));
                } catch (Exception e) {
                    result.postValue(UiState.error(e.getMessage()));
                    Log.e("AccountViewModel: reg", "Failed get user");
                }
            }
            @Override
            public void onFailure(Throwable t) {
                result.postValue(UiState.error(t.getMessage()));
                Log.e("AccountViewModel: reg", "Ошибка загрузки данных", t);
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
                    settingsRepository.addSettings(new SettingsDTO().fromModel(new Settings(dto.getId(),0,0,0,0,0)));
                    filterRepository.addFilters(new FilterDTO().fromModel(new Filter(dto.getId(),null,null,null,null,null,0)));
                    result.postValue(UiState.success(true));
                } catch (Exception e) {
                    result.postValue(UiState.error(e.getMessage()));
                    Log.e("AccountViewModel: enter", "Failed get user");
                }
            }
            @Override
            public void onFailure(Throwable t) {
                result.postValue(UiState.error(t.getMessage()));
                Log.e("AccountViewModel: enter", "Ошибка загрузки данных", t);
            }
        }));
    }
}
