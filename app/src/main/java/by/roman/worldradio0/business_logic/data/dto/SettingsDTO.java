package by.roman.worldradio0.business_logic.data.dto;

import com.google.gson.annotations.SerializedName;
import by.roman.worldradio0.business_logic.data.models.Settings;

public class SettingsDTO {
    @SerializedName("id")
    private int userId;

    @SerializedName("audio_balance")
    public int audioBalance;

    @SerializedName("gain_record")
    public int gainRecord;

    @SerializedName("gain_broadcast")
    public int gainBroadcast;

    @SerializedName("agc_enabled")
    public int agcEnabled;

    @SerializedName("crossfade_enabled")
    public int crossfadeEnabled;

    @SerializedName("crossfade_time")
    public int crossfadeTime;

    @SerializedName("network_type")
    public int networkType;

    @SerializedName("timer_seconds_enabled")
    public int timerSecondsEnabled;

    @SerializedName("timer_dots_type")
    public int timerDotsType;

    @SerializedName("notification_enabled")
    public int notification_enabled;

    @SerializedName("navigation_type")
    public int navigation_type;

    @SerializedName("snap_enabled")
    private int snap_enabled;
    public Settings toModel(){
        Settings sett = new Settings(userId);
        sett.setAudioBalance(audioBalance);
        sett.setGainRecord(gainRecord);
        sett.setGainBroadcast(gainBroadcast);
        sett.setCrossfadeEnabled(crossfadeEnabled);
        sett.setCrossfadeTime(crossfadeTime);

        sett.setNetworkType(networkType);

        sett.setTimerSecondsEnabled(timerSecondsEnabled);
        sett.setTimerDotsType(timerDotsType);
        sett.setNotificationEnabled(notification_enabled);
        sett.setNavigationType(navigation_type);

        sett.setSnapEnabled(snap_enabled);
        return sett;
    }

    public SettingsDTO fromModel(Settings settings) {
        SettingsDTO dto = new SettingsDTO();
        dto.userId = settings.getId();
        dto.audioBalance = settings.getAudioBalance();
        dto.gainRecord = settings.getGainRecord();
        dto.gainBroadcast = settings.getGainBroadcast();
        dto.agcEnabled = settings.getAgcEnabled();
        dto.crossfadeEnabled = settings.getCrossfadeEnabled();
        dto.crossfadeTime = settings.getCrossfadeTime();
        dto.networkType = settings.getNetworkType();
        dto.timerSecondsEnabled = settings.getTimerSecondsEnabled();
        dto.timerDotsType = settings.getTimerDotsType();
        dto.notification_enabled = settings.getNotificationEnabled();
        dto.navigation_type = settings.getNavigationType();
        dto.snap_enabled = settings.getSnapEnabled();

        return dto;
    }

    // Геттеры
    public int getUserId() {
        return userId;
    }

    public int getAudioBalance() {
        return audioBalance;
    }

    public int getGainRecord() {
        return gainRecord;
    }

    public int getGainBroadcast() {
        return gainBroadcast;
    }

    public int getAgcEnabled() {
        return agcEnabled;
    }

    public int getCrossfadeEnabled() {
        return crossfadeEnabled;
    }

    public int getCrossfadeTime() {
        return crossfadeTime;
    }

    public int getNetworkType() {
        return networkType;
    }

    public int getTimerSecondsEnabled() {
        return timerSecondsEnabled;
    }

    public int getTimerDotsType() {
        return timerDotsType;
    }

    public int getNotification_enabled() {
        return notification_enabled;
    }

    public int getNavigation_type() {
        return navigation_type;
    }

    public int getSnapEnabled() {
        return snap_enabled;
    }
}