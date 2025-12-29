package by.roman.worldradio0.business_logic.media;

import static by.roman.worldradio0.business_logic.player.PlayerService.ACTION_STOP;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media3.common.util.UnstableApi;

import by.roman.worldradio0.R;
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

    private static final String CHANNEL_ID = "timer_service_channel";
    private static final int NOTIFICATION_ID = 1;

    private Handler handler;
    private Runnable stopRunnable;
    private Runnable tickRunnable;
    private static final long TICK_INTERVAL = 60_000;
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
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action == null) return START_NOT_STICKY;

        switch (action) {
            case ACTION_START_TIMER:
                startForeground(NOTIFICATION_ID, createNotification("Таймер запущен", ""));
                long duration = intent.getLongExtra(EXTRA_DURATION_MS, 2 * 60 * 1000);
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
                stopForeground(true);
                stopSelf();
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Channel for timer service");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(String title, String content) {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.timer_home)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .build();
    }

    private void updateNotification(String title, String content) {
        Notification notification = createNotification(title, content);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(NOTIFICATION_ID, notification);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void startSleepTimer(long durationMs) {
        stopSleepTimer();
        duration = durationMs;
        endTimeMillis = System.currentTimeMillis() + durationMs;

        stopRunnable = () -> {
            Log.d("TimerService", "Timer expired, stopping playback and service.");
            flag = false;

            updateNotification("Таймер завершен", "Воспроизведение остановлено");

            Intent stopIntent = new Intent(this, PlayerService.class);
            stopIntent.setAction(ACTION_STOP);
            startService(stopIntent);

            stopSelf();
        };

        handler.postDelayed(stopRunnable, durationMs);
        flag = true;
        flag2 = false;

        updateNotification("Таймер запущен", formatRemainingText(durationMs) + " осталось");
        Log.d("TimerService", "Sleep timer started for " + durationMs + " ms");
        startTicking();
    }

    private void stopSleepTimer() {
        if (stopRunnable != null) {
            handler.removeCallbacks(stopRunnable);
            stopTicking();
            stopRunnable = null;
            remaining = 0;
            endTimeMillis = 0;
            flag = false;
            flag2 = false;

            updateNotification("Таймер остановлен", "Таймер не активен");
            Log.d("TimerService", "Sleep timer stopped");
        }
    }

    private void pauseSleepTimer() {
        long now = System.currentTimeMillis();
        remaining = Math.max(endTimeMillis - now, 0);
        if (stopRunnable != null) {
            handler.removeCallbacks(stopRunnable);
            stopTicking();
            flag = false;
            flag2 = true;

            updateNotification("Таймер на паузе", formatRemainingText(remaining) + " осталось");
            Log.d("TimerService", "Sleep timer paused");
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void resumeSleepTimer() {
        if (remaining != 0 && stopRunnable != null) {
            endTimeMillis = System.currentTimeMillis() + remaining;
            handler.postDelayed(stopRunnable, remaining);
            flag = true;
            flag2 = false;

            updateNotification("Таймер возобновлен", formatRemainingText(remaining) + " осталось");
            Log.d("TimerService", "Sleep timer resumed for " + remaining + " ms");
            startTicking();
        }
    }

    private void startTicking() {
        stopTicking();

        tickRunnable = new Runnable() {
            @Override
            public void run() {
                if (flag && !flag2) {
                    long left = getTimeLeft();
                    updateNotification("Таймер", formatRemainingText(left));
                    handler.postDelayed(this, 60_000);
                }
            }
        };

        handler.post(tickRunnable);
    }


    private void stopTicking() {
        if (tickRunnable != null) {
            handler.removeCallbacks(tickRunnable);
            tickRunnable = null;
        }
    }


    private long getTimeLeft() {
        long now = System.currentTimeMillis();
        if (flag) {
            return Math.max(endTimeMillis - now, 0);
        } else {
            return remaining;
        }
    }

    private String formatRemainingText(long millis) {
        long totalMinutes = (long) Math.ceil(millis / 60000.0);

        if (totalMinutes <= 0) {
            return "Время вышло";
        }

        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        StringBuilder sb = new StringBuilder("Осталось ");

        if (hours > 0) {
            sb.append(hours).append(" ").append(plural(hours, "час", "часа", "часов"));
            if (minutes > 0) {
                sb.append(" ");
            }
        }

        if (minutes > 0) {
            sb.append(minutes).append(" ").append(plural(minutes, "минута", "минуты", "минут"));
        }

        return sb.toString();
    }

    private String plural(long value, String one, String few, String many) {
        if (value % 10 == 1 && value % 100 != 11) {
            return one;
        }
        if (value % 10 >= 2 && value % 10 <= 4
                && (value % 100 < 10 || value % 100 >= 20)) {
            return few;
        }
        return many;
    }


    @Override
    public void onDestroy() {
        stopSleepTimer();
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.cancel(NOTIFICATION_ID);
        stopForeground(true);
        super.onDestroy();
        Log.d("TimerService", "Service destroyed");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}