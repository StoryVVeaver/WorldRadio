package by.roman.worldradio0.business_logic.view_models;

import static androidx.core.content.ContextCompat.startForegroundService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.util.UnstableApi;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.player.PlayerService;
import by.roman.worldradio0.business_logic.player.RadioManager;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class PlayerViewModel extends ViewModel implements FavoriteRepositoryImpl.OnFavoritesChangedListener{

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final RadioRepository radioRepository;
    private final FavoriteRepository favoriteRepository;
    private final RadioManager radioManager;
    private final UserRepository userRepository;
    private final MutableLiveData<String> currentTrack = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>();
    @Inject
    public PlayerViewModel(@NonNull RadioManager radioManager,FavoriteRepository favoriteRepository, @ApplicationContext Context context, RadioRepository radioRepository, UserRepository userRepository) {
        this.context = context;
        this.radioRepository = radioRepository;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.radioManager = radioManager;
        radioManager.getCurrentTrack().observeForever(track -> {
            if (track != null) {
                currentTrack.setValue(track);
                Log.d("PlayerViewModel","Now playing: " + track);
            }
        });
        radioManager.getLiveIsPlaying().observeForever(status -> {
            if (status != null) {
                isPlaying.setValue(status);
                Log.d("PlayerViewModel","Player status: " + status);
            }
        });
        favoriteRepository.addListener(this);
        isFavorite.postValue(isFavorite());
    }
    public LiveData<String> getCurrentTrack() {
        return currentTrack;
    }

    @OptIn(markerClass = UnstableApi.class)
    public void start(){
        String streamUrl = radioRepository.getStationById(userRepository.getPlayingUUID()).getUrl();
        Log.d("PlayerViewModel","push " + streamUrl);
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(PlayerService.ACTION_START);
        intent.putExtra(PlayerService.EXTRA_STREAM_URL, streamUrl);
        startForegroundService(context, intent);
    }
    @OptIn(markerClass = UnstableApi.class)
    public void stop(){
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(PlayerService.ACTION_STOP);
        startForegroundService(context, intent);
    }
    @OptIn(markerClass = UnstableApi.class)
    public void play(){
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(PlayerService.ACTION_PLAY);
        startForegroundService(context, intent);
    }
    @OptIn(markerClass = UnstableApi.class)
    public void pause(){
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(PlayerService.ACTION_PAUSE);
        startForegroundService(context, intent);
    }
    public void addToFavorite(){
        try {
            favoriteRepository.addToFavorite(userRepository.getPlayingUUID());
        } catch (Exception e) {
            Log.e("PlayerVM", "Failed add to favorite");
        }
    }
    public void removeFromFavorite(){
        try {
            favoriteRepository.removeFromFavorite(userRepository.getPlayingUUID());
        } catch (Exception e) {
            Log.e("PlayerVM", "Failed remove from favorite: " + e.getMessage());
        }
    }
    private boolean isFavorite(){
        try {
            return favoriteRepository.isStationFavorite(userRepository.getPlayingUUID());
        } catch (Exception e) {
            Log.e("PlayerViewModel", "Failed check is favorite");
            return false;
        }
    }
    public void setPlaying(String UUID){
        try {
            userRepository.setPlayingUUID(UUID);
        } catch (Exception e){
            Log.e("DB", "Ошибка при установке: " + UUID, e);
        }
    }
    public RadioStation getCurrentStation(){
        return radioRepository.getPlayingStation();
    }
    public LiveData<Boolean> getIsPlaying(){
        return isPlaying;
    }
    public LiveData<Boolean> getIsFavorite(){
        return isFavorite;
    }
    @Override
    public void onFavoritesChanged() {
        isFavorite.postValue(isFavorite());
        Log.d("PlayerViewModel","Trig: " + isFavorite());
    }
    @Override
    public void onCleared(){
        super.onCleared();
        favoriteRepository.removeListener(this);
    }
}