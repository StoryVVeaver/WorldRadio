package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import androidx.lifecycle.LiveData;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.models.RadioStation;

public interface RadioRepository {
    LiveData<Boolean> getShowPlayer();
    void setStatePlayer(boolean state);
    List<RadioStation> getFavoriteStations(int currentPage, int pageSize);
    List<RadioStation> getFilteredStations(int currentPage, int pageSize);
    List<RadioStation> getAllStations(int currentPage, int pageSize);
    RadioStation getStationById(String uuid);
    RadioStation getPlayingStation();
    List<String> getContrives();
    List<String> getLanguage();
    List<String> getTags();
    List<String> getNames();
    List<String> getCodecs();
    void addRadioStation(RadioStationDTO radioStationDTO);
    int getCountFilteredStations();
}
