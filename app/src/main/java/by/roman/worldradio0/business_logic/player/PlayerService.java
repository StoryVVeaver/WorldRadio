package by.roman.worldradio0.business_logic.player;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaSession;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
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

    private String currentTrack;
    private String currentStreamUrl;
    private boolean isPlayingBefore = false;
    private MediaSession mediaSession;

    @Inject
    protected RadioRepository radioRepository;
    @Inject
    protected UserRepository userRepository;
    @Inject
    protected RadioManager radioManager;
    @Inject
    protected NotificationService notificationService;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onCreate() {
        super.onCreate();

        mediaSession = new MediaSession.Builder(this, radioManager.getPlayer())
                .setId("RadioMediaSession")
                .build();

        Log.d("RadioService", "create");
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        Log.d("RadioService", "Received action: " + action);

        switch (action) {
            case ACTION_PLAY:
                Log.d("RadioService", "Resume playback");
                runOnMainThread(radioManager::resume);
                break;

            case ACTION_START:
                String newStreamUrl = intent.getStringExtra(EXTRA_STREAM_URL);
                Log.d("RadioService", "Stream URL: " + newStreamUrl);

                if (newStreamUrl != null) {
                    currentStreamUrl = newStreamUrl;
                    isManuallyStopped = false;

                    runOnMainThread(() -> {
                        try {
                            RadioStation station = radioRepository.getStationByUrl(currentStreamUrl);
                            if (station == null) {
                                Log.e("RadioService", "No station found for current stream URL");
                            }

                            radioManager.play(currentStreamUrl);
                            currentTrack = null;

                            stopForeground(true);
                            startForeground(NOTIFICATION_ID,
                                    notificationService.startNotification(
                                            currentTrack,
                                            true,
                                            radioRepository.getPlayingStation(),
                                            mediaSession));

                            if (!isPlayingBefore) {
                                radioRepository.setStatePlayer(true);
                            }
                            isPlayingBefore = true;

                        } catch (Exception e) {
                            Log.e("RadioService", "Error during playback: " + e.getMessage(), e);
                            handleStop();
                        }
                    });
                }
                break;

            case ACTION_PAUSE:
                Log.d("RadioService", "Pause playback");
                runOnMainThread(radioManager::pause);
                break;

            case ACTION_STOP:
                Log.d("RadioService", "Stop playback completely");
                handleStop();
                break;
        }

        return START_NOT_STICKY;
    }

    private void handleStop() {
        runOnMainThread(() -> {
            if (notificationService != null) {
                notificationService.stopNotification();
            }

            radioManager.stop();
            isManuallyStopped = true;
            isPlayingBefore = false;
            userRepository.setPlayingUUID(null);
            stopForeground(true);
            radioRepository.setStatePlayer(false);
        });
    }

    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            mainHandler.post(runnable);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (notificationService != null) {
            notificationService.stopNotification();
        }

        radioManager.stop();
        radioManager.release();

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
