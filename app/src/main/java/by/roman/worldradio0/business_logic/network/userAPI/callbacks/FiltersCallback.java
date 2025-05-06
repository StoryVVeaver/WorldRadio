package by.roman.worldradio0.business_logic.network.userAPI.callbacks;


import by.roman.worldradio0.business_logic.data.dto.FilterDTO;

public interface FiltersCallback {
    void onSuccess(FilterDTO dto);
    void onFailure(Throwable t);
}
