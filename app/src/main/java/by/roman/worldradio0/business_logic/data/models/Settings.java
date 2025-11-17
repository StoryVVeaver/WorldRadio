package by.roman.worldradio0.business_logic.data.models;

public class Settings {
    private int id;
    private int audio_balance;
    private int gain_record;
    private int gain_broadcast;
    private int agc_enabled;
    private int crossfade_enabled;
    private int crossfade_time;

    private int network_type;

    private int timer_seconds_enabled;
    private int timer_dots_type;
    private int notification_enabled;
    private int navigation_type;

    private int snap_enabled;

    public Settings(int id) {
        this.id = id;
        this.audio_balance = 0;
        this.gain_record = 0;
        this.gain_broadcast = 0;
        this.agc_enabled = 0;
        this.crossfade_enabled = 0;
        this.crossfade_time = 0;

        this.network_type = 0;

        this.timer_seconds_enabled = 1;
        this.timer_dots_type = 0;
        this.notification_enabled = 1;
        this.navigation_type = 0;

        this.snap_enabled = 0;
    }
    public Settings(int id,
                    int audio_balance, int gain_broadcast, int gain_record, int agc_enabled, int crossfade_enabled, int crossfade_time,
                    int network_type,
                    int timer_seconds_enabled, int timer_dots_type, int notification_enabled, int navigation_type,
                    int snap_enabled) {
        this.id = id;
        this.audio_balance = audio_balance;
        this.gain_record = gain_record;
        this.gain_broadcast = gain_broadcast;
        this.agc_enabled = agc_enabled;
        this.crossfade_enabled = crossfade_enabled;
        this.crossfade_time = crossfade_time;

        this.network_type = network_type;

        this.timer_seconds_enabled = timer_seconds_enabled;
        this.timer_dots_type = timer_dots_type;
        this.notification_enabled = notification_enabled;
        this.navigation_type = navigation_type;

        this.snap_enabled = snap_enabled;
    }

    // Getters
    public int getId() { return id; }
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
    public int getTimerDotsType() {
        return timer_dots_type;
    }
    public int getTimerSecondsEnabled() {
        return timer_seconds_enabled;
    }
    public int getNotificationEnabled(){
        return notification_enabled;
    }
    public int getNavigationType(){
        return navigation_type;
    }

    //Setters
    public void setId(int id){
        this.id = id;
    }
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
    public void setTimerSecondsEnabled(int timer_seconds_enabled) {
        this.timer_seconds_enabled = timer_seconds_enabled;
    }
    public void setTimerDotsType(int timer_dots_type) {
        this.timer_dots_type = timer_dots_type;
    }
    public void setNotificationEnabled(int notification_enabled){
        this.notification_enabled = notification_enabled;
    }
    public void setNavigationType(int navigation_type) {
        this.navigation_type = navigation_type;
    }
    public int getSnapEnabled() {
        return snap_enabled;
    }
    public void setSnapEnabled(int snap_enabled) {
        this.snap_enabled = snap_enabled;
    }
}
