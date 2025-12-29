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

@UnstableApi
public class RadioManager {
    private static final String TAG = "RadioManager";

    private final ExoPlayer player;
    private final Context context;
    private final MutableLiveData<String> currentTrack = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isPlaying = new MutableLiveData<>();
    private final MutableLiveData<String> playbackError = new MutableLiveData<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Inject
    public RadioManager(Context context) {
        this.context = context;

        DataSource.Factory dataSourceFactory = createDataSourceFactory();
        MediaSource.Factory mediaSourceFactory = new DefaultMediaSourceFactory(dataSourceFactory);

        this.player = new ExoPlayer.Builder(context)
                .setMediaSourceFactory(mediaSourceFactory)
                .build();

        player.setPlayWhenReady(false);

        setupPlayerListeners();
    }

    private DataSource.Factory createDataSourceFactory() {
        HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent("App/1.0 (Linux; Android) ExoPlayer")
                .setConnectTimeoutMs(15000)
                .setReadTimeoutMs(15000)
                .setAllowCrossProtocolRedirects(true)
                .setKeepPostFor302Redirects(true);

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
                        Log.d(TAG, "Stream Title: " + streamTitle);
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
                Log.d(TAG, "Playback state changed: " + playbackState);
                switch (playbackState) {
                    case Player.STATE_READY:
                        isPlaying.postValue(player.getPlayWhenReady());
                        playbackError.postValue(null);
                        break;
                    case Player.STATE_BUFFERING:
                        isPlaying.postValue(false);
                        break;
                    case Player.STATE_ENDED:
                    case Player.STATE_IDLE:
                        isPlaying.postValue(false);
                        break;
                }
            }

            @Override
            public void onIsPlayingChanged(boolean playing) {
                Log.d(TAG, "Is playing changed: " + playing);
                isPlaying.postValue(playing);
            }

            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Log.e(TAG, "Playback error: " + error.getMessage(), error);
                isPlaying.postValue(false);

                String errorMessage = getErrorMessage(error);
                playbackError.postValue(errorMessage);

                player.stop();
            }
        });
        // ...
    }

    private String getErrorMessage(PlaybackException error) {
        String baseMessage;
        switch (error.errorCode) {
            case PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED:
                baseMessage = "Ошибка сети: проверьте подключение";
                break;
            case PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS:
                baseMessage = "Ошибка сервера (HTTP-статус): " + error.getMessage();
                break;
            case PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND:
                baseMessage = "Поток не найден (404)";
                break;
            case PlaybackException.ERROR_CODE_DECODING_FAILED:
                baseMessage = "Формат аудио не поддерживается устройством";
                break;
            case PlaybackException.ERROR_CODE_IO_UNSPECIFIED:
                if (error.getMessage() != null && error.getMessage().toLowerCase().contains("ssl")) {
                    baseMessage = "Ошибка SSL/TLS: проблема с безопасным соединением";
                } else {
                    baseMessage = "Ошибка ввода/вывода: " + error.getMessage();
                }
                break;
            default:
                baseMessage = "Ошибка воспроизведения: " + error.getMessage();
                break;
        }
        return baseMessage;
    }

    public void play(String streamUrl) {
        Log.d(TAG, "Attempting to play: " + streamUrl);

        if (streamUrl == null || streamUrl.isEmpty()) {
            playbackError.postValue("Неверный URL потока");
            return;
        }

        stop();

        if (isPlaylistUrl(streamUrl)) {
            Log.d(TAG, "Detected playlist, parsing...");
            parseAndPlayPlaylist(streamUrl);
        } else {
            playDirectStream(streamUrl);
        }
    }

    private boolean isPlaylistUrl(String url) {
        return url.toLowerCase().endsWith(".m3u") ||
                url.toLowerCase().endsWith(".m3u8") ||
                url.toLowerCase().endsWith(".pls");
    }

    private void parseAndPlayPlaylist(String playlistUrl) {
        if (playlistUrl.toLowerCase().endsWith(".m3u8") || playlistUrl.toLowerCase().endsWith(".mpd")) {
            Log.d(TAG, "HLS/DASH detected, playing directly.");
            mainHandler.post(() -> playDirectStream(playlistUrl));
            return;
        }

        executorService.execute(() -> {
            try {
                List<String> streamUrls = parsePlaylist(playlistUrl);

                if (streamUrls.isEmpty()) {
                    mainHandler.post(() -> {
                        playbackError.postValue("В плейлисте не найдено действующих потоков. Попытка воспроизвести оригинальный URL.");
                        playDirectStream(playlistUrl);
                    });
                    return;
                }

                Log.d(TAG, "Found " + streamUrls.size() + " streams in playlist, using first one");

                String firstStream = streamUrls.get(0);
                mainHandler.post(() -> playDirectStream(firstStream));

            } catch (Exception e) {
                Log.e(TAG, "Error parsing playlist: " + e.getMessage(), e);
                mainHandler.post(() -> {
                    playbackError.postValue("Ошибка парсинга плейлиста. Попытка воспроизвести оригинальный URL.");
                    playDirectStream(playlistUrl);
                });
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
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "WorldRadio/1.0");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    if (playlistUrl.toLowerCase().endsWith(".pls")) {
                        if (line.toLowerCase().startsWith("file")) {
                            int equalsIndex = line.indexOf('=');
                            if (equalsIndex > 0) {
                                String urlPart = line.substring(equalsIndex + 1).trim();
                                if (isValidUrl(urlPart)) {
                                    streamUrls.add(urlPart);
                                }
                            }
                        }
                    } else {
                        if (isValidUrl(line)) {
                            streamUrls.add(line);
                        }
                    }
                }
            }

        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing reader", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return streamUrls;
    }

    private boolean isValidUrl(String url) {
        return url != null &&
                (url.startsWith("http://") || url.startsWith("https://")) &&
                url.length() > 10;
    }


    private void playDirectStream(String streamUrl) {
        try {
            currentTrack.postValue(null);
            playbackError.postValue(null);

            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(Uri.parse(streamUrl))
                    .build();

            player.setMediaItem(mediaItem);
            player.prepare();
            player.setPlayWhenReady(true);

            Log.d(TAG, "Playback started successfully: " + streamUrl);

        } catch (Exception e) {
            Log.e(TAG, "Error starting playback: " + e.getMessage(), e);
            playbackError.postValue("Не удалось начать воспроизведение: " + e.getMessage());
            isPlaying.postValue(false);
        }
    }

    public void stop() {
        Log.d(TAG, "Stopping playback");
        try {
            if (player.isPlaying() || player.getPlaybackState() != Player.STATE_IDLE) {
                player.stop();
                player.clearMediaItems();
            }
            player.setPlayWhenReady(false);
            currentTrack.postValue(null);
            playbackError.postValue(null);
        } catch (Exception e) {
            Log.e(TAG, "Error stopping playback: " + e.getMessage(), e);
        }
    }

    public void pause() {
        Log.d(TAG, "Pausing playback");
        player.setPlayWhenReady(false);
    }

    public void resume() {
        Log.d(TAG, "Resuming playback");
        if (player.getPlaybackState() == Player.STATE_READY) {
            player.setPlayWhenReady(true);
        }
    }

    public void setVolume(float volume) {
        player.setVolume(volume);
    }

    public LiveData<String> getCurrentTrack() {
        return currentTrack;
    }

    public LiveData<Boolean> getLiveIsPlaying() {
        return isPlaying;
    }

    public LiveData<String> getPlaybackError() {
        return playbackError;
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public boolean getIsPlaying() {
        return player != null && player.getPlayWhenReady() && player.getPlaybackState() == Player.STATE_READY;
    }

    public void release() {
        Log.d(TAG, "Releasing player resources");
        try {
            if (player != null) {
                player.stop();
                player.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error releasing player: " + e.getMessage(), e);
        }
        executorService.shutdownNow();
    }
}