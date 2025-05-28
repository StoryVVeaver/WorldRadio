package by.roman.worldradio0.business_logic.player;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.lifecycle.Observer;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaSession;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.SettingsRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.media.NotificationService;
import dagger.hilt.android.AndroidEntryPoint;

@UnstableApi
@AndroidEntryPoint
public class PlayerService extends Service {
    public static final String ACTION_START = "by.roman.worldradio0.ACTION_START";
    public static final String ACTION_PLAY = "by.roman.worldradio0.ACTION_PLAY";
    public static final String ACTION_STOP = "by.roman.worldradio0.ACTION_STOP";
    public static final String ACTION_PAUSE = "by.roman.worldradio0.ACTION_PAUSE";
    public static final String EXTRA_STREAM_URL = "stream_url";

    private static final int NOTIFICATION_ID = 1;
    private boolean isManuallyStopped = false;

    private Settings settings;
    private String currentTrack;
    private String currentStreamUrl;
    private boolean isPlaying = true;
    private MediaSession mediaSession;

    @Inject
    protected RadioRepository radioRepository;
    @Inject
    protected UserRepository userRepository;
    @Inject
    protected SettingsRepository settingsRepository;
    @Inject
    protected RadioManager radioManager;
    @Inject
    protected NotificationService notificationService;


    private final Observer<String> trackObserver = new Observer<>() {
        @Override
        public void onChanged(String newTrack) {
            if (newTrack != null && notificationService != null && !isManuallyStopped && settings.getNotificationEnabled() == 1) {
                notificationService.updateTrack(newTrack);
            }
        }
    };

    private final Observer<Boolean> stateObserver = new Observer<>() {
        @Override
        public void onChanged(Boolean flag) {
            if (flag != null && notificationService != null && !isManuallyStopped && settings.getNotificationEnabled() == 1) {
                isPlaying = flag;
                notificationService.updatePlaybackState(flag);
            }
        }
    };

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate() {
        super.onCreate();

        radioManager.getCurrentTrack().observeForever(trackObserver);
        radioManager.getLiveIsPlaying().observeForever(stateObserver);

        mediaSession = new MediaSession.Builder(this, radioManager.getPlayer())
                .setId("RadioMediaSession")
                .build();

        settings = settingsRepository.getSettings();

        Log.d("RadioService", "create");
    }


    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        Log.d("RadioService", action);
        switch (action) {
            case ACTION_PLAY:
                Log.d("RadioService", "play");
                radioManager.play(currentStreamUrl);
                break;
            case ACTION_START:
                Log.d("RadioService","start");
                currentStreamUrl = intent.getStringExtra(PlayerService.EXTRA_STREAM_URL);
                Log.d("RadioService", currentStreamUrl);
                isManuallyStopped = false;
                if (currentStreamUrl != null) {
                    radioManager.play(currentStreamUrl);
                    currentTrack = null;
                    Log.d("RadioService", "Station: " + radioRepository.getPlayingStation().getName());
                    stopForeground(true);
                    if(settings.getNotificationEnabled() == 1){
                        startForeground(NOTIFICATION_ID, notificationService.startNotification(currentTrack, isPlaying, radioRepository.getPlayingStation()));
                        notificationService.updatePlaybackState(true);
                    }
                    radioRepository.setStatePlayer(true);
                }
                break;
            case ACTION_PAUSE:
                Log.d("RadioService", "pause");
                radioManager.stop();
                break;
            case ACTION_STOP:
                Log.d("RadioService", "stop");
                if(settings.getNotificationEnabled() == 1){
                    notificationService.stopNotification();
                }
                radioManager.stop();
                isManuallyStopped = true;
                userRepository.setPlayingUUID(null);
                stopForeground(true);
                radioRepository.setStatePlayer(false);
                break;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        radioManager.stop();
        radioManager.release();
        radioManager.getCurrentTrack().removeObserver(trackObserver);
        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }
        Log.d("RadioService","destroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
