package by.roman.worldradio0.business_logic.data.models.settings.child;

import java.util.List;

import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;

public class SwitchItem extends SettingsItem {
    private final String key;
    private final List<String> types;
    private int pos;
    public SwitchItem(String key, String title, List<String> types, int pos){
        super(title);
        this.key = key;
        this.types = types;
        this.pos = pos;
    }

    public String getKey() {
        return key;
    }
    public List<String> getTypes() {
        return types;
    }
    public int getPos() {
        return pos;
    }
    @Override
    public int getType() {
        return TYPE_SWITCH_CHILD;
    }
}
