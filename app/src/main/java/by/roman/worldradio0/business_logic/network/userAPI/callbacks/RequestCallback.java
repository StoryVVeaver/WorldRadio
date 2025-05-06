package by.roman.worldradio0.business_logic.network.userAPI.callbacks;

import by.roman.worldradio0.business_logic.data.dto.UserDTO;

public interface RequestCallback {
    void onSuccess(UserDTO dto);
    void onFailure(Throwable t);
}
