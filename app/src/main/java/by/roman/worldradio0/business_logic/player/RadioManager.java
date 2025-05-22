package by.roman.worldradio0.business_logic.player;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Metadata;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.analytics.AnalyticsListener;
import androidx.media3.extractor.metadata.icy.IcyInfo;

import dagger.hilt.android.AndroidEntryPoint;

public class RadioManager {
    private final ExoPlayer player;
    private final MutableLiveData<String> currentTrack = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();

    public RadioManager(Context context) {
        this.player = new ExoPlayer.Builder(context).build();

        player.addAnalyticsListener(new AnalyticsListener() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onMetadata(@NonNull EventTime eventTime, @NonNull Metadata metadata) {
                for (int i = 0; i < metadata.length(); i++) {
                    Metadata.Entry entry = metadata.get(i);
                    if (entry instanceof IcyInfo) {
                        IcyInfo icy = (IcyInfo) entry;
                        String streamTitle = icy.title;
                        Log.d("IcyMeta", "Stream Title: " + streamTitle);
                        currentTrack.postValue(streamTitle);
                    }
                }
            }
        });
        player.addListener(
                new Player.Listener() {
                    @Override
                    public void onIsPlayingChanged(boolean status) {
                        if (status) {
                            isPlaying.postValue(true);
                        } else {
                            isPlaying.postValue(false);
                        }
                    }
                });
    }

    public void play(String streamUrl) {
        Log.d("RadioManager","play: " + streamUrl);
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(Uri.parse(streamUrl))
                .setMimeType(MimeTypes.AUDIO_MPEG)
                .build();

        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        isPlaying.postValue(true);
    }

    public LiveData<String> getCurrentTrack() {
        return currentTrack;
    }

    public void stop() {
        player.stop();
        isPlaying.postValue(false);
    }
    public ExoPlayer getPlayer(){
        return player;
    }

    public void release() {
        player.release();
    }
    public boolean getIsPlaying(){
        return player != null && player.isPlaying();
    }
    public LiveData<Boolean> getLiveIsPlaying(){
        return isPlaying;
    }
}

