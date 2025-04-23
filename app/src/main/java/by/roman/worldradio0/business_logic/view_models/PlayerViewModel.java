package by.roman.worldradio0.business_logic.view_models;

import static androidx.core.content.ContextCompat.startForegroundService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.OptIn;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.media3.common.util.UnstableApi;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.data.repositories.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.UserRepository;
import by.roman.worldradio0.business_logic.player.PlayerService;
import by.roman.worldradio0.business_logic.player.RadioManager;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class PlayerViewModel extends ViewModel {

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final RadioRepository radioRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<String> currentTrack = new MutableLiveData<>();
    @Inject
    public PlayerViewModel(RadioManager radioManager, @ApplicationContext Context context, RadioRepository radioRepository, UserRepository userRepository) {
        this.context = context;
        this.radioRepository = radioRepository;
        this.userRepository = userRepository;
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
    public void play(){
        String streamUrl = radioRepository.getStationById(userRepository.getPlayingUUID()).getUrl();
        Log.d("PlayerViewModel","push " + streamUrl);
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(PlayerService.ACTION_START);
        intent.putExtra(PlayerService.EXTRA_STREAM_URL, streamUrl);
        startForegroundService(context, intent);
    }

    @OptIn(markerClass = UnstableApi.class)
    public void stop(){
        Intent stopIntent = new Intent(context, PlayerService.class);
        stopIntent.setAction(PlayerService.ACTION_STOP);
        context.startService(stopIntent);
    }
}
