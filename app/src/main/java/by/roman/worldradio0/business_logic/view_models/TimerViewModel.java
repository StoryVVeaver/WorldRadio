package by.roman.worldradio0.business_logic.view_models;


import static androidx.core.content.ContextCompat.startForegroundService;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.media.TimerService;
import by.roman.worldradio0.business_logic.player.PlayerService;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class TimerViewModel extends ViewModel {
    private MutableLiveData<Long> duration = new MutableLiveData<>();
    private MutableLiveData<Long> time_left = new MutableLiveData<>();
    private MutableLiveData<Boolean> flag = new MutableLiveData<>();
    private MutableLiveData<Boolean> flag2 = new MutableLiveData<>();
    @SuppressLint("StaticFieldLeak")
    private final Context context;

    @Inject
    public TimerViewModel(@ApplicationContext Context context){
        this.context = context;
        BroadcastReceiver timerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, @NonNull Intent intent) {
                if (TimerService.ACTION_DATA_RESULT.equals(intent.getAction())) {
                    time_left.postValue(intent.getLongExtra(TimerService.EXTRA_TIME_LEFT_MS, 0));
                    duration.postValue(intent.getLongExtra(TimerService.EXTRA_TIME_DURATION_MS, 0));
                    flag.postValue(intent.getBooleanExtra(TimerService.EXTRA_TIME_START, false));
                    flag2.postValue(intent.getBooleanExtra(TimerService.EXTRA_TIME_PAUSE, false));
                }
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(timerReceiver,
                new IntentFilter(TimerService.ACTION_DATA_RESULT));
    }
    public void startTimer(long totalTime){
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_START_TIMER);
        intent.putExtra(TimerService.EXTRA_DURATION_MS,totalTime);
        startForegroundService(context, intent);
    }
    public void resumeTimer(){
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_RESUME_TIMER);
        startForegroundService(context, intent);
    }
    public void pauseTimer(){
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_PAUSE_TIMER);
        startForegroundService(context, intent);
    }
    public void stopTimer(){
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_STOP_TIMER);
        startForegroundService(context, intent);
    }
    public void loadData(){
        Intent intent = new Intent(context, TimerService.class);
        intent.setAction(TimerService.ACTION_GET_TIME);
        startForegroundService(context, intent);
    }
    public MutableLiveData<Long> getDuration() {
        return duration;
    }
    public MutableLiveData<Long> getTime_left() {
        return time_left;
    }
    public MutableLiveData<Boolean> getFlag() {
        return flag;
    }
    public MutableLiveData<Boolean> getFlag2() {
        return flag2;
    }
}
