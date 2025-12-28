package by.roman.worldradio0.business_logic.settings;

import static by.roman.worldradio0.business_logic.settings.SettingsKeys.AGC_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.AUDIO_BALANCE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.CROSSFADE_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.CROSSFADE_TIME;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.DELETE_ACCOUNT;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.GET_USER_DATA;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.NAVIGATION_TYPE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.NETWORK_TYPE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.PUT_USER_DATA;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.TIMER_DOTS_TYPE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.TIMER_SECONDS_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.UPDATE_STATIONS_DATA;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.R;
import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsGroup;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckWIthSliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.SliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.SwitchItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.TextButtonItem;

public class SettingsList {
    public static List<SettingsGroup> getSettingsList(Settings settings, Context context){

        List<SettingsGroup> groups = new ArrayList<>();
        try {
            List<SettingsItem> audioItems = new ArrayList<>();
            audioItems.add(new SliderItem(AUDIO_BALANCE,context.getResources().getString(R.string.balance) + ":",-10, 10,settings.getAudioBalance(),"L","R",true));
            audioItems.add(new CheckItem(AGC_ENABLED, context.getResources().getString(R.string.agc) + ":",settings.getAgcEnabled() == 1));
            audioItems.add(new CheckWIthSliderItem(CROSSFADE_ENABLED,CROSSFADE_TIME, context.getResources().getString(R.string.crossfade) + ": ",0, 20,settings.getCrossfadeTime(),settings.getCrossfadeEnabled() == 1));
            //groups.add(new SettingsGroup(context.getResources().getString(R.string.audio), audioItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list audio settings");
        }

        try {
            List<SettingsItem> networkItems = new ArrayList<>();
            List<String> network_types = new ArrayList<>();network_types.add(context.getResources().getString(R.string.wifi));network_types.add(context.getResources().getString(R.string.mobile));network_types.add(context.getResources().getString(R.string.any));
            networkItems.add(new SwitchItem(NETWORK_TYPE,  context.getResources().getString(R.string.connection) + ":", network_types,settings.getNetworkType()));
            groups.add(new SettingsGroup(context.getResources().getString(R.string.network_sett), networkItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list network settings");
        }

        try {
            List<SettingsItem> viewItems = new ArrayList<>();
            viewItems.add(new CheckItem(TIMER_SECONDS_ENABLED,context.getResources().getString(R.string.timer_seconds) + ":",settings.getTimerSecondsEnabled() == 1));
            List<String> dots_types = new ArrayList<>();dots_types.add(context.getResources().getString(R.string.circle));dots_types.add(context.getResources().getString(R.string.rhombus));
            viewItems.add(new SwitchItem(TIMER_DOTS_TYPE,context.getResources().getString(R.string.divider) + ":",dots_types,settings.getTimerDotsType()));
            List<String> nav_types = new ArrayList<>();nav_types.add(context.getResources().getString(R.string.swipe));nav_types.add(context.getResources().getString(R.string.buttons));nav_types.add(context.getResources().getString(R.string.swipe) + " " + context.getResources().getString(R.string.and) + " " + context.getResources().getString(R.string.buttons));
            viewItems.add(new SwitchItem(NAVIGATION_TYPE, context.getResources().getString(R.string.nav_type) + ":",nav_types,settings.getNavigationType()));
            groups.add(new SettingsGroup(context.getResources().getString(R.string.appearance), viewItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list view settings");
        }

        try {
            List<SettingsItem> dataItems = new ArrayList<>();
            dataItems.add(new TextButtonItem(GET_USER_DATA, PUT_USER_DATA,"Данные:", "[Загрузить]", "[Выгрузить]"));
            dataItems.add(new TextButtonItem(UPDATE_STATIONS_DATA,"Станции:", "[Обновить]"));
            dataItems.add(new TextButtonItem(DELETE_ACCOUNT,"Удалить аккаунт", "                "));
            groups.add(new SettingsGroup("Данные и аккаунт", dataItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list data settings");
        }

        return groups;
    }
}
