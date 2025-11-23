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
import android.graphics.drawable.BitmapDrawable; // Новый импорт для заглушки
import android.graphics.drawable.Drawable; // Новый импорт
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.session.MediaSession;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget; // Использование CustomTarget вместо SimpleTarget
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
    private MediaSession mediaSession;

    @SuppressLint("RestrictedApi")
    @OptIn(markerClass = UnstableApi.class)
    @Inject
    public NotificationService(NotificationManager notificationManager, @ApplicationContext Context context) {
        this.notificationManager = notificationManager;
        this.context = context;
        createNotificationChannel();
    }

    @OptIn(markerClass = UnstableApi.class)
    public Notification startNotification(String contentText, boolean isPlaying, RadioStation radioStationModel, MediaSession session) {
        this.radioStation = radioStationModel;
        this.isPlaying = isPlaying;
        this.mediaSession = session;
        currentTrack = contentText;

        if (radioStation == null) {
            return new NotificationCompat.Builder(context, CHANNEL_ID).build();
        }

        Glide.with(context)
                .asBitmap()
                .load(radioStation.getFavicon())
                .error(R.drawable.no_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        updateNotificationWithBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (placeholder instanceof BitmapDrawable) {
                            updateNotificationWithBitmap(((BitmapDrawable) placeholder).getBitmap());
                        } else {
                            updateNotificationWithFallback();
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (errorDrawable instanceof BitmapDrawable) {
                            updateNotificationWithBitmap(((BitmapDrawable) errorDrawable).getBitmap());
                        } else {
                            updateNotificationWithFallback();
                        }
                    }
                });

        return buildNotification(null);
    }

    private void updateNotificationWithFallback() {
        Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.no_icon);
        if (drawable != null) {
            Bitmap fallbackBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            android.graphics.Canvas canvas = new android.graphics.Canvas(fallbackBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            updateNotificationWithBitmap(fallbackBitmap);
        } else {
            updateNotificationWithBitmap(null);
        }
    }


    private void updateNotificationWithBitmap(Bitmap bitmap) {
        if (radioStation == null) return;
        Notification notification = buildNotification(bitmap);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void updatePlaybackState(boolean isNowPlaying) {
        if (radioStation == null) return;
        this.isPlaying = isNowPlaying;
        rebuildAndNotify();
    }

    public void updateTrack(String newTrack) {
        if (radioStation == null) return;
        currentTrack = newTrack;
        rebuildAndNotify();
    }

    private void rebuildAndNotify() {
        Glide.with(context)
                .asBitmap()
                .load(radioStation.getFavicon())
                .error(R.drawable.no_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        notificationManager.notify(NOTIFICATION_ID, buildNotification(resource));
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        if (placeholder instanceof BitmapDrawable) {
                            updateNotificationWithBitmap(((BitmapDrawable) placeholder).getBitmap());
                        } else {
                            updateNotificationWithFallback();
                        }
                    }
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (errorDrawable instanceof BitmapDrawable) {
                            updateNotificationWithBitmap(((BitmapDrawable) errorDrawable).getBitmap());
                        } else {
                            updateNotificationWithFallback();
                        }
                    }
                });
    }

    @NonNull
    private Notification buildNotification(@Nullable Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return buildStandardMediaNotification(bitmap);
        } else {
            return buildCustomColorNotification(bitmap);
            //return buildStandardMediaNotification(bitmap);
        }
    }

    private Notification buildCustomColorNotification(@Nullable Bitmap bitmap) {
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_custom);

        remoteViews.setTextViewText(R.id.station_name_notification, radioStation.getName());
        remoteViews.setTextViewText(R.id.track_notification, currentTrack != null ? currentTrack : "");

        int buttonIcon = isPlaying ? R.drawable.pause : R.drawable.play;
        remoteViews.setImageViewResource(R.id.play_pause_notification, buttonIcon);

        String action = isPlaying ? ACTION_PAUSE : ACTION_PLAY;
        remoteViews.setOnClickPendingIntent(R.id.play_pause_notification, createActionIntent(action));
        remoteViews.setOnClickPendingIntent(R.id.stop_notification, createActionIntent(PlayerService.ACTION_STOP));

        if (bitmap != null) {
            remoteViews.setImageViewBitmap(R.id.image_view_station_notification, bitmap);

            Palette palette = Palette.from(bitmap).generate();
            int defaultColor = ContextCompat.getColor(context, R.color.bottom_player);
            int dominantColor = palette.getDominantColor(defaultColor);

            try {
                remoteViews.setInt(R.id.notification_root, "setBackgroundColor", dominantColor);
            } catch (Exception e) {
                Log.e("NotificationService", "Error setting background color", e);
            }
        } else {
            remoteViews.setImageViewResource(R.id.image_view_station_notification, R.drawable.no_icon);
        }

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

    private Notification buildStandardMediaNotification(@Nullable Bitmap bitmap) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.globe_selector)
                .setContentTitle(radioStation.getName())
                .setContentText(currentTrack != null ? currentTrack : "")
                .setLargeIcon(bitmap)
                .setContentIntent(createContentIntent())
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        int icon = isPlaying ? R.drawable.pause : R.drawable.play;
        String label = isPlaying ? "Pause" : "Play";
        String actionStr = isPlaying ? ACTION_PAUSE : ACTION_PLAY;
        builder.addAction(new NotificationCompat.Action(icon, label, createActionIntent(actionStr)));

        builder.addAction(new NotificationCompat.Action(R.drawable.unsaved, "Stop", createActionIntent(PlayerService.ACTION_STOP)));

        MediaStyle mediaStyle = new MediaStyle()
                .setShowActionsInCompactView(0, 1);

        builder.setStyle(mediaStyle);

        return builder.build();
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
            channel.setSound(null, null);
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
        radioStation = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}