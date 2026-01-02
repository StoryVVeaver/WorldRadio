package by.roman.worldradio0.business_logic.network.radio.callbacks;

import by.roman.worldradio0.business_logic.network.radio.VoteModel;

public interface VoteCallback {
    void onSuccess(VoteModel t);
    void onFailure(Throwable t);
}
