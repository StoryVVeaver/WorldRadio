package by.roman.worldradio0.business_logic.data.models.settings.child;

import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;

public class TextButtonItem extends SettingsItem {
    private final String text1Key;
    private final String text2Key;
    private final String text1;
    private final String text2;

    public TextButtonItem(String text1Key, String title, String text1) {
        super(title);
        this.text1Key = text1Key;
        this.text2Key = "";
        this.text1 = text1;
        this.text2 = "";
    }
    public TextButtonItem(String text1Key, String text2Key,String title, String text1, String text2) {
        super(title);
        this.text1Key = text1Key;
        this.text2Key = text2Key;
        this.text1 = text1;
        this.text2 = text2;
    }

    public String getText1() {
        return text1;
    }
    public String getText2() {
        return text2;
    }

    @Override
    public int getType() {
        return TYPE_TEXT_BUTTON_CHILD;
    }
    public String getText1Key() {
        return text1Key;
    }
    public String getText2Key() {
        return text2Key;
    }
}
