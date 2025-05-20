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
    public int radioModuleEnabled;

    public int timerSecondsEnabled;
    public int timerDotsType;

    public Settings toModel(){
        return new Settings(
                userId,
                audioBalance, gainRecord, gainBroadcast, agcEnabled, crossfadeEnabled, crossfadeTime,
                networkType, radioModuleEnabled,
                timerSecondsEnabled, timerDotsType
        );

    }
    public SettingsDTO fromModel(Settings settings) {
        SettingsDTO dto = new SettingsDTO();
        dto.userId = settings.getUserId();
        dto.audioBalance = settings.getAudioBalance();
        dto.gainRecord = settings.getGainRecord();
        dto.gainBroadcast = settings.getGainBroadcast();
        dto.agcEnabled = settings.getAgcEnabled();
        dto.crossfadeEnabled = settings.getCrossfadeEnabled();
        dto.crossfadeTime = settings.getCrossfadeTime();
        dto.networkType = settings.getNetworkType();
        dto.radioModuleEnabled = settings.getRadioModuleEnabled();
        dto.timerSecondsEnabled = settings.getTimerSecondsEnabled();
        dto.timerDotsType = settings.getTimerDotsType();


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
    public int getRadioModuleEnabled() {
        return radioModuleEnabled;
    }
    public int getTimerSecondsEnabled() {
        return timerSecondsEnabled;
    }
    public int getTimerDotsType() {
        return timerDotsType;
    }
}
