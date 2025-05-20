package by.roman.worldradio0.business_logic.data.models.settings.child;

import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;

public class SliderItem extends SettingsItem {
    private final String key;
    private final int min;
    private final int max;
    private int value;
    private final String leftLabel;
    private final String rightLabel;
    private final boolean isDouble;
    private final boolean isPercent;

    public SliderItem(String key, String title,int min, int max, int value, String leftLabel, String rightLabel, boolean isDouble) {
        super(title);
        this.key = key;
        this.value = value;
        this.min = min;
        this.max = max;
        this.leftLabel = leftLabel;
        this.rightLabel = rightLabel;
        this.isDouble = isDouble;
        this.isPercent = false;
    }

    public SliderItem(String key, String title,int min, int max, int value, boolean isPercent) {
        super(title);
        this.key = key;
        this.value = value;
        this.min = min;
        this.max = max;
        this.leftLabel = null;
        this.rightLabel = null;
        this.isDouble = false;
        this.isPercent = isPercent;
    }

    public String getKey(){
        return key;
    }
    public int getValue() { return value; }
    @Override
    public int getType() {
        return TYPE_SLIDER_CHILD;
    }
    public int getMin() {
        return min;
    }
    public int getMax() {
        return max;
    }
    public String getLeftLabel() {
        return leftLabel;
    }
    public String getRightLabel() {
        return rightLabel;
    }
    public boolean getIsDouble() {
        return isDouble;
    }
    public boolean getIsPercent() {
        return isPercent;
    }

    public void setValue(int value) { this.value = value; }
}

