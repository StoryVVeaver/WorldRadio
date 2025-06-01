package by.roman.worldradio0.business_logic.data.dto;

import by.roman.worldradio0.business_logic.data.models.Settings;

public class SettingsDTO {
    private int userId;
    public int audioBalance;
    public int gainRecord;
    public int gainBroadcast;
    public int agcEnabled;
    public int crossfadeEnabled;
    public int crossfadeTime;

    public int networkType;

    public int timerSecondsEnabled;
    public int timerDotsType;
    public int notification_enabled;
    public int navigation_type;

    public Settings toModel(){
        return new Settings(
                userId,
                audioBalance, gainRecord, gainBroadcast, agcEnabled, crossfadeEnabled, crossfadeTime,
                networkType,
                timerSecondsEnabled, timerDotsType, notification_enabled, navigation_type
        );

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

        return dto;
    }

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
}
