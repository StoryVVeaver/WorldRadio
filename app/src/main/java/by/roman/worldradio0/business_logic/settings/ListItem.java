package by.roman.worldradio0.business_logic.settings;

public interface ListItem {
    int TYPE_GROUP = 0;
    int TYPE_TEXT_CHILD = 1;
    int TYPE_SLIDER_CHILD = 2;
    int TYPE_CHECK_CHILD = 3;
    int TYPE_CHECK_WITH_SLIDER_CHILD = 4;
    int TYPE_TEXT_BUTTON_CHILD = 5;
    int TYPE_SWITCH_CHILD = 6;

    int getType();
}

