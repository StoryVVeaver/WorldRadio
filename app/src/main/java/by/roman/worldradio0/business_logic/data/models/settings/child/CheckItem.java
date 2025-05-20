package by.roman.worldradio0.business_logic.data.models.settings.child;

import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;

public class CheckItem extends SettingsItem {
    private final String key;
    private boolean enabled;

    public CheckItem(String key, String title, boolean enabled) {
        super(title);
        this.key = key;
        this.enabled = enabled;
    }

    public String getKey(){
        return key;
    }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public int getType() {
        return TYPE_CHECK_CHILD;
    }
}
