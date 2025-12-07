package by.roman.worldradio0.business_logic;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.OptIn;
import androidx.core.content.ContextCompat;
import androidx.media3.common.util.UnstableApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.data.models.History;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.HistoryRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.player.PlayerService;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class PlayerStarter {

    @Inject
    RadioRepository radioRepository;

    @Inject
    HistoryRepository historyRepository;

    @Inject
    UserRepository userRepository;

    private final Context context;

    @Inject
    public PlayerStarter(@ApplicationContext Context context) {
        this.context = context;
    }

    @OptIn(markerClass = UnstableApi.class)
    public void start(String uuid) {
        String streamUrl = radioRepository.getStationById(uuid).getUrl();
        userRepository.setPlayingUUID(uuid);

        Intent intent = new Intent(context, PlayerService.class);
        intent.setAction(PlayerService.ACTION_START);
        intent.putExtra(PlayerService.EXTRA_STREAM_URL, streamUrl);

        historyRepository.addToHistory(
                new History(userRepository.getUserInSystem(), uuid)
        );

        ContextCompat.startForegroundService(context, intent);
    }
}
