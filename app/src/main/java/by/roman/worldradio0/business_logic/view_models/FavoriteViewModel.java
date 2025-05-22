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
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoriteViewModel extends ViewModel implements FavoriteRepositoryImpl.OnFavoritesChangedListener {
    private final RadioRepository radioRepository;
    private final FavoriteRepository favoriteRepository;
    private final MutableLiveData<UiState<List<RadioStation>>> favoriteStations = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private int currentPage = 0;
    private boolean isLastPage = false;
    private final int pageSize = 200;
    public boolean getIsLastPage() {
        return isLastPage;
    }
    @Inject
    public FavoriteViewModel(RadioRepository radioRepository, FavoriteRepository favoriteRepository) {
        this.radioRepository = radioRepository;
        this.favoriteRepository = favoriteRepository;
        loadStart();
        favoriteRepository.addListener(this);
    }
    public LiveData<UiState<List<RadioStation>>> getFavoriteStations(){
        return favoriteStations;
    }
    private void loadStart(){
        favoriteStations.setValue(UiState.loading());
        executor.execute(() -> {
            try {
                Log.d("FavoriteViewModel","loadStart");
                List<RadioStation> list = radioRepository.getFavoriteStations(currentPage,pageSize);
                favoriteStations.postValue(UiState.success(list));
            } catch (Exception e) {
                favoriteStations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
    }
    public void removeFromFavorite(String UUID){
        favoriteRepository.removeFromFavorite(UUID);
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
        favoriteRepository.removeListener(this);
    }

    @Override
    public void onFavoritesChanged() {
        Log.d("PlayerViewModel","Trig");
        loadStart();
    }
}
