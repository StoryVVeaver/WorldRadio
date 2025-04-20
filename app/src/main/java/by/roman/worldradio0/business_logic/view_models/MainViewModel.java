package by.roman.worldradio0.business_logic.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.RadioRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private final RadioRepository radioRepository;
    private final MutableLiveData<UiState<List<RadioStation>>> favoriteStations = new MutableLiveData<>();

    public LiveData<UiState<List<RadioStation>>> getFavoriteStations() {
        return favoriteStations;
    }
    @Inject
    public MainViewModel(RadioRepository radioRepository){
        this.radioRepository = radioRepository;
    }
    public void loadFavorite(){
        favoriteStations.setValue(UiState.loading());

        new Thread(() -> {
            try {
                List<RadioStation> list = radioRepository.getFavoriteStations();
                favoriteStations.postValue(UiState.success(list));
            } catch (Exception e) {
                favoriteStations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        }).start();
    }
    public List<RadioStation> getAll(){
        return radioRepository.getAllStations();
    }
    public void loadFromAPI(){

    }
}
