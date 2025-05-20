package by.roman.worldradio0.business_logic.data.models.settings;

import by.roman.worldradio0.business_logic.settings.ListItem;

public abstract class SettingsItem implements ListItem {
    private String title;

    public SettingsItem(String title) {
        this.title = title;
    }

    public String getTitle() { return title; }

    // Удаляем getType() — он будет реализован в наследниках
}

