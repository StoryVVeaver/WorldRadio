package by.roman.worldradio0.business_logic.media;

import static by.roman.worldradio0.business_logic.player.PlayerService.ACTION_STOP;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media3.common.util.UnstableApi;

import by.roman.worldradio0.business_logic.player.PlayerService;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimerService extends Service {

    public static final String ACTION_START_TIMER = "ACTION_START_TIMER";
    public static final String ACTION_PAUSE_TIMER = "ACTION_PAUSE_TIMER";
    public static final String ACTION_RESUME_TIMER = "ACTION_RESUME_TIMER";
    public static final String ACTION_STOP_TIMER = "ACTION_STOP_TIMER";
    public static final String EXTRA_DURATION_MS = "EXTRA_DURATION_MS";
    public static final String ACTION_GET_TIME = "ACTION_GET_TIME";
    public static final String ACTION_DATA_RESULT = "ACTION_DATA_RESULT";
    public static final String EXTRA_TIME_LEFT_MS = "EXTRA_TIME_LEFT_MS";
    public static final String EXTRA_TIME_DURATION_MS = "EXTRA_TIME_DURATION_MS";
    public static final String EXTRA_TIME_START = "EXTRA_TIME_START";
    public static final String EXTRA_TIME_PAUSE = "EXTRA_TIME_PAUSE";
    public static final String EXTRA_TIME_FINISH = "EXTRA_TIME_FINISH";

    private Handler handler;
    private Runnable stopRunnable;
    private long endTimeMillis = 0;
    private long duration = 0;
    private long remaining = 0;
    private boolean flag = false;
    private boolean flag2 = false;
    private boolean finish = true;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        Log.d("TS","created");
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == null) return START_NOT_STICKY;
        switch (action) {
            case ACTION_START_TIMER:
                long duration = intent.getLongExtra(EXTRA_DURATION_MS, 2 * 60 * 1000); // default 2 min
                startSleepTimer(duration);
                break;
            case ACTION_PAUSE_TIMER:
                pauseSleepTimer();
                break;
            case ACTION_RESUME_TIMER:
                resumeSleepTimer();
                break;
            case ACTION_STOP_TIMER:
                stopSleepTimer();
                break;
            case ACTION_GET_TIME:
                long timeLeft = getTimeLeft();
                Intent resultIntent = new Intent(ACTION_DATA_RESULT);
                resultIntent.putExtra(EXTRA_TIME_LEFT_MS, timeLeft);
                resultIntent.putExtra(EXTRA_TIME_DURATION_MS, this.duration);
                resultIntent.putExtra(EXTRA_TIME_START, this.flag);
                resultIntent.putExtra(EXTRA_TIME_PAUSE, this.flag2);
                LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
                break;
        }
        return START_STICKY;
    }

    @OptIn(markerClass = UnstableApi.class)
    private void startSleepTimer(long durationMs) {
        stopSleepTimer();
        duration = durationMs;
        endTimeMillis = System.currentTimeMillis() + durationMs;

        stopRunnable = () -> {
            Log.d("TimerService", "Timer expired, stopping playback and service.");
            flag = false;
            Intent stopIntent = new Intent(this, PlayerService.class);
            stopIntent.setAction(ACTION_STOP);
            startService(stopIntent);
        };
        handler.postDelayed(stopRunnable, durationMs);
        flag = true;
        Log.d("TimerService", "Sleep timer started for " + durationMs + " ms");
    }
    private void stopSleepTimer() {
        if (stopRunnable != null) {
            handler.removeCallbacks(stopRunnable);
            stopRunnable = null;
            remaining = 0;
            endTimeMillis = 0;
            flag = false;
            Log.d("TimerService", "Sleep timer stopped");
        }
    }
    private void pauseSleepTimer(){
        long now = System.currentTimeMillis();
        remaining =  Math.max(endTimeMillis - now, 0);
        if (stopRunnable != null) {
            handler.removeCallbacks(stopRunnable);
            flag = false;
            flag2 = true;
            Log.d("TimerService", "Sleep timer paused");
        }
    }
    @OptIn(markerClass = UnstableApi.class)
    private void resumeSleepTimer(){
        if(remaining != 0 && stopRunnable != null){
            endTimeMillis = System.currentTimeMillis() + remaining;
            handler.postDelayed(stopRunnable, remaining);
            flag = true;
            flag2 = false;
            Log.d("TimerService", "Sleep timer resumed for " + remaining + " ms");
        }
    }
    private long getTimeLeft() {
        long now = System.currentTimeMillis();
        if(flag) {
            return Math.max(endTimeMillis - now, 0);
        } else {
            return remaining;
        }
    }
    @Override
    public void onDestroy() {
        stopSleepTimer();
        super.onDestroy();
        Log.d("TimerService", "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
