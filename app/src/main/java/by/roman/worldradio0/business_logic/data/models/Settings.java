package by.roman.worldradio0.business_logic.data.models;

public class Settings {
    private final int userId;
    private int audio_balance;
    private int gain_record;
    private int gain_broadcast;
    private int agc_enabled;
    private int crossfade_enabled;
    private int crossfade_time;

    private int network_type;
    private int radio_module_enabled;

    private int timer_seconds_enabled;
    private int timer_dots_type;


    public Settings(int userId,
                    int audio_balance,
                    int gain_record,
                    int gain_broadcast,
                    int agc_enabled,
                    int crossfade_enabled,
                    int crossfade_time,
                    int network_type,
                    int radio_module_enabled,
                    int timer_seconds_enabled,
                    int timer_dots_type) {
        this.userId = userId;
        this.audio_balance = audio_balance;
        this.gain_record = gain_record;
        this.gain_broadcast = gain_broadcast;
        this.agc_enabled = agc_enabled;
        this.crossfade_enabled = crossfade_enabled;
        this.crossfade_time = crossfade_time;
        this.network_type = network_type;
        this.radio_module_enabled = radio_module_enabled;
        this.timer_seconds_enabled = timer_seconds_enabled;
        this.timer_dots_type = timer_dots_type;
    }

    // Getters
    public int getUserId() { return userId; }
    public int getAudioBalance() {
        return audio_balance;
    }
    public int getGainRecord() {
        return gain_record;
    }
    public int getGainBroadcast() {
        return gain_broadcast;
    }
    public int getAgcEnabled() {
        return agc_enabled;
    }
    public int getCrossfadeEnabled() {
        return crossfade_enabled;
    }
    public int getCrossfadeTime() {
        return crossfade_time;
    }

    public int getNetworkType(){
        return network_type;
    }
    public int getRadioModuleEnabled(){
        return radio_module_enabled;
    }

    public int getTimerDotsType() {
        return timer_dots_type;
    }
    public int getTimerSecondsEnabled() {
        return timer_seconds_enabled;
    }

    //Setters
    public void setAudioBalance(int audio_balance) {
        this.audio_balance = audio_balance;
    }
    public void setGainRecord(int gain_record) {
        this.gain_record = gain_record;
    }
    public void setGainBroadcast(int gain_broadcast) {
        this.gain_broadcast = gain_broadcast;
    }
    public void setAgcEnabled(int agc_enabled) {
        this.agc_enabled = agc_enabled;
    }
    public void setCrossfadeEnabled(int crossfade_enabled) {
        this.crossfade_enabled = crossfade_enabled;
    }
    public void setCrossfadeTime(int crossfade_time) {
        this.crossfade_time = crossfade_time;
    }

    public void setNetworkType(int network_type){
        this.network_type = network_type;
    }
    public void setRadioModuleEnabled(int radioModuleEnabled){
        this.radio_module_enabled = radioModuleEnabled;
    }

    public void setTimerSecondsEnabled(int timer_seconds_enabled) {
        this.timer_seconds_enabled = timer_seconds_enabled;
    }
    public void setTimerDotsType(int timer_dots_type) {
        this.timer_dots_type = timer_dots_type;
    }
}
