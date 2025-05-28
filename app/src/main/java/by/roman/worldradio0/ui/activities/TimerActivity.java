package by.roman.worldradio0.ui.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.RadioAdapter;
import by.roman.worldradio0.business_logic.adapters.TimerWheelAdapter;
import by.roman.worldradio0.business_logic.media.TimerService;
import by.roman.worldradio0.business_logic.view_models.SettingsViewModel;
import by.roman.worldradio0.business_logic.view_models.TimerViewModel;
import by.roman.worldradio0.ui.elements.view.CircularTimerView;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TimerActivity extends AppCompatActivity {

    private RecyclerView recyclerHour, recyclerMinute, recyclerSecond;
    private TimerWheelAdapter hourAdapter, minuteAdapter, secondAdapter;
    private CircularTimerView circularTimerView;
    private CountDownTimer countDownTimer;
    //private SettingsViewModel settingsViewModel;
    private TimerViewModel viewModel;
    private SettingsViewModel settingsViewModel;

    private ImageView pauseButton;
    private ImageView playButton;
    private ImageView startButton;
    private ImageView stopButton;
    private ImageView backButton;
    private ImageView divider1;
    private ImageView divider2;
    private ConstraintLayout time;

    private long totalTime = 0L;
    private boolean isStart = false;
    private boolean isPaused = false;
    private long timeRemaining = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        long startTime = System.nanoTime();
        Log.v("TimerActivity: performance", "onCreated started");
        findAll();
        initAll();
        observeTime();
        buttons();

        //TODO перенос на фрагмент
        //TODO добавление залпланированного старта
        //TODO улучшенить логику, переключение секунд в процессе работы и пауз некиритично ломает
        Log.v("TimerActivity: performance", "onCreated total execution time: " + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    private void buttons(){
        startButton.setOnClickListener(v -> {
            startTimer();
        });
        stopButton.setOnClickListener(v -> {
            stopTimer();
        });
        playButton.setOnClickListener(v -> {
            resumeTimer();
        });
        pauseButton.setOnClickListener(v -> {
            pauseTimer();
        });
        backButton.setOnClickListener(v -> {
            finish();
        });
    }
    private void startTimer(){
        viewModel.startTimer(totalTime);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(totalTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                circularTimerView.setCurrentTimeMillis(millisUntilFinished);
            }
            @Override
            public void onFinish() {
                circularTimerView.setCurrentTimeMillis(0);
                stopUI();
            }
        };
        countDownTimer.start();
        isStart = true;
        startUI(0);
    }
    private void startUI(int value){
        if(value == 0){
            animateMoveDown(circularTimerView);
        } else {
            animateMoveDownFast(circularTimerView);
        }
        time.setVisibility(GONE);
        startButton.setVisibility(GONE);
        stopButton.setVisibility(VISIBLE);
        pauseButton.setVisibility(VISIBLE);
        stopButton.setAlpha(0.5f);
        stopButton.setEnabled(false);
    }
    private void stopTimer(){
        viewModel.stopTimer();
        countDownTimer.cancel();
        isStart = false;
        stopUI();
    }
    private void stopUI(){
        animateMoveUp(circularTimerView);
        time.setVisibility(VISIBLE);
        startButton.setVisibility(VISIBLE);
        stopButton.setVisibility(GONE);
        playButton.setVisibility(GONE);
        pauseButton.setVisibility(GONE);
        fadeInOnResume();
        circularTimerView.setCurrentTimeMillis(totalTime);
    }
    private void resumeTimer(){
        viewModel.resumeTimer();
        countDownTimer = new CountDownTimer(timeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                circularTimerView.setCurrentTimeMillis(millisUntilFinished);
            }
            @Override
            public void onFinish() {
                circularTimerView.setCurrentTimeMillis(0);
                stopUI();
            }
        };
        countDownTimer.start();
        isStart = true;
        isPaused = false;
        resumeUI();
    }
    private void resumeUI(){
        stopButton.setEnabled(false);
        stopButton.setAlpha(0.5f);
        pauseButton.setVisibility(VISIBLE);
        playButton.setVisibility(GONE);
        fadeInOnResume();
    }
    private void pauseTimer(){
        viewModel.pauseTimer();
        countDownTimer.cancel();
        isStart = false;
        isPaused = true;
        pauseUI();
    }
    private void pauseUI(){
        stopButton.setEnabled(true);
        stopButton.setAlpha(1f);
        playButton.setVisibility(VISIBLE);
        pauseButton.setVisibility(GONE);
        fadeOutOnPause();
    }
    private void observeTime(){
        viewModel.loadData();
        viewModel.getDuration().observe(this, duration -> {
            if(duration == null) return;
            Log.d("TA","Duration: " + duration);
            this.totalTime = duration;
            circularTimerView.setMaxTimeMillis(totalTime);
            setValues();
        });
        viewModel.getTime_left().observe(this, left -> {
            if(left == null) return;
            Log.d("TA","Left: " + left);
            this.timeRemaining = left;
            circularTimerView.setCurrentTimeMillis(timeRemaining);
        });
        viewModel.getFlag().observe(this, flag -> {
            if(flag == null) return;
            Log.d("TA","Flag: " + flag);
            this.isStart = flag;
            if(isStart){
                countDownTimer = new CountDownTimer(timeRemaining, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeRemaining = millisUntilFinished;
                        circularTimerView.setCurrentTimeMillis(millisUntilFinished);
                    }
                    @Override
                    public void onFinish() {
                    }
                };
                countDownTimer.start();
                startUI(1);
            }
        });
        viewModel.getFlag2().observe(this, flag2 -> {
            if(flag2 == null) return;
            Log.d("TA","Flag2: " + flag2);
            this.isPaused = flag2;
            if(isPaused){
                startUI(1);
                pauseUI();
                circularTimerView.setMaxTimeMillis(totalTime);
                circularTimerView.setCurrentTimeMillis(timeRemaining);
            }
        });
    }
    private void initAll(){
        viewModel = new ViewModelProvider(this).get(TimerViewModel.class);
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        hourAdapter = new TimerWheelAdapter(this, 24);
        setupRecyclerView(recyclerHour, hourAdapter);
        hourAdapter.setSelectedPosition(hourAdapter.getItemCount() / 2);

        minuteAdapter = new TimerWheelAdapter(this, 60);
        setupRecyclerView(recyclerMinute, minuteAdapter);
        minuteAdapter.setSelectedPosition(minuteAdapter.getItemCount() / 2);
        if (settingsViewModel.getSettingsModel().getTimerSecondsEnabled() == 1){
            recyclerSecond.setVisibility(VISIBLE);
            divider2.setVisibility(VISIBLE);
            secondAdapter = new TimerWheelAdapter(this, 60);
            setupRecyclerView(recyclerSecond, secondAdapter);
            secondAdapter.setSelectedPosition(secondAdapter.getItemCount() / 2);
        }
        if (settingsViewModel.getSettingsModel().getTimerDotsType() == 0){
            divider1.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.circle));
            divider2.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.circle));
        } else {
            divider1.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.romb));
            divider2.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.romb));
        }
    }
            private void setupRecyclerView(@NonNull RecyclerView recyclerView, TimerWheelAdapter adapter) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                LinearSnapHelper snapHelper = new LinearSnapHelper();
                snapHelper.attachToRecyclerView(recyclerView);
                recyclerView.post(() -> {
                    int middlePosition = (adapter.getItemCount() / 2);
                    Log.d("TimerActivity", "ad: " + middlePosition);
                    scrollToPosition(middlePosition,recyclerView,adapter);

                });
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                        super.onScrollStateChanged(rv, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            View centerView = snapHelper.findSnapView(layoutManager);
                            if (centerView != null) {
                                int position = layoutManager.getPosition(centerView);
                                adapter.setSelectedPosition(position);
                                updateTotalTime();
                            }
                        }
                    }
                });
            }
            private void scrollToPosition(int value, @NonNull RecyclerView recyclerView, TimerWheelAdapter adapter){
                LinearLayoutManager layoutManager1 = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager1 != null) {
                    layoutManager1.scrollToPositionWithOffset((adapter.getItemCount() / 2) + value, -20);
                }
                adapter.setSelectedPosition((adapter.getItemCount() / 2) + value);
            }
            private void updateTotalTime() {
                if(!isStart && !isPaused){
                    int hours = hourAdapter.getSelectedPosition() % 24;
                    int minutes = minuteAdapter.getSelectedPosition() % 60;
                    totalTime = (hours * 3600000L) + (minutes * 60000L);
                    if(settingsViewModel.getSettingsModel().getTimerSecondsEnabled() == 1){
                        int seconds = secondAdapter.getSelectedPosition() % 60;
                        totalTime = totalTime + (seconds * 1000L);
                    }
                    circularTimerView.setMaxTimeMillis(totalTime+1L);
                    circularTimerView.setCurrentTimeMillis(totalTime);
                }
            }
            private void setValues() {
                int hours = (int) (totalTime / 3_600_000L);
                Log.d("TA", "Hours: " + hours);
                scrollToPosition(hours,recyclerHour,hourAdapter);
                long remaining = totalTime % 3_600_000L;
                int minutes = (int) (remaining / 60_000L);
                Log.d("TA", "Minutes: " + minutes);
                scrollToPosition(minutes,recyclerMinute,minuteAdapter);
                remaining = remaining % 60_000L;
                int seconds = (int) (remaining / 1_000L);
                Log.d("TA", "Seconds: " + seconds);
                if(settingsViewModel.getSettingsModel().getTimerSecondsEnabled() == 1){
                    scrollToPosition(seconds,recyclerSecond,secondAdapter);
                }
                circularTimerView.setMaxTimeMillis(totalTime+1L);
                circularTimerView.setCurrentTimeMillis(totalTime);
            }
    private void findAll(){
        circularTimerView = findViewById(R.id.circularTimerView);
        pauseButton = findViewById(R.id.pauseButtonView);
        playButton = findViewById(R.id.playButtonView);
        startButton = findViewById(R.id.startButtonView);
        backButton = findViewById(R.id.backButtonTimerView);
        stopButton = findViewById(R.id.stopButtonView);
        recyclerHour = findViewById(R.id.recyclerHour);
        recyclerMinute = findViewById(R.id.recyclerMinute);
        recyclerSecond = findViewById(R.id.recyclerSecond);
        divider1 = findViewById(R.id.dotDivider1);
        divider2 = findViewById(R.id.dotDivider2);
        time = findViewById(R.id.setTime);
    }
            private void fadeOutOnPause() {
                ValueAnimator fadeOut = ValueAnimator.ofFloat(1f, 0.5f);
                fadeOut.setDuration(500);
                fadeOut.addUpdateListener(animation -> {
                    float alpha = (float) animation.getAnimatedValue();
                    circularTimerView.setAlpha(alpha);
                });
                fadeOut.start();
            }
            private void fadeInOnResume() {
                ValueAnimator fadeIn = ValueAnimator.ofFloat(0.5f, 1f);
                fadeIn.setDuration(500);
                fadeIn.addUpdateListener(animation -> {
                    float alpha = (float) animation.getAnimatedValue();
                    circularTimerView.setAlpha(alpha);
                });
                fadeIn.start();
            }
            private void animateMoveDownFast(View view) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 70f, 270f);
                animator.setDuration(1);
                animator.start();
            }
            private void animateMoveDown(View view) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 70f, 270f);
                animator.setDuration(1250);
                animator.start();
            }
            private void animateMoveUp(View view) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", 270f, 70f);
                animator.setDuration(1250);
                animator.start();
            }
    @Override
    protected void onStop() {
        super.onStop();
    }
}