package by.roman.worldradio0.business_logic;

import static by.roman.worldradio0.business_logic.settings.SettingsKeys.AGC_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.AUDIO_BALANCE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.CROSSFADE_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.CROSSFADE_TIME;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.DELETE_ACCOUNT;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.EXIT_FROM_ACCOUNT;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.GAIN_BROADCAST;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.GAIN_RECORD;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.GET_USER_DATA;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.NAVIGATION_TYPE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.NETWORK_TYPE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.PUT_USER_DATA;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.TIMER_DOTS_TYPE;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.TIMER_SECONDS_ENABLED;
import static by.roman.worldradio0.business_logic.settings.SettingsKeys.UPDATE_STATIONS_DATA;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.business_logic.data.models.Settings;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsGroup;
import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.CheckWIthSliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.SliderItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.SwitchItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.TextButtonItem;
import by.roman.worldradio0.business_logic.data.models.settings.child.TextItem;

public class SettingsList {
    public static List<SettingsGroup> getSettingsList(Settings settings){

        List<SettingsGroup> groups = new ArrayList<>();

        try {
            List<SettingsItem> audioItems = new ArrayList<>();
            audioItems.add(new SliderItem(AUDIO_BALANCE,"Баланс:",-10, 10,settings.getAudioBalance(),"L","R",true));
            audioItems.add(new TextItem("Усиление"));
            audioItems.add(new SliderItem(GAIN_RECORD,"     Запись:", 0, 100, settings.getGainRecord(),true));
            audioItems.add(new SliderItem(GAIN_BROADCAST,"     Эфир:", 0, 100, settings.getGainBroadcast(),true));
            audioItems.add(new CheckItem(AGC_ENABLED,"AGC: ",settings.getAgcEnabled() == 1));
            audioItems.add(new CheckWIthSliderItem(CROSSFADE_ENABLED,CROSSFADE_TIME,"Crossfade: ",0, 20,settings.getCrossfadeTime(),settings.getCrossfadeEnabled() == 1));
            groups.add(new SettingsGroup("Аудио", audioItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list audio settings");
        }

        try {
            List<SettingsItem> networkItems = new ArrayList<>();
            List<String> network_types = new ArrayList<>();network_types.add("Только Wi-fi");network_types.add("Только мобильная сеть");network_types.add("Любое");
            networkItems.add(new SwitchItem(NETWORK_TYPE, "Подключение:", network_types,settings.getNetworkType()));
            groups.add(new SettingsGroup("Сетевые параметры", networkItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list network settings");
        }

        try {
            List<SettingsItem> viewItems = new ArrayList<>();
            viewItems.add(new CheckItem(TIMER_SECONDS_ENABLED,"Использовать секунды:",settings.getTimerSecondsEnabled() == 1));
            List<String> dots_types = new ArrayList<>();dots_types.add("Круг");dots_types.add("Ромб");
            viewItems.add(new SwitchItem(TIMER_DOTS_TYPE,"Вид разделителя:",dots_types,settings.getTimerDotsType()));
            //viewItems.add(new CheckItem(NOTIFICATION_ENABLED,"Показывать уведомление с плеером",settings.getNotificationEnabled() == 1));
            List<String> nav_types = new ArrayList<>();nav_types.add("Свайп");nav_types.add("Кнопка");nav_types.add("Свайп и кнопка");
            viewItems.add(new SwitchItem(NAVIGATION_TYPE,"Вид навигации:",nav_types,settings.getNavigationType()));
            groups.add(new SettingsGroup("Оформление", viewItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list view settings");
        }

        try {
            List<SettingsItem> dataItems = new ArrayList<>();
            dataItems.add(new TextButtonItem(GET_USER_DATA, PUT_USER_DATA,"Синхронизация:", "[Загрузить]", "[Выгрузить]"));
            dataItems.add(new TextButtonItem(UPDATE_STATIONS_DATA,"Станции:", "[Обновить]"));
            dataItems.add(new TextButtonItem(EXIT_FROM_ACCOUNT,"Выйти из аккаунта", "                "));
            dataItems.add(new TextButtonItem(DELETE_ACCOUNT,"Удалить аккаунт", "                "));
            //dataItems.add(new TextButtonItem("9","История:", "[Очистить]")); //TODO
            groups.add(new SettingsGroup("Данные и аккаунт", dataItems));
        } catch (Exception e) {
            Log.e("SettingsViewModel", "Error creating list data settings");
        }

        return groups;
    }
}
