package by.roman.worldradio0.business_logic.media;

import static by.roman.worldradio0.business_logic.player.PlayerService.ACTION_PAUSE;
import static by.roman.worldradio0.business_logic.player.PlayerService.ACTION_PLAY;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import javax.inject.Inject;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.player.PlayerService;
import by.roman.worldradio0.ui.activities.MainActivity;
import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.qualifiers.ApplicationContext;

@UnstableApi
@AndroidEntryPoint
public class NotificationService extends Service {
    private final Context context;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "radio_channel";
    private final NotificationManager notificationManager;
    private String currentTrack;
    private RadioStation radioStation;
    private boolean isPlaying;
    private RemoteViews remoteViews;

    @SuppressLint("RestrictedApi")
    @OptIn(markerClass = UnstableApi.class)
    @Inject
    public NotificationService(NotificationManager notificationManager, @ApplicationContext Context context) {
        this.notificationManager = notificationManager;
        this.context = context;
        createNotificationChannel();
    }

    @OptIn(markerClass = UnstableApi.class)
    public Notification startNotification(String contentText, boolean isPlaying, RadioStation radioStationModel) {
        this.radioStation = radioStationModel;
        this.isPlaying = isPlaying;
        currentTrack = contentText;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_custom);

        updateRemoteViews();

        Glide.with(context)
                .asBitmap()
                .error(AppCompatResources.getDrawable(context,R.drawable.no_icon))
                .load(radioStation.getFavicon())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        remoteViews.setImageViewBitmap(R.id.image_view_station_notification, resource);
                        notificationManager.notify(NOTIFICATION_ID, buildNotification());
                    }
                });

        return buildNotification();
    }

    public void updatePlaybackState(boolean isNowPlaying) {
        this.isPlaying = isNowPlaying;
        updateRemoteViews();
        Notification notification = buildNotification();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void updateTrack(String newTrack) {
        currentTrack = newTrack;
        remoteViews.setTextViewText(R.id.track_notification, currentTrack);
        Notification notification = buildNotification();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void updateRemoteViews() {
        if (remoteViews == null) return;

        remoteViews.setTextViewText(R.id.station_name_notification, radioStation.getName());
        remoteViews.setTextViewText(R.id.track_notification, currentTrack);

        int buttonIcon = isPlaying ? R.drawable.pause : R.drawable.play;
        remoteViews.setImageViewResource(R.id.play_pause_notification, buttonIcon);

        String action = isPlaying ? ACTION_PAUSE : ACTION_PLAY;
        Log.d("notification",action);
        remoteViews.setOnClickPendingIntent(R.id.play_pause_notification, createActionIntent(action));
        remoteViews.setOnClickPendingIntent(R.id.stop_notification, createActionIntent(PlayerService.ACTION_STOP));
    }

    @NonNull
    private Notification buildNotification() {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.globe_selector)
                .setCustomContentView(remoteViews)
                .setContentIntent(createContentIntent())
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(null)
                .build();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Radio Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Channel for radio playback controls");
            notificationManager.createNotificationChannel(channel);
        }
    }
    private PendingIntent createContentIntent() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private PendingIntent createActionIntent(String action) {
        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(action);
        return PendingIntent.getService(
                context,
                action.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    public void stopNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
