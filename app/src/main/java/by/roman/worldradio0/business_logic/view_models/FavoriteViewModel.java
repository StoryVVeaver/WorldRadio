package by.roman.worldradio0.business_logic.view_models;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private int currentTrackPage = 0;
    private int currentStationPage = 0;
    private final int pageSize = 30;
    private boolean allLoadedTrack = false;
    private boolean allLoadedStation = false;
    private boolean isLoadingTrack = false;
    private boolean isLoadingStation = false;
    public boolean getIsLastStationPage() {
        return allLoadedStation;
    }
    @Inject
    public FavoriteViewModel(RadioRepository radioRepository, FavoriteStationRepository favoriteStationRepository, FavoriteTrackRepository favoriteTrackRepository) {
        this.radioRepository = radioRepository;
        this.favoriteTrackRepository = favoriteTrackRepository;
        this.favoriteStationRepository = favoriteStationRepository;
        favoriteStationRepository.addListener(this);
        favoriteTrackRepository.addListener(this);
    }
    public LiveData<UiState<List<RadioStation>>> getFavoriteStations(){
        return favoriteStations;
    }
    public LiveData<UiState<List<FavoriteTrack>>> getFavoriteTracks(){
        return favoriteTracks;
    }
    public void loadStationNextPage() {
        if (isLoadingStation || allLoadedStation) return;

        isLoadingStation = true;
        UiState<List<RadioStation>> currentState = null;
        if(currentStationPage != 0){
            currentState = favoriteStations.getValue();
        }
        List<RadioStation> currentList = currentState != null && currentState.data != null
                ? new ArrayList<>(currentState.data)
                : new ArrayList<>();

        favoriteStations.postValue(UiState.loading());

        executor.execute(() -> {
            try {
                List<RadioStation> newList = radioRepository.getFavoriteStations(currentStationPage, pageSize);

                if (newList.size() < pageSize) allLoadedStation = true;

                currentList.addAll(newList);
                favoriteStations.postValue(UiState.success(currentList));
                currentStationPage++;
            } catch (Exception e) {
                favoriteStations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            } finally {
                isLoadingStation = false;
            }
        });
    }
    private void loadStationsStart(){
        favoriteStations.setValue(UiState.loading());
        executor.execute(() -> {
            try {
                Log.d("FavoriteViewModel","loadStart");
                List<RadioStation> list = radioRepository.getFavoriteStations(currentStationPage,1000);
                favoriteStations.postValue(UiState.success(list));
            } catch (Exception e) {
                favoriteStations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
    }
    public void loadTrackNextPage() {
        if (isLoadingTrack || allLoadedTrack) return;

        isLoadingTrack = true;
        UiState<List<FavoriteTrack>> currentState = null;
        if(currentTrackPage != 0){
            currentState = favoriteTracks.getValue();
        }
        List<FavoriteTrack> currentList = currentState != null && currentState.data != null
                ? new ArrayList<>(currentState.data)
                : new ArrayList<>();

        favoriteTracks.setValue(UiState.loading());

        executor.execute(() -> {
            try {
                List<FavoriteTrack> newList = favoriteTrackRepository.getFavorites(currentTrackPage, pageSize);

                if (newList.size() < pageSize) allLoadedTrack = true;

                currentList.addAll(newList);
                favoriteTracks.postValue(UiState.success(currentList));
                currentTrackPage++;
            } catch (Exception e) {
                favoriteStations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            } finally {
                isLoadingTrack = false;
            }
        });
    }
    public void removeStationFromFavorite(String UUID){
        favoriteStationRepository.removeFromFavorite(UUID);
    }
    public void removeTrackFromFavorite(String track){
        favoriteTrackRepository.removeFromFavorite(track);
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
        currentStationPage = 0;
        allLoadedStation = false;
        loadStationNextPage();
    }

    @Override
    public void onFavoriteTracksChanged() {
        Log.d("PlayerViewModel","Trig, tracks");
        currentTrackPage = 0;
        allLoadedTrack = false;
        loadTrackNextPage();
    }
}
