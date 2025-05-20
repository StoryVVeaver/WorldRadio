package by.roman.worldradio0.business_logic.data.models.settings.child;

import by.roman.worldradio0.business_logic.data.models.settings.SettingsItem;

public class CheckWIthSliderItem extends SettingsItem {
    private final String checkKey;
    private final String sliderKey;
    private final int min;
    private final int max;
    private int value;
    private boolean isChecked;

    public CheckWIthSliderItem(String checkKey, String sliderKey,String title, int min, int max, int value, boolean isChecked) {
        super(title);
        this.checkKey = checkKey;
        this.sliderKey = sliderKey;
        this.min = min;
        this.max = max;
        this.value = value;
        this.isChecked = isChecked;
    }

    public String getCheckKey(){
        return checkKey;
    }
    public String getSliderKey(){
        return sliderKey;
    }
    public int getMin() {
        return min;
    }
    public int getMax() {
        return max;
    }
    public int getValue() {
        return value;
    }
    public boolean isChecked() {
        return isChecked;
    }

    public void setValue(int value) {
        this.value = value;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public int getType() {
        return TYPE_CHECK_WITH_SLIDER_CHILD;
    }
}
