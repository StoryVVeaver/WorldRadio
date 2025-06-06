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
import by.roman.worldradio0.business_logic.data.models.FavoriteTrack;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteStationRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteTrackRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteStationRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteTrackRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoriteViewModel extends ViewModel implements FavoriteStationRepositoryImpl.OnFavoriteStationsChangedListener, FavoriteTrackRepositoryImpl.OnFavoriteTracksChangedListener {
    private final RadioRepository radioRepository;
    private final FavoriteStationRepository favoriteStationRepository;
    private final FavoriteTrackRepository favoriteTrackRepository;
    private final MutableLiveData<UiState<List<RadioStation>>> favoriteStations = new MutableLiveData<>();
    private final MutableLiveData<UiState<List<FavoriteTrack>>> favoriteTracks = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private int currentPage = 0;
    private boolean isLastPage = false;
    private final int pageSize = 200;
    public boolean getIsLastPage() {
        return isLastPage;
    }
    @Inject
    public FavoriteViewModel(RadioRepository radioRepository, FavoriteStationRepository favoriteStationRepository, FavoriteTrackRepository favoriteTrackRepository) {
        this.radioRepository = radioRepository;
        this.favoriteTrackRepository = favoriteTrackRepository;
        this.favoriteStationRepository = favoriteStationRepository;
        loadStationsStart();
        loadTracksStart();
        favoriteStationRepository.addListener(this);
        favoriteTrackRepository.addListener(this);
    }
    public LiveData<UiState<List<RadioStation>>> getFavoriteStations(){
        return favoriteStations;
    }
    public LiveData<UiState<List<FavoriteTrack>>> getFavoriteTracks(){
        return favoriteTracks;
    }
    private void loadStationsStart(){
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
    private void loadTracksStart(){
        favoriteTracks.setValue(UiState.loading());
        executor.execute(() -> {
            try {
                Log.d("FavoriteViewModel","loadStart");
                List<FavoriteTrack> list = favoriteTrackRepository.getFavorites(currentPage,pageSize);
                favoriteTracks.postValue(UiState.success(list));
            } catch (Exception e) {
                favoriteTracks.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
    }
    public void removeStationFromFavorite(String UUID){
        favoriteStationRepository.removeFromFavorite(UUID);
    }
    public void removeTrackFromFavorite(String track){
        favoriteTrackRepository.addToFavorite(0, track);
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
        favoriteStationRepository.removeListener(this);
        favoriteTrackRepository.removeListener(this);
    }

    @Override
    public void onFavoriteStationsChanged() {
        Log.d("PlayerViewModel","Trig, stations");
        loadStationsStart();
    }

    @Override
    public void onFavoriteTracksChanged() {
        Log.d("PlayerViewModel","Trig, tracks");
        loadTracksStart();
    }
}
