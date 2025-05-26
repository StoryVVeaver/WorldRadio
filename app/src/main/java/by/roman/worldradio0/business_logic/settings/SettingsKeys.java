package by.roman.worldradio0.business_logic.settings;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.AndroidEntryPoint;

@Singleton
public class SettingsKeys {
    public static final String AUDIO_BALANCE = "audio_balance"; //slider
    public static final String GAIN_RECORD = "gain_record"; //slider
    public static final String GAIN_BROADCAST = "gain_broadcast"; //slider
    public static final String AGC_ENABLED = "agc_enabled"; //toggle
    public static final String CROSSFADE_ENABLED= "crossfade_enabled"; //toggle
    public static final String CROSSFADE_TIME = "crossfade_time"; //slider

    public static final String NETWORK_TYPE = "network_type"; //switch
    public static final String RADIO_MODULE_ENABLED = "radio_module_enabled"; //toggle

    public static final String TIMER_SECONDS_ENABLED = "timer_seconds_enabled"; //toggle
    public static final String TIMER_DOTS_TYPE = "timer_dots_type"; //switch
    public static final String NOTIFICATION_ENABLED = "notification_enabled"; //check

    public static final String GET_USER_DATA = "get_user_data"; //button
    public static final String PUT_USER_DATA = "put_user_data"; //button
    public static final String UPDATE_STATIONS_DATA = "update_stations_data"; //button
    public static final String EXIT_FROM_ACCOUNT = "exit_from_account"; //button
    public static final String DELETE_ACCOUNT = "delete_account"; //button
    @Inject
    public SettingsKeys(){}
}
