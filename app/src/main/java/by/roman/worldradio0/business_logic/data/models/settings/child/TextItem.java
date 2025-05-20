package by.roman.worldradio0.business_logic.data.models.settings.child;

import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;

public class TextItem extends SettingsItem {

    public TextItem(String title) {
        super(title);
    }

    @Override
    public String getTitle() {
        return super.getTitle();
    }

    @Override
    public int getType() {
        return TYPE_TEXT_CHILD;
    }
}

