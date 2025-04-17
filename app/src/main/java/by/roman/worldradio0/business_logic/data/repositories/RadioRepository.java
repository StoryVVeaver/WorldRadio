package by.roman.worldradio0.business_logic.data.repositories;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.models.RadioStation;

public interface RadioRepository {
    List<RadioStation> getFavoriteStations();
    List<RadioStation> getFilteredStations();
    List<RadioStation> getAllStations();
    RadioStation getStationById(String uuid);
    List<String> getContrives();
    List<String> getLanguage();
    List<String> getTags();
    void addRadioStation(RadioStationDTO radioStationDTO);
    int getCountFilteredStations();
}
