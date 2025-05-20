package by.roman.worldradio0.business_logic.settings;

import androidx.annotation.NonNull;

public interface SettingsChangeListener {
    void onSliderChanged(@NonNull String key, int value);
    void onToggleChanged(@NonNull String key, boolean isChecked);
    void onSwitchChanged(@NonNull String key, int pos);
    void onClickChanged(@NonNull String key);
}
