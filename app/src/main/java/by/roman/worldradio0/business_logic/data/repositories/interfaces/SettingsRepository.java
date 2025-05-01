package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import by.roman.worldradio0.business_logic.data.dto.SettingsDTO;
import by.roman.worldradio0.business_logic.data.models.Settings;

public interface SettingsRepository {
    Settings getSettings(int id);
    void setSettings(SettingsDTO settingsDTO);
    void removeSettings(int id);
    void addSettings(SettingsDTO settingsDTO);
}
