package by.roman.worldradio0.business_logic.view_models;

import static androidx.core.content.ContextCompat.startForegroundService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.util.UnstableApi;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.dto.HistoryDTO;
import by.roman.worldradio0.business_logic.data.models.History;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteStationRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteTrackRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteStationRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteTrackRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.HistoryRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.SettingsRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.network.NetworkUtil;
import by.roman.worldradio0.business_logic.player.PlayerService;
import by.roman.worldradio0.business_logic.player.RadioManager;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class PlayerViewModel extends ViewModel implements FavoriteStationRepositoryImpl.OnFavoriteStationsChangedListener, FavoriteTrackRepositoryImpl.OnFavoriteTracksChangedListener {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final RadioRepository radioRepository;
    private final HistoryRepository historyRepository;
    private final FavoriteStationRepository favoriteStationRepository;
    private final FavoriteTrackRepository favoriteTrackRepository;
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;
    private final MutableLiveData<String> currentTrack = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavoriteTrack = new MutableLiveData<>();
    private final MutableLiveData<Boolean> snapNearestEvent = new MutableLiveData<>();
    private Settings settings;
    @Inject
    public PlayerViewModel(@NonNull RadioManager radioManager,HistoryRepository historyRepository, FavoriteTrackRepository favoriteTrackRepository, FavoriteStationRepository favoriteStationRepository, @ApplicationContext Context context, RadioRepository radioRepository, UserRepository userRepository, SettingsRepository settingsRepository) {
        this.context = context;
        this.radioRepository = radioRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.favoriteTrackRepository = favoriteTrackRepository;
        this.favoriteStationRepository = favoriteStationRepository;
        this.settingsRepository = settingsRepository;
        radioManager.getCurrentTrack().observeForever(track -> {
            if (track != null) {
                currentTrack.setValue(track);
                Log.d("PlayerViewModel","Now playing: " + track);
                isFavoriteTrack.postValue(isFavoriteTrack());
            }
        });
        radioManager.getLiveIsPlaying().observeForever(status -> {
            if (status != null) {
                isPlaying.setValue(status);
                Log.d("PlayerViewModel","Player status: " + status);
            }
        });
        favoriteStationRepository.addListener(this);
        favoriteTrackRepository.addListener(this);
        isFavorite.postValue(isFavorite());
        settings = settingsRepository.getSettings();
        if(currentTrack.getValue() != null){
            isFavoriteTrack.postValue(isFavoriteTrack());
        }
    }
    public LiveData<String> getCurrentTrack() {
        return currentTrack;
    }
    public boolean isInternetConnected(){
        try {
            settings = settingsRepository.getSettings();
            return NetworkUtil.isNetworkAvailable(context);
        } catch (Exception e) {
            Log.e("PlayerViewModel","Failed check connection status: " + e.getMessage());
            return false;
        }
    }
    public String checkTypeInternet(){
        String state = NetworkUtil.getConnectionType(context);
        switch (settings.getNetworkType()){
            case 0:
                if(state.equals("WIFI")){
                    return "ok";
                } else return "bad";

            case 1:
                if(state.equals("MOBILE")){
                    return "ok";
                } else return "bad";

            default:
                return "ok";
        }
    }
    @OptIn(markerClass = UnstableApi.class)
    public void start(){
        String streamUrl = radioRepository.getStationById(userRepository.getPlayingUUID()).getUrl();
        Log.d("PlayerViewModel","push " + streamUrl);
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(PlayerService.ACTION_START);
        intent.putExtra(PlayerService.EXTRA_STREAM_URL, streamUrl);
        historyRepository.addToHistory(new HistoryDTO().fromModel(new History(userRepository.getUserInSystem(), userRepository.getPlayingUUID())));
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
    public void playNext(){

    }
    public void playPrevious(){

    }
    public void playPreviousFromHistory(){

    }
    public void addToFavorite(){
        try {
            favoriteStationRepository.addToFavorite(-1, userRepository.getPlayingUUID());
        } catch (Exception e) {
            Log.e("PlayerVM", "Failed add to favorite");
        }
    }
    public void addTrackToFavorite(){
        try {
            favoriteTrackRepository.addToFavorite(-1, getCurrentTrack().getValue());
        } catch (Exception e) {
            Log.e("PlayerVM", "Failed add to favorite");
        }
    }
    public void removeFromFavorite(){
        try {
            favoriteStationRepository.removeFromFavorite(userRepository.getPlayingUUID());
        } catch (Exception e) {
            Log.e("PlayerVM", "Failed remove from favorite: " + e.getMessage());
        }
    }
    public void removeTrackFromFavorite(){
        try {
            favoriteTrackRepository.removeFromFavorite(getCurrentTrack().getValue());
        } catch (Exception e) {
            Log.e("PlayerVM", "Failed remove from favorite: " + e.getMessage());
        }
    }
    private boolean isFavoriteTrack(){
        try {
            return favoriteTrackRepository.isStationFavorite(getCurrentTrack().getValue());
        } catch (Exception e) {
            Log.e("PlayerViewModel", "Failed check is favorite");
            return false;
        }
    }
    private boolean isFavorite(){
        try {
            return favoriteStationRepository.isStationFavorite(userRepository.getPlayingUUID());
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
    public void requestSnapNearest() {
        snapNearestEvent.postValue(true);
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
    public LiveData<Boolean> getIsFavoriteTrack(){
        return isFavoriteTrack;
    }
    public LiveData<Boolean> getSnapNearestEvent() {
        return snapNearestEvent;
    }
    @Override
    public void onFavoriteTracksChanged() {
        isFavoriteTrack.postValue(isFavoriteTrack());
        Log.d("PlayerViewModel","Track: " + isFavoriteTrack());
    }
    @Override
    public void onFavoriteStationsChanged() {
        isFavorite.postValue(isFavorite());
        Log.d("PlayerViewModel","Station: " + isFavorite());
    }
    @Override
    public void onCleared(){
        super.onCleared();
        favoriteStationRepository.removeListener(this);
    }
}