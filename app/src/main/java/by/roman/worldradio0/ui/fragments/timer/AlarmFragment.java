package by.roman.worldradio0.ui.fragments.timer;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Calendar;
import java.util.Locale;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.adapters.TimerWheelAdapter;
import by.roman.worldradio0.business_logic.media.AlarmReceiver;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlarmFragment extends Fragment {

    private static final String ARG_UUID = "arg_uuid";
    private String uuid;
    private TimerWheelAdapter hourAdapter;
    private TimerWheelAdapter minuteAdapter;

    public static AlarmFragment newInstance(String uuid) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UUID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uuid = getArguments().getString(ARG_UUID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView hourWheel = view.findViewById(R.id.hourWheel);
        RecyclerView minuteWheel = view.findViewById(R.id.minuteWheel);
        Button startButton = view.findViewById(R.id.startButton);

        hourAdapter = new TimerWheelAdapter(requireActivity(), 24);
        minuteAdapter = new TimerWheelAdapter(requireActivity(), 60);

        setupWheel(hourWheel, hourAdapter, 24);
        setupWheel(minuteWheel, minuteAdapter, 60);

        startButton.setOnClickListener(v -> scheduleAlarm());
    }

    private void setupWheel(@NonNull RecyclerView recyclerView, TimerWheelAdapter adapter, int range) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        final LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.post(() -> {
            int mid = adapter.getItemCount() / 2;
            int startOffset = mid % range;
            int zeroPosition = mid - startOffset;

            scrollToPosition(0, recyclerView, adapter, zeroPosition);
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
                    }
                }
            }
        });
    }

    private void scrollToPosition(int value, @NonNull RecyclerView recyclerView, TimerWheelAdapter adapter, int zeroPosition) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int target = zeroPosition + value;
            layoutManager.scrollToPositionWithOffset(target, -20);
            adapter.setSelectedPosition(target);
        }
    }

    private void scheduleAlarm() {
        int hour = hourAdapter.getRealValue();
        int minute = minuteAdapter.getRealValue();

        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        if (target.getTimeInMillis() <= now.getTimeInMillis()) {
            target.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("uuid", uuid);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                1001,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), pendingIntent);
        }

        showAlarmNotification(target.getTimeInMillis());
    }

    private void showAlarmNotification(long triggerTime) {
        Context ctx = requireContext();
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "alarm_channel",
                    "Запланированные сигналы",
                    NotificationManager.IMPORTANCE_HIGH
            );
            nm.createNotificationChannel(channel);
        }

        Intent cancelIntent = new Intent(ctx, AlarmReceiver.class);
        cancelIntent.setAction("CANCEL_ALARM");
        cancelIntent.putExtra("uuid", uuid);

        PendingIntent cancelPending = PendingIntent.getBroadcast(
                ctx,
                2002,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(triggerTime);

        String timeText = String.format(Locale.getDefault(), "%02d:%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "alarm_channel")
                .setSmallIcon(R.drawable.timer_home)
                .setContentTitle(getResources().getString(R.string.alarm))
                .setContentText(getResources().getString(R.string.alarm_time) + " " + timeText)
                .addAction(R.drawable.delete, "Отмена", cancelPending)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        nm.notify(5005, builder.build());
    }
}