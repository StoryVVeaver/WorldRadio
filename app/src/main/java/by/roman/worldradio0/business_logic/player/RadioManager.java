package by.roman.worldradio0.business_logic.player;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.analytics.AnalyticsListener;
import androidx.media3.extractor.metadata.icy.IcyInfo;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.MediaSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

@UnstableApi
@Singleton
public class RadioManager {
    private static final String TAG = "RadioManager";

    private ExoPlayer player;
    private final Context context;
    private final MutableLiveData<String> currentTrack = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();
    private final MutableLiveData<String> playbackError = new MutableLiveData<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    public RadioManager(Context context) {
        this.context = context.getApplicationContext();
    }

    private void ensurePlayerInitialized() {
        if (player == null) {
            synchronized (this) {
                if (player == null) {
                    Log.d(TAG, "Initializing ExoPlayer instance...");

                    DataSource.Factory dataSourceFactory = createDataSourceFactory();
                    MediaSource.Factory mediaSourceFactory = new DefaultMediaSourceFactory(dataSourceFactory);

                    AudioAttributes audioAttributes = new AudioAttributes.Builder()
                            .setUsage(C.USAGE_MEDIA)
                            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                            .build();

                    this.player = new ExoPlayer.Builder(context)
                            .setMediaSourceFactory(mediaSourceFactory)
                            .setAudioAttributes(audioAttributes, true)
                            .setHandleAudioBecomingNoisy(true)
                            .build();

                    player.setPlayWhenReady(false);
                    setupPlayerListeners();
                }
            }
        }
    }

    private DataSource.Factory createDataSourceFactory() {
        HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent("WorldRadio/1.0 (Android) ExoPlayer")
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(15000)
                .setAllowCrossProtocolRedirects(true);

        return new DefaultDataSource.Factory(context, httpDataSourceFactory);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setupPlayerListeners() {
        player.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onMetadata(@NonNull EventTime eventTime, @NonNull androidx.media3.common.Metadata metadata) {
                for (int i = 0; i < metadata.length(); i++) {
                    androidx.media3.common.Metadata.Entry entry = metadata.get(i);
                    if (entry instanceof IcyInfo) {
                        IcyInfo icy = (IcyInfo) entry;
                        String streamTitle = icy.title;
                        if (streamTitle != null && !streamTitle.trim().isEmpty()) {
                            currentTrack.postValue(streamTitle);
                        }
                    }
                }
            }
        });

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                switch (playbackState) {
                    case Player.STATE_READY:
                        isPlaying.postValue(player.getPlayWhenReady());
                        playbackError.postValue(null);
                        break;
                    case Player.STATE_BUFFERING:
                        break;
                    case Player.STATE_ENDED:
                    case Player.STATE_IDLE:
                        isPlaying.postValue(false);
                        break;
                }
            }

            @Override
            public void onIsPlayingChanged(boolean playing) {
                isPlaying.postValue(playing);
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Log.e(TAG, "Playback error: " + error.getErrorCodeName(), error);
                isPlaying.postValue(false);
                playbackError.postValue(getErrorMessage(error));
                player.stop();
            }
        });
    }

    private String getErrorMessage(PlaybackException error) {
        switch (error.errorCode) {
            case PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED:
                return "Ошибка сети: проверьте подключение";
            case PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS:
                return "Ошибка сервера (404/500)";
            case PlaybackException.ERROR_CODE_DECODING_FAILED:
                return "Формат аудио не поддерживается";
            default:
                return "Ошибка воспроизведения";
        }
    }

    public void play(String streamUrl) {
        if (streamUrl == null || streamUrl.isEmpty()) {
            playbackError.postValue("Неверный URL");
            return;
        }

        ensurePlayerInitialized();

        stop();

        if (isPlaylistUrl(streamUrl)) {
            parseAndPlayPlaylist(streamUrl);
        } else {
            playDirectStream(streamUrl);
        }
    }

    private boolean isPlaylistUrl(String url) {
        String lower = url.toLowerCase();
        return lower.endsWith(".m3u") || lower.endsWith(".m3u8") || lower.endsWith(".pls");
    }

    private void parseAndPlayPlaylist(String playlistUrl) {
        if (playlistUrl.toLowerCase().contains("m3u8")) {
            mainHandler.post(() -> playDirectStream(playlistUrl));
            return;
        }

        executorService.execute(() -> {
            try {
                List<String> streamUrls = parsePlaylist(playlistUrl);
                String urlToPlay = streamUrls.isEmpty() ? playlistUrl : streamUrls.get(0);
                mainHandler.post(() -> playDirectStream(urlToPlay));
            } catch (Exception e) {
                mainHandler.post(() -> playDirectStream(playlistUrl));
            }
        });
    }

    private List<String> parsePlaylist(String playlistUrl) throws Exception {
        List<String> streamUrls = new ArrayList<>();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(playlistUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    if (playlistUrl.endsWith(".pls") && line.toLowerCase().startsWith("file")) {
                        int idx = line.indexOf('=');
                        if (idx > 0) streamUrls.add(line.substring(idx + 1).trim());
                    } else if (line.startsWith("http")) {
                        streamUrls.add(line);
                    }
                }
            }
        } finally {
            if (reader != null) reader.close();
            if (connection != null) connection.disconnect();
        }
        return streamUrls;
    }

    private void playDirectStream(String streamUrl) {
        ensurePlayerInitialized();
        mainHandler.post(() -> {
            try {
                currentTrack.setValue(null);
                playbackError.setValue(null);

                MediaItem mediaItem = MediaItem.fromUri(streamUrl);
                player.setMediaItem(mediaItem);
                player.prepare();
                player.setPlayWhenReady(true);
            } catch (Exception e) {
                playbackError.postValue("Ошибка старта");
            }
        });
    }

    public void stop() {
        if (player == null) return;
        mainHandler.post(() -> {
            player.stop();
            player.clearMediaItems();
            currentTrack.setValue(null);
        });
    }

    public void pause() {
        if (player != null) player.setPlayWhenReady(false);
    }

    public void resume() {
        if (player != null && player.getPlaybackState() == Player.STATE_READY) {
            player.setPlayWhenReady(true);
        }
    }

    public void setVolume(float volume) {
        if (player != null) player.setVolume(volume);
    }

    public LiveData<String> getCurrentTrack() { return currentTrack; }
    public LiveData<Boolean> getLiveIsPlaying() { return isPlaying; }
    public LiveData<String> getPlaybackError() { return playbackError; }

    public ExoPlayer getPlayer() {
        ensurePlayerInitialized();
        return player;
    }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
        executorService.shutdownNow();
    }
}