package by.roman.worldradio0.business_logic.view_models;

import static androidx.core.content.ContextCompat.startForegroundService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.util.UnstableApi;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.models.History;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteStationRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteTrackRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.RadioRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteStationRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteTrackRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.HistoryRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.SettingsRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.network.NetworkUtil;
import by.roman.worldradio0.business_logic.network.radio.ClickModel;
import by.roman.worldradio0.business_logic.network.radio.DataFromRadio;
import by.roman.worldradio0.business_logic.network.radio.VoteModel;
import by.roman.worldradio0.business_logic.network.radio.callbacks.RadioCallback;
import by.roman.worldradio0.business_logic.player.PlayerService;
import by.roman.worldradio0.business_logic.player.RadioManager;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class PlayerViewModel extends ViewModel implements FavoriteStationRepositoryImpl.OnFavoriteStationsChangedListener, FavoriteTrackRepositoryImpl.OnFavoriteTracksChangedListener, RadioRepositoryImpl.OnPlayingChangedListener {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final RadioRepository radioRepository;
    private final HistoryRepository historyRepository;
    private final FavoriteStationRepository favoriteStationRepository;
    private final FavoriteTrackRepository favoriteTrackRepository;
    private final UserRepository userRepository;
    private final DataFromRadio dataFromRadio;
    private final SettingsRepository settingsRepository;
    private final MutableLiveData<String> currentTrack = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();
    private final MutableLiveData<RadioStation> isPlayingChanged = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavorite = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isFavoriteTrack = new MutableLiveData<>();
    private final MutableLiveData<Boolean> snapNearest = new MutableLiveData<>();
    private final MutableLiveData<Boolean> snapPrevious = new MutableLiveData<>();
    private final MutableLiveData<Boolean> playNext = new MutableLiveData<>();
    private final MutableLiveData<Boolean> playPrevious = new MutableLiveData<>();
    private final MutableLiveData<UiState<VoteModel>> vote = new MutableLiveData<>();
    private final MutableLiveData<String> selectedCard = new MutableLiveData<>();
    private Settings settings;
    private RadioStation currentStation;

    @OptIn(markerClass = UnstableApi.class)
    @Inject
    public PlayerViewModel(@NonNull RadioManager radioManager,HistoryRepository historyRepository, FavoriteTrackRepository favoriteTrackRepository,
                           FavoriteStationRepository favoriteStationRepository, @ApplicationContext Context context, DataFromRadio dataFromRadio,
                           RadioRepository radioRepository, UserRepository userRepository, SettingsRepository settingsRepository) {
        this.context = context;
        this.radioRepository = radioRepository;
        this.userRepository = userRepository;
        this.dataFromRadio = dataFromRadio;
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
        radioRepository.addListener(this);
        isFavorite.postValue(isFavorite());
        settings = settingsRepository.getSettings();
        if(currentTrack.getValue() != null){
            isFavoriteTrack.postValue(isFavoriteTrack());
        }
    }
    public LiveData<String> getCurrentTrack() {
        return currentTrack;
    }

    public LiveData<UiState<VoteModel>> getVote() {
        return vote;
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
    public void start(RadioStation station) {
        String uuid = station.getStationUuid();
        currentStation = station;
        selectedCard.postValue(uuid);
        radioRepository.setCurrentUUID(uuid);
        historyRepository.addToHistory(new History(userRepository.getUserInSystem(), uuid));
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(PlayerService.ACTION_START);
        intent.putExtra(PlayerService.EXTRA_STREAM_UUID, uuid);
        intent.putExtra(PlayerService.EXTRA_URL, station.getUrl());
        intent.putExtra(PlayerService.EXTRA_NAME, station.getName());
        intent.putExtra(PlayerService.EXTRA_ICON, station.getFavicon());

        ContextCompat.startForegroundService(context, intent);

        executor.execute(() -> dataFromRadio.click(uuid, new RadioCallback<>() {
            @Override
            public void onSuccess(ClickModel t) {
                Intent updateIntent = new Intent(context, PlayerService.class);
                updateIntent.setAction(PlayerService.ACTION_START);
                updateIntent.putExtra(PlayerService.EXTRA_STREAM_UUID, uuid);
                updateIntent.putExtra("EXTRA_URL", t.getUrl());
                updateIntent.putExtra("EXTRA_NAME", t.getName());
                context.startService(updateIntent);
            }

            @Override
            public void onFailure(Throwable t) {

            }

            @Override
            public void onLoading() {

            }
        }));
    }
    @OptIn(markerClass = UnstableApi.class)
    public void stop(){
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(PlayerService.ACTION_STOP);
        context.startService(intent);
        radioRepository.setCurrentUUID("");
        selectedCard.postValue(null);
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
    public void playNext(){
        Log.v("PlayerViewModel","start next");
        playNext.postValue(true);
    }
    public void playPrevious(){
        playPrevious.postValue(true);
    }
    public void addToFavorite(){
        try {
            favoriteStationRepository.addToFavorite(-1, radioRepository.getCurrentUUID());
        } catch (Exception e) {
            Log.e("PlayerVM", "Failed add to favorite");
        }
    }
    public void voteStation(){
        String uuid = radioRepository.getCurrentUUID();
        if(!uuid.isEmpty()){
            vote.postValue(UiState.loading());
            executor.execute(() -> dataFromRadio.vote(uuid, new RadioCallback<>() {
                @Override
                public void onSuccess(VoteModel t) {
                    vote.postValue(UiState.success(t));
                }

                @Override
                public void onFailure(Throwable t) {
                    vote.postValue(UiState.error(t.getMessage()));
                }

                @Override
                public void onLoading() {

                }
            }));
        }
    }
    public void addTrackToFavorite(){
        try {
            if(!Objects.equals(getCurrentTrack().getValue(), null)) {
                if(!getCurrentTrack().getValue().isEmpty()){
                    Log.v("model", getCurrentTrack().getValue() + " ");
                    favoriteTrackRepository.addToFavorite(-1, getCurrentTrack().getValue());
                }
            }
        } catch (Exception e) {
            Log.e("PlayerVM", "Failed add to favorite");
        }
    }
    public void removeFromFavorite(){
        try {
            favoriteStationRepository.removeFromFavorite(radioRepository.getCurrentUUID());
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
    public boolean isFavoriteTrack(){
        try {
            return favoriteTrackRepository.isStationFavorite(getCurrentTrack().getValue());
        } catch (Exception e) {
            Log.e("PlayerViewModel", "Failed check is favorite");
            return false;
        }
    }
    public boolean isFavorite(){
        try {
            return favoriteStationRepository.isStationFavorite(radioRepository.getCurrentUUID());
        } catch (Exception e) {
            Log.e("PlayerViewModel", "Failed check is favorite");
            return false;
        }
    }

    public void requestSnapNearest() {
        snapNearest.postValue(true);
    }
    public void requestSnapPrevious() {
        snapPrevious.postValue(true);
    }
    public RadioStation getCurrentStation(){
        return currentStation;

    }
    public RadioStation getStationById(String uuid){
        return radioRepository.getStationById(uuid);
    }
    public LiveData<String> getSelectedCard(){
        return selectedCard;
    }
    public LiveData<Boolean> getIsPlaying(){
        return isPlaying;
    }
    public LiveData<RadioStation> getIsPlayingChanged(){
        return isPlayingChanged;
    }
    public LiveData<Boolean> getIsFavorite(){
        return isFavorite;
    }
    public LiveData<Boolean> getIsFavoriteTrack(){
        return isFavoriteTrack;
    }
    public LiveData<Boolean> getSnapNearestEvent() {
        return snapNearest;
    }
    public LiveData<Boolean> getSnapPrevious() {
        return snapPrevious;
    }
    public LiveData<Boolean> getPlayNext() {
        return playNext;
    }
    public LiveData<Boolean> getPlayPrevious() {
        return playPrevious;
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
        favoriteTrackRepository.removeListener(this);
        radioRepository.removeListener(this);
    }

    @Override
    public void onPlayingChanged() {
        isPlayingChanged.postValue(getCurrentStation());
        currentTrack.postValue("");
    }
}