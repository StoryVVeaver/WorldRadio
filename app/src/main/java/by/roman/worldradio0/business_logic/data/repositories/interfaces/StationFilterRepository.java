package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import java.util.List;

public interface StationFilterRepository {
    void clearTable();
    List<String> getAllFilters();
    void addFilter(String filter);
}
