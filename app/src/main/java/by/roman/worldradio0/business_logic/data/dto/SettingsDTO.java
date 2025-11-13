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

    private int par1;
    private int par2;
    private int par3;
    private int par4;
    private int par5;
    private int par6;
    private int par7;
    private int par8;
    private int par9;
    private int par10;

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
