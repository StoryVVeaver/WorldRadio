package by.roman.worldradio0.business_logic;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.OptIn;
import androidx.core.content.ContextCompat;
import androidx.media3.common.util.UnstableApi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Singleton;

import by.roman.worldradio0.business_logic.data.models.History;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.HistoryRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import by.roman.worldradio0.business_logic.network.radio.ClickModel;
import by.roman.worldradio0.business_logic.network.radio.DataFromRadio;
import by.roman.worldradio0.business_logic.network.radio.callbacks.RadioCallback;
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

    @Inject
    DataFromRadio dataFromRadio;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Context context;

    @Inject
    public PlayerStarter(@ApplicationContext Context context) {
        this.context = context;
    }

    @OptIn(markerClass = UnstableApi.class)
    public void start(String uuid) {
        radioRepository.setCurrentUUID(uuid);
        Intent startIntent = new Intent(context, PlayerService.class);
        startIntent.setAction(PlayerService.ACTION_START);
        startIntent.putExtra(PlayerService.EXTRA_STREAM_UUID, uuid);
        ContextCompat.startForegroundService(context, startIntent);

        executor.execute(() -> dataFromRadio.click(uuid, new RadioCallback<ClickModel>() {
            @Override
            public void onSuccess(ClickModel t) {
                Intent updateIntent = new Intent(context, PlayerService.class);
                updateIntent.setAction(PlayerService.ACTION_START);
                updateIntent.putExtra(PlayerService.EXTRA_STREAM_UUID, uuid);
                updateIntent.putExtra(PlayerService.EXTRA_URL, t.getUrl());
                updateIntent.putExtra(PlayerService.EXTRA_NAME, t.getName());

                context.startService(updateIntent);
            }

            @Override
            public void onFailure(Throwable t) {
                Intent errorIntent = new Intent(context, PlayerService.class);
                errorIntent.setAction(PlayerService.ACTION_STOP);
                context.startService(errorIntent);
            }

            @Override
            public void onLoading() {
            }
        }));

        historyRepository.addToHistory(
                new History(userRepository.getUserInSystem(), uuid)
        );
    }
}