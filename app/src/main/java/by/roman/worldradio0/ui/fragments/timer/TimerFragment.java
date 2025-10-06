package by.roman.worldradio0.ui.fragments.timer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.TimerWheelAdapter;
import by.roman.worldradio0.business_logic.view_models.SettingsViewModel;
import by.roman.worldradio0.business_logic.view_models.StateViewModel;
import by.roman.worldradio0.business_logic.view_models.TimerViewModel;
import by.roman.worldradio0.ui.elements.view.CircularTimerView;

/**
 * Refactored TimerFragment with clearer state machine and readable methods.
 * This version avoids compact one-line statements and keeps code explicit and maintainable.
 */
public class TimerFragment extends Fragment {

    private static final String TAG = "TimerFragment";
    private static final long TICK_INTERVAL_MS = 1000L;

    private RecyclerView recyclerHour;
    private RecyclerView recyclerMinute;
    private RecyclerView recyclerSecond;

    private TimerWheelAdapter hourAdapter;
    private TimerWheelAdapter minuteAdapter;
    private TimerWheelAdapter secondAdapter;

    private CircularTimerView circularTimerView;
    private CountDownTimer countDownTimer;

    private TimerViewModel viewModel;
    private SettingsViewModel settingsViewModel;
    private StateViewModel stateViewModel;

    private ImageView pauseButton;
    private ImageView playButton;
    private ImageView startButton;
    private ImageView stopButton;
    private ImageView backButton;
    private ImageView divider1;
    private ImageView divider2;

    private ConstraintLayout time;

    private long totalTime = 0L;
    private long timeRemaining = 0L;

    private enum TimerState {
        IDLE,
        RUNNING,
        PAUSED
    }

    private enum TimerAction {
        START,
        PAUSE,
        RESUME,
        STOP
    }

    private TimerState state = TimerState.IDLE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        long startTime = System.nanoTime();
        Log.v(TAG + ":performance", "onCreateView started");
        findViews(view);
        initViewModels();
        initAdapters();
        attachListeners();
        observeViewModel();
        Log.v(TAG + ":performance", "onCreateView total execution time: "
                + (System.nanoTime() - startTime) / 1_000_000.0 + "ms");
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelTimer();
    }
    private void findViews(@NonNull View root) {
        circularTimerView = root.findViewById(R.id.circularTimerView);
        pauseButton = root.findViewById(R.id.pauseButtonView);
        playButton = root.findViewById(R.id.playButtonView);
        startButton = root.findViewById(R.id.startButtonView);
        backButton = root.findViewById(R.id.backButtonTimerView);
        stopButton = root.findViewById(R.id.stopButtonView);
        recyclerHour = root.findViewById(R.id.recyclerHour);
        recyclerMinute = root.findViewById(R.id.recyclerMinute);
        recyclerSecond = root.findViewById(R.id.recyclerSecond);
        divider1 = root.findViewById(R.id.dotDivider1);
        divider2 = root.findViewById(R.id.dotDivider2);
        time = root.findViewById(R.id.setTime);
    }
    private void initViewModels() {
        viewModel = new ViewModelProvider(requireActivity()).get(TimerViewModel.class);
        stateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
    }
    private void initAdapters() {
        hourAdapter = new TimerWheelAdapter(requireActivity(), 24);
        setupRecyclerView(recyclerHour, hourAdapter);
        hourAdapter.setSelectedPosition(hourAdapter.getItemCount() / 2);
        minuteAdapter = new TimerWheelAdapter(requireActivity(), 60);
        setupRecyclerView(recyclerMinute, minuteAdapter);
        minuteAdapter.setSelectedPosition(minuteAdapter.getItemCount() / 2);
        boolean secondsEnabled = settingsViewModel.getSettingsModel().getTimerSecondsEnabled() == 1;
        if (secondsEnabled) {
            recyclerSecond.setVisibility(VISIBLE);
            divider2.setVisibility(VISIBLE);
            secondAdapter = new TimerWheelAdapter(requireActivity(), 60);
            setupRecyclerView(recyclerSecond, secondAdapter);
            secondAdapter.setSelectedPosition(secondAdapter.getItemCount() / 2);
        } else {
            recyclerSecond.setVisibility(GONE);
            divider2.setVisibility(GONE);
        }
        int dotsType = settingsViewModel.getSettingsModel().getTimerDotsType();
        if (dotsType == 0) {
            divider1.setImageDrawable(AppCompatResources.getDrawable(requireActivity(), R.drawable.circle));
            divider2.setImageDrawable(AppCompatResources.getDrawable(requireActivity(), R.drawable.circle));
        } else {
            divider1.setImageDrawable(AppCompatResources.getDrawable(requireActivity(), R.drawable.romb));
            divider2.setImageDrawable(AppCompatResources.getDrawable(requireActivity(), R.drawable.romb));
        }
        circularTimerView.setMaxTimeMillis(totalTime + 1L);
        circularTimerView.setCurrentTimeMillis(totalTime);
    }
    private void attachListeners() {
        startButton.setOnClickListener(v -> handleAction(TimerAction.START));
        stopButton.setOnClickListener(v -> handleAction(TimerAction.STOP));
        playButton.setOnClickListener(v -> handleAction(TimerAction.RESUME));
        pauseButton.setOnClickListener(v -> handleAction(TimerAction.PAUSE));
        backButton.setOnClickListener(v -> stateViewModel.closeFullscreen());
    }
    private void observeViewModel() {
        viewModel.loadData();
        viewModel.getDuration().observe(getViewLifecycleOwner(), newDuration -> {
            if (newDuration == null) return;
            this.totalTime = newDuration;
            circularTimerView.setMaxTimeMillis(totalTime);
            setPickersFromTotalTime();
        });
        viewModel.getTime_left().observe(getViewLifecycleOwner(), left -> {
            if (left == null) return;
            this.timeRemaining = left;
            circularTimerView.setCurrentTimeMillis(timeRemaining);
        });
        viewModel.getFlag().observe(getViewLifecycleOwner(), flag -> {
            if (flag == null) return;
            if (flag) {
                startCountDown(timeRemaining);
                setState(TimerState.RUNNING);
            }
        });
        viewModel.getFlag2().observe(getViewLifecycleOwner(), flag2 -> {
            if (flag2 == null) return;
            if (flag2) {
                cancelTimer();
                setState(TimerState.PAUSED);
                circularTimerView.setMaxTimeMillis(totalTime);
                circularTimerView.setCurrentTimeMillis(timeRemaining);
            }
        });
    }
    private void handleAction(TimerAction action) {
        switch (action) {
            case START:
                if (state == TimerState.RUNNING) {
                    return;
                }
                updateTotalTimeFromPickers();
                viewModel.startTimer(totalTime);
                startCountDown(totalTime);
                setState(TimerState.RUNNING);
                break;
            case PAUSE:
                if (state != TimerState.RUNNING) {
                    return;
                }
                viewModel.pauseTimer();
                cancelTimer();
                setState(TimerState.PAUSED);
                break;
            case RESUME:
                if (state != TimerState.PAUSED) {
                    return;
                }
                viewModel.resumeTimer();
                startCountDown(timeRemaining);
                setState(TimerState.RUNNING);
                break;
            case STOP:
                viewModel.stopTimer();
                cancelTimer();
                setState(TimerState.IDLE);
                break;
            default:
                break;
        }
    }
    private void setState(TimerState newState) {
        this.state = newState;
        updateUiForState(newState);
    }
    private void startCountDown(long durationMs) {
        cancelTimer();
        countDownTimer = new CountDownTimer(durationMs, TICK_INTERVAL_MS) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemaining = millisUntilFinished;
                circularTimerView.setCurrentTimeMillis(millisUntilFinished);
            }
            @Override
            public void onFinish() {
                circularTimerView.setCurrentTimeMillis(0);
                setState(TimerState.IDLE);
            }
        };
        countDownTimer.start();
    }
    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
    private void updateUiForState(TimerState currentState) {
        switch (currentState) {
            case RUNNING:
                animateMoveDown(circularTimerView);
                time.setVisibility(GONE);
                startButton.setVisibility(GONE);
                stopButton.setVisibility(VISIBLE);
                pauseButton.setVisibility(VISIBLE);
                playButton.setVisibility(GONE);
                stopButton.setEnabled(false);
                stopButton.setAlpha(0.5f);
                break;
            case PAUSED:
                stopButton.setEnabled(true);
                stopButton.setAlpha(1f);
                playButton.setVisibility(VISIBLE);
                pauseButton.setVisibility(GONE);
                fadeOutOnPause();
                break;
            case IDLE:
            default:
                animateMoveUp(circularTimerView);
                time.setVisibility(VISIBLE);
                startButton.setVisibility(VISIBLE);
                stopButton.setVisibility(GONE);
                playButton.setVisibility(GONE);
                pauseButton.setVisibility(GONE);
                fadeInOnResume();
                circularTimerView.setCurrentTimeMillis(totalTime);
                break;
        }
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView, TimerWheelAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        final LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.post(() -> {
            int middlePosition = adapter.getItemCount() / 2;
            scrollToPosition(middlePosition, recyclerView, adapter);
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
                        if (state == TimerState.IDLE) {
                            updateTotalTimeFromPickers();
                        }
                    }
                }
            }
        });
    }
    private void scrollToPosition(int value, @NonNull RecyclerView recyclerView, TimerWheelAdapter adapter) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int target = (adapter.getItemCount() / 2) + value;
            layoutManager.scrollToPositionWithOffset(target, -20);
            adapter.setSelectedPosition(target);
        }
    }
    private void updateTotalTimeFromPickers() {
        int hours = hourAdapter.getSelectedPosition() % 24;
        int minutes = minuteAdapter.getSelectedPosition() % 60;
        long computed = (hours * 3_600_000L) + (minutes * 60_000L);
        boolean secondsEnabled = settingsViewModel.getSettingsModel().getTimerSecondsEnabled() == 1;
        if (secondsEnabled && secondAdapter != null) {
            int seconds = secondAdapter.getSelectedPosition() % 60;
            computed = computed + (seconds * 1000L);
        }
        this.totalTime = computed;
        circularTimerView.setMaxTimeMillis(totalTime + 1L);
        circularTimerView.setCurrentTimeMillis(totalTime);
    }
    private void setPickersFromTotalTime() {
        int hours = (int) (totalTime / 3_600_000L);
        scrollToPosition(hours, recyclerHour, hourAdapter);
        long remaining = totalTime % 3_600_000L;
        int minutes = (int) (remaining / 60_000L);
        scrollToPosition(minutes, recyclerMinute, minuteAdapter);
        remaining = remaining % 60_000L;
        int seconds = (int) (remaining / 1_000L);
        boolean secondsEnabled = settingsViewModel.getSettingsModel().getTimerSecondsEnabled() == 1;
        if (secondsEnabled && secondAdapter != null) {
            scrollToPosition(seconds, recyclerSecond, secondAdapter);
        }
        circularTimerView.setMaxTimeMillis(totalTime + 1L);
        circularTimerView.setCurrentTimeMillis(totalTime);
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
}