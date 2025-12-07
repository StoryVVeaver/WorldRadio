package by.roman.worldradio0.ui.fragments.timer;

import android.app.AlarmManager;
import android.app.Notification;
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
        RecyclerView hourWheel = view.findViewById(R.id.hourWheel);
        RecyclerView minuteWheel = view.findViewById(R.id.minuteWheel);
        Button startButton = view.findViewById(R.id.startButton);

        hourAdapter = new TimerWheelAdapter(requireContext(), 24);
        minuteAdapter = new TimerWheelAdapter(requireContext(), 60);

        setupWheel(hourWheel, hourAdapter);
        setupWheel(minuteWheel, minuteAdapter);

        startButton.setOnClickListener(v -> scheduleAlarm());
    }

    private void setupWheel(RecyclerView wheel, TimerWheelAdapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        wheel.setLayoutManager(layoutManager);
        wheel.setAdapter(adapter);

        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(wheel);

        wheel.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    View centerView = snapHelper.findSnapView(layoutManager);
                    if (centerView != null) {
                        int pos = layoutManager.getPosition(centerView);
                        adapter.setSelectedPosition(pos);
                    }
                }
            }
        });

        wheel.scrollToPosition(adapter.getItemCount() / 2);
    }

    private void scheduleAlarm() {
        int hour = hourAdapter.getRealValue();
        int minute = minuteAdapter.getRealValue();

        Calendar now = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);
        target.set(Calendar.SECOND, 0);

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
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), pendingIntent);

        showAlarmNotification(target.getTimeInMillis());
    }

    private void showAlarmNotification(long triggerTime) {
        Context ctx = requireContext();
        NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                "alarm_channel",
                "Запланированные сигналы",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        nm.createNotificationChannel(channel);

        Intent cancelIntent = new Intent(ctx, AlarmReceiver.class);
        cancelIntent.setAction("CANCEL_ALARM");

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

        Notification notification = new NotificationCompat.Builder(ctx, "alarm_channel")
                .setSmallIcon(R.drawable.timer_home)
                .setContentTitle("Будильник установлен")
                .setContentText("Сработает в " + timeText)
                .addAction(R.drawable.delete, "Отменить", cancelPending)
                .setOngoing(true)
                .build();

        nm.notify(5005, notification);
    }

}
