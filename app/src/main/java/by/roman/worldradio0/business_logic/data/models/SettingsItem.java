package by.roman.worldradio0.business_logic.data.models;

import by.roman.worldradio0.business_logic.settings.ListItem;

public class SettingsItem implements ListItem {
    private String title;
    private boolean selected;

    public SettingsItem(String title, boolean selected) {
        this.title = title;
        this.selected = selected;
    }

    public String getTitle() { return title; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    @Override
    public int getType() {
        return TYPE_CHILD;
    }
}
