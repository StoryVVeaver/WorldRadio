package by.roman.worldradio0.business_logic.data.repositories;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.models.RadioStation;

public interface RadioRepository {
    List<RadioStation> getFavoriteStations(int currentPage, int pageSize);
    List<RadioStation> getFilteredStations(int currentPage, int pageSize);
    List<RadioStation> getAllStations(int currentPage, int pageSize);
    RadioStation getStationById(String uuid);
    RadioStation getPlayingStation();
    List<String> getContrives();
    List<String> getLanguage();
    List<String> getTags();
    void addRadioStation(RadioStationDTO radioStationDTO);
    int getCountFilteredStations();
}
