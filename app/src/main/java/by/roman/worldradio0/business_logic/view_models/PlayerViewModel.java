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
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.player.PlayerService;
import by.roman.worldradio0.business_logic.player.RadioManager;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class PlayerViewModel extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final RadioRepository radioRepository;
    private final FavoriteRepository favoriteRepository;
    private final RadioManager radioManager;
    private final UserRepository userRepository;
    private final MutableLiveData<String> currentTrack = new MutableLiveData<>();
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
        context.startService(intent);
    }

    @OptIn(markerClass = UnstableApi.class)
    public void play(){
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(PlayerService.ACTION_PLAY);
        context.startService(intent);
    }
    @OptIn(markerClass = UnstableApi.class)
    public void pause(){
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(PlayerService.ACTION_PAUSE);
        context.startService(intent);
    }
    public void addToFavorite(){
        favoriteRepository.addToFavorite(userRepository.getPlayingUUID());
    }
    public void removeFromFavorite(){
        favoriteRepository.removeFromFavorite(userRepository.getPlayingUUID());
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
    public boolean getIsPlaying(){
        return radioManager.getIsPlaying();
    }
}
//возможно нужно как-то сигналить ui о успехе операции