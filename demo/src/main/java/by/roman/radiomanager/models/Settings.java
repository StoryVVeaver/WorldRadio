package by.roman.radiomanager.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_settings")
public class Settings {
    @Id
    private Long id;
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

    public Settings(){
        
    }

    public Settings(Long id, int audio_balance, int gain_record, int gain_broadcast, int agc_enabled, int crossfade_enabled, int crossfade_time, int network_type, int timer_seconds_enabled, int timer_dots_type, int notification_enabled, int navigation_type, int snap_enabled) {
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

    public Settings(Long id) {
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
        this.navigation_type = 2;
        this.snap_enabled = 1;
    }

    public Long getId() {
        return id;
    }

    public int getAudio_balance() {
        return audio_balance;
    }

    public int getGain_record() {
        return gain_record;
    }

    public int getGain_broadcast() {
        return gain_broadcast;
    }

    public int getAgc_enabled() {
        return agc_enabled;
    }

    public int getCrossfade_enabled() {
        return crossfade_enabled;
    }

    public int getCrossfade_time() {
        return crossfade_time;
    }

    public int getNetwork_type() {
        return network_type;
    }

    public int getTimer_seconds_enabled() {
        return timer_seconds_enabled;
    }

    public int getTimer_dots_type() {
        return timer_dots_type;
    }

    public int getNotification_enabled() {
        return notification_enabled;
    }

    public int getNavigation_type() {
        return navigation_type;
    }

    public int getSnap_enabled() {
        return snap_enabled;
    }
}
