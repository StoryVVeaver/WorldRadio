package by.roman.worldradio0.business_logic.data.repositories;

import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;

public interface FilterRepository {
    Filter getFilters();
    void setFilters(FilterDTO filterDTO);
    void addFilters(FilterDTO filterDTO);
    void removeFilters();
}
