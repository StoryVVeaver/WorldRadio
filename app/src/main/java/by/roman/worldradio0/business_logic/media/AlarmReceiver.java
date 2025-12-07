package by.roman.worldradio0.business_logic.media;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import by.roman.worldradio0.business_logic.PlayerStarter;
import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.components.SingletonComponent;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {

        Log.d(TAG, "onReceive triggered. Action: " + intent.getAction());

        if ("CANCEL_ALARM".equals(intent.getAction())) {
            Log.d(TAG, "Cancel alarm requested");

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    1001,
                    new Intent(context, AlarmReceiver.class),
                    PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
            );

            if (pi != null) {
                Log.d(TAG, "Existing alarm found → cancelling");
                am.cancel(pi);
                pi.cancel();
            } else {
                Log.d(TAG, "No existing alarm to cancel");
            }

            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(5005);

            Log.d(TAG, "Notification 5005 cancelled");
            return;
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(5005);

        String uuid = intent.getStringExtra("uuid");
        if (uuid == null) {
            Log.e(TAG, "No UUID provided in intent!");
            return;
        }
        Log.d(TAG, "UUID received: " + uuid);

        PlayerStarter playerStarter = EntryPointAccessors.fromApplication(
                context.getApplicationContext(),
                PlayerStarterEntryPoint.class
        ).getPlayerStarter();

        try {
            playerStarter.start(uuid);
            Log.d(TAG, "playerStarter.start(uuid) executed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error while calling playerStarter.start(uuid): " + e.getMessage(), e);
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent.class)
    public interface PlayerStarterEntryPoint {
        PlayerStarter getPlayerStarter();
    }
}
