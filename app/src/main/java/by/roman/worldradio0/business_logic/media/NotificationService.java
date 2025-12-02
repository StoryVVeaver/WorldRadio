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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaSession;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
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
    private MediaSession mediaSession;
    private RadioStation radioStation;
    private boolean isPlaying = false;
    private String currentTrack = "";
    private RemoteViews remoteViews;
    private boolean isStopped = false;

    @SuppressLint("RestrictedApi")
    @Inject
    public NotificationService(NotificationManager notificationManager, @ApplicationContext Context context) {
        this.notificationManager = notificationManager;
        this.context = context;
        createNotificationChannel();
    }

    public Notification startNotification(String track, boolean isPlaying, RadioStation station, MediaSession session) {
        this.radioStation = station;
        this.isPlaying = isPlaying;
        this.mediaSession = session;
        this.currentTrack = track != null ? track : "";
        this.isStopped = false;

        if (radioStation == null) {
            return buildSimpleFallback();
        }

        loadBitmapAndShow(radioStation.getFavicon());

        return buildNotification(null);
    }

    public void updatePlaybackState(boolean nowPlaying) {
        if (radioStation == null || isStopped) return;
        this.isPlaying = nowPlaying;
        rebuild();
    }

    public void updateTrack(String newTrack) {
        if (radioStation == null || isStopped) return;
        this.currentTrack = newTrack != null ? newTrack : "";
        rebuild();
    }

    public void stopNotification() {
        isStopped = true;
        notificationManager.cancel(NOTIFICATION_ID);
        radioStation = null;
    }

    private void rebuild() {
        if (radioStation == null || isStopped) return;
        loadBitmapAndShow(radioStation.getFavicon());
    }

    private void loadBitmapAndShow(String url) {

        Glide.with(context)
                .asBitmap()
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.no_icon)
                .into(new CustomTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> transition) {
                        if (isStopped || radioStation == null) return;
                        notificationManager.notify(NOTIFICATION_ID, buildNotification(bitmap));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (isStopped || radioStation == null) return;
                        fallbackNotify(placeholder);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (isStopped || radioStation == null) return;
                        fallbackNotify(errorDrawable);
                    }
                });
    }

    private void fallbackNotify(@Nullable Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            Drawable d = AppCompatResources.getDrawable(context, R.drawable.no_icon);
            if (d != null) {
                bitmap = Bitmap.createBitmap(
                        d.getIntrinsicWidth(),
                        d.getIntrinsicHeight(),
                        Bitmap.Config.ARGB_8888
                );
                android.graphics.Canvas c = new android.graphics.Canvas(bitmap);
                d.setBounds(0, 0, c.getWidth(), c.getHeight());
                d.draw(c);
            }
        }

        notificationManager.notify(NOTIFICATION_ID, buildNotification(bitmap));
    }

    private Notification buildNotification(@Nullable Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return buildStandard(bitmap);
        } else {
            return buildCustom(bitmap);
        }
    }

    private Notification buildSimpleFallback() {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.globe_selector)
                .setContentTitle("Radio")
                .setContentText(currentTrack)
                .setOngoing(true)
                .build();
    }

    private Notification buildCustom(@Nullable Bitmap bitmap) {

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_custom);

        String name = radioStation != null && radioStation.getName() != null
                ? radioStation.getName()
                : "Unknown Station";

        remoteViews.setTextViewText(R.id.station_name_notification, name);
        remoteViews.setTextViewText(R.id.track_notification, currentTrack);

        int iconRes = isPlaying ? R.drawable.pause : R.drawable.play;
        remoteViews.setImageViewResource(R.id.play_pause_notification, iconRes);

        remoteViews.setOnClickPendingIntent(
                R.id.play_pause_notification,
                createActionIntent(isPlaying ? ACTION_PAUSE : ACTION_PLAY)
        );

        remoteViews.setOnClickPendingIntent(
                R.id.stop_notification,
                createActionIntent(PlayerService.ACTION_STOP)
        );

        if (bitmap != null) {
            remoteViews.setImageViewBitmap(R.id.image_view_station_notification, bitmap);

            int defaultColor = ContextCompat.getColor(context, R.color.bottom_player);
            int dominant = Palette.from(bitmap).generate().getDominantColor(defaultColor);

            try {
                remoteViews.setInt(R.id.notification_root, "setBackgroundColor", dominant);
            } catch (Exception ignored) {}
        } else {
            remoteViews.setImageViewResource(R.id.image_view_station_notification, R.drawable.no_icon);
        }

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.globe_selector)
                .setCustomContentView(remoteViews)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(createContentIntent())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }

    private Notification buildStandard(@Nullable Bitmap bitmap) {

        String title = radioStation != null && radioStation.getName() != null
                ? radioStation.getName()
                : "Radio";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.globe_selector)
                .setContentTitle(title)
                .setContentText(currentTrack)
                .setLargeIcon(bitmap)
                .setOngoing(true)
                .setContentIntent(createContentIntent())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        builder.addAction(
                new NotificationCompat.Action(
                        isPlaying ? R.drawable.pause : R.drawable.play,
                        isPlaying ? "Pause" : "Play",
                        createActionIntent(isPlaying ? ACTION_PAUSE : ACTION_PLAY)
                )
        );

        builder.addAction(
                new NotificationCompat.Action(
                        R.drawable.delete,
                        "Stop",
                        createActionIntent(PlayerService.ACTION_STOP)
                )
        );

        builder.setStyle(new MediaStyle().setShowActionsInCompactView(0, 1));

        return builder.build();
    }

    @SuppressLint("ObsoleteSdkInt")
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    CHANNEL_ID,
                    "Radio Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            ch.setDescription("Radio controls");
            ch.setSound(null, null);
            notificationManager.createNotificationChannel(ch);
        }
    }

    private PendingIntent createContentIntent() {
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(
                context,
                0,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private PendingIntent createActionIntent(String action) {
        Intent i = new Intent(context, PlayerService.class);
        i.setAction(action);

        return PendingIntent.getService(
                context,
                action.hashCode(),
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
