package by.roman.worldradio0.business_logic.data.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.Settings;

public class SettingsDao {
    protected static final String TABLE_SETTINGS = "settings";
    protected static final String COLUMN_USER_ID_SETTINGS = "id";
    protected static final String COLUMN_AUDIO_BALANCE = "audio_balance";
    protected static final String COLUMN_GAIN_RECORD = "gain_record";
    protected static final String COLUMN_GAIN_BROADCAST = "gain_broadcast";
    protected static final String COLUMN_AGC_ENABLED = "agc_enabled";
    protected static final String COLUMN_CROSSFADE_ENABLED = "crossfade_enabled";
    protected static final String COLUMN_CROSSFADE_TIME = "crossfade_time";
    protected static final String COLUMN_NETWORK_TYPE = "network_type";
    protected static final String COLUMN_TIMER_SECONDS_ENABLED = "timer_seconds_enabled";
    protected static final String COLUMN_TIMER_DOTS_TYPE = "timer_dots_type";
    protected static final String COLUMN_NOTIFICATION_ENABLED = "notification_enabled";
    protected static final String COLUMN_NAVIGATION_TYPE = "navigation_type";
    protected static final String COLUMN_X = "x";
    protected static final String COLUMN_Y = "y";
    protected static final String COLUMN_Z = "z";
    protected static final String COLUMN_C = "c";

    protected static final String CREATE_TABLE_SETTINGS = "CREATE TABLE " + TABLE_SETTINGS + " (" +
            COLUMN_USER_ID_SETTINGS       + " INTEGER PRIMARY KEY, " +
            COLUMN_AUDIO_BALANCE          + " INTEGER, " +
            COLUMN_GAIN_RECORD            + " INTEGER, " +
            COLUMN_GAIN_BROADCAST         + " INTEGER, " +
            COLUMN_AGC_ENABLED            + " INTEGER, " +
            COLUMN_CROSSFADE_ENABLED      + " INTEGER, " +
            COLUMN_CROSSFADE_TIME         + " INTEGER, " +
            COLUMN_NETWORK_TYPE           + " INTEGER, " +
            COLUMN_TIMER_SECONDS_ENABLED  + " INTEGER, " +
            COLUMN_TIMER_DOTS_TYPE        + " INTEGER, " +
            COLUMN_NOTIFICATION_ENABLED   + " INTEGER, " +
            COLUMN_NAVIGATION_TYPE        + " INTEGER, " +
            COLUMN_X        + " INTEGER, " +
            COLUMN_Y        + " INTEGER, " +
            COLUMN_Z        + " INTEGER, " +
            COLUMN_C        + " INTEGER, " +
            "FOREIGN KEY (" + COLUMN_USER_ID_SETTINGS + ") REFERENCES " + UserDao.TABLE_USER + "(" + UserDao.COLUMN_ID_USER + ") ON DELETE CASCADE" +
            ");";
    private final SQLiteDatabase db;
    public SettingsDao(SQLiteDatabase db){
        this.db = db;
    }
    public void setSettings(@NonNull SettingsDTO dto) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_AUDIO_BALANCE, dto.getAudioBalance());
        values.put(COLUMN_GAIN_RECORD, dto.getGainRecord());
        values.put(COLUMN_GAIN_BROADCAST, dto.getGainBroadcast());
        values.put(COLUMN_AGC_ENABLED, dto.getAgcEnabled());
        values.put(COLUMN_CROSSFADE_ENABLED, dto.getCrossfadeEnabled());
        values.put(COLUMN_CROSSFADE_TIME, dto.getCrossfadeTime());

        values.put(COLUMN_NETWORK_TYPE, dto.getNetworkType());

        values.put(COLUMN_TIMER_SECONDS_ENABLED, dto.getTimerSecondsEnabled());
        values.put(COLUMN_TIMER_DOTS_TYPE, dto.getTimerDotsType());
        values.put(COLUMN_NOTIFICATION_ENABLED, dto.getNotification_enabled());
        values.put(COLUMN_NAVIGATION_TYPE, dto.getNavigation_type());

        String selection = COLUMN_USER_ID_SETTINGS + " = ?";
        String[] selectionArgs = {String.valueOf(dto.getUserId())};

        db.update(TABLE_SETTINGS, values, selection, selectionArgs);
    }
    public Settings getSetting(int id) {
        Cursor cursor = db.query(TABLE_SETTINGS,
                new String[]{
                        COLUMN_USER_ID_SETTINGS,
                        COLUMN_AUDIO_BALANCE, COLUMN_GAIN_RECORD, COLUMN_GAIN_BROADCAST, COLUMN_AGC_ENABLED, COLUMN_CROSSFADE_ENABLED, COLUMN_CROSSFADE_TIME,
                        COLUMN_NETWORK_TYPE,
                        COLUMN_TIMER_SECONDS_ENABLED, COLUMN_TIMER_DOTS_TYPE, COLUMN_NOTIFICATION_ENABLED, COLUMN_NAVIGATION_TYPE
                },
                COLUMN_USER_ID_SETTINGS + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToNext()) {
            try (cursor) {
                int idIndex = cursor.getColumnIndex(COLUMN_USER_ID_SETTINGS);

                int audioBalanceIndex = cursor.getColumnIndex(COLUMN_AUDIO_BALANCE);
                int gainRecordIndex = cursor.getColumnIndex(COLUMN_GAIN_RECORD);
                int gainBroadcastIndex = cursor.getColumnIndex(COLUMN_GAIN_BROADCAST);
                int agcEnabledIndex = cursor.getColumnIndex(COLUMN_AGC_ENABLED);
                int crossfadeEnabledIndex = cursor.getColumnIndex(COLUMN_CROSSFADE_ENABLED);
                int crossfadeTimeIndex = cursor.getColumnIndex(COLUMN_CROSSFADE_TIME);

                int networkTypeIndex = cursor.getColumnIndex(COLUMN_NETWORK_TYPE);

                int timerSecondsIndex = cursor.getColumnIndex(COLUMN_TIMER_SECONDS_ENABLED);
                int timerDotsIndex = cursor.getColumnIndex(COLUMN_TIMER_DOTS_TYPE);
                int notificationEnabledIndex = cursor.getColumnIndex(COLUMN_NOTIFICATION_ENABLED);
                int navigationTypeIndex = cursor.getColumnIndex(COLUMN_NAVIGATION_TYPE);

                if (idIndex != -1 &&
                        audioBalanceIndex != -1 &&
                        gainRecordIndex != -1 &&
                        gainBroadcastIndex != -1 &&
                        agcEnabledIndex != -1 &&
                        crossfadeEnabledIndex != -1 &&
                        crossfadeTimeIndex != -1 &&

                        networkTypeIndex != -1 &&

                        timerSecondsIndex != -1 &&
                        timerDotsIndex != -1 &&
                        notificationEnabledIndex != -1 &&
                        navigationTypeIndex != -1) {

                    return new Settings(
                            cursor.getInt(idIndex),

                            cursor.getInt(audioBalanceIndex),
                            cursor.getInt(gainRecordIndex),
                            cursor.getInt(gainBroadcastIndex),
                            cursor.getInt(agcEnabledIndex),
                            cursor.getInt(crossfadeEnabledIndex),
                            cursor.getInt(crossfadeTimeIndex),

                            cursor.getInt(networkTypeIndex),

                            cursor.getInt(timerSecondsIndex),
                            cursor.getInt(timerDotsIndex),
                            cursor.getInt(notificationEnabledIndex),
                            cursor.getInt(navigationTypeIndex)
                    );
                }
            }
        }

        return null;
    }
    public void removeSettings(int id) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_AUDIO_BALANCE, 0);
        values.put(COLUMN_GAIN_RECORD, 0);
        values.put(COLUMN_GAIN_BROADCAST, 0);
        values.put(COLUMN_AGC_ENABLED, 0);
        values.put(COLUMN_CROSSFADE_ENABLED, 0);
        values.put(COLUMN_CROSSFADE_TIME, 0);

        values.put(COLUMN_NETWORK_TYPE, 0);

        values.put(COLUMN_TIMER_SECONDS_ENABLED, 0);
        values.put(COLUMN_TIMER_DOTS_TYPE, 0);
        values.put(COLUMN_NOTIFICATION_ENABLED, 1);
        values.put(COLUMN_NAVIGATION_TYPE, 0);

        String selection = COLUMN_USER_ID_SETTINGS + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.update(TABLE_SETTINGS, values, selection, selectionArgs);
    }
    public void addSettings(@NonNull SettingsDTO dto) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_SETTINGS, dto.getUserId());

        values.put(COLUMN_AUDIO_BALANCE, dto.getAudioBalance());
        values.put(COLUMN_GAIN_RECORD, dto.getGainRecord());
        values.put(COLUMN_GAIN_BROADCAST, dto.getGainBroadcast());
        values.put(COLUMN_AGC_ENABLED, dto.getAgcEnabled());
        values.put(COLUMN_CROSSFADE_ENABLED, dto.getCrossfadeEnabled());
        values.put(COLUMN_CROSSFADE_TIME, dto.getCrossfadeTime());

        values.put(COLUMN_NETWORK_TYPE, dto.getNetworkType());

        values.put(COLUMN_TIMER_SECONDS_ENABLED, dto.getTimerSecondsEnabled());
        values.put(COLUMN_TIMER_DOTS_TYPE, dto.getTimerDotsType());
        values.put(COLUMN_NOTIFICATION_ENABLED, dto.getNotification_enabled());
        values.put(COLUMN_NAVIGATION_TYPE, dto.getNavigation_type());

        db.insertWithOnConflict(TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

}
