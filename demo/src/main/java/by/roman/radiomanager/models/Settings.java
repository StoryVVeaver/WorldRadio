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
    private int radio_module_enabled;

    private int timer_seconds_enabled;
    private int timer_dots_type;

    public Settings(){
        
    }

    public Settings(Long id, int audio_balance, int gain_record, int gain_broadcast, int agc_enabled, int crossfade_enabled, int crossfade_time, int network_type, int radio_module_enabled, int timer_seconds_enabled, int timer_dots_type) {
        this.id = id;
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

    public int getRadio_module_enabled() {
        return radio_module_enabled;
    }

    public int getTimer_seconds_enabled() {
        return timer_seconds_enabled;
    }

    public int getTimer_dots_type() {
        return timer_dots_type;
    }
}
