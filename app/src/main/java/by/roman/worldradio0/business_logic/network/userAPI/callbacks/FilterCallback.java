package by.roman.worldradio0.business_logic.network.userAPI.callbacks;

import java.util.List;

public interface FilterCallback {
    void onSuccess(List<String> t);
    void onFailure(Throwable t);
}
