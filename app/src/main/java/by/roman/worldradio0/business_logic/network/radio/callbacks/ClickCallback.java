package by.roman.worldradio0.business_logic.network.radio.callbacks;


import by.roman.worldradio0.business_logic.network.radio.ClickModel;

public interface ClickCallback {
    void onSuccess(ClickModel t);
    void onFailure(Throwable t);
}
