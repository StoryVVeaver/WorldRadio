package by.roman.worldradio0.business_logic.data.models;

import java.util.List;

import by.roman.worldradio0.business_logic.settings.ListItem;

public class SettingsGroup implements ListItem {
    private String title;
    private boolean expanded = false;
    private List<SettingsItem> children;

    public SettingsGroup(String title, List<SettingsItem> children) {
        this.title = title;
        this.children = children;
    }

    public String getTitle() { return title; }
    public List<SettingsItem> getChildren() { return children; }
    public boolean isExpanded() { return expanded; }
    public void setExpanded(boolean expanded) { this.expanded = expanded; }

    @Override
    public int getType() {
        return TYPE_GROUP;
    }
}
