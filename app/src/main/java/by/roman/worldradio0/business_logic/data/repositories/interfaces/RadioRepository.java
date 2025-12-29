package by.roman.worldradio0.business_logic.data.repositories.interfaces;

import androidx.lifecycle.LiveData;

import java.util.List;

import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.FavoriteStationRepositoryImpl;
import by.roman.worldradio0.business_logic.data.repositories.RadioRepositoryImpl;

public interface RadioRepository {
    LiveData<Boolean> getShowPlayer();
    void setStatePlayer(boolean state);
    List<RadioStation> getFavoriteStations(int currentPage, int pageSize);
    List<RadioStation> getFilteredStations(int currentPage, int pageSize);
    List<RadioStation> getAllStations(int currentPage, int pageSize);
    RadioStation getStationById(String uuid);
    RadioStation getStationByUrl(String url);
    void addListener(RadioRepositoryImpl.OnPlayingChangedListener listener);
    void removeListener(RadioRepositoryImpl.OnPlayingChangedListener listener);
    String getCurrentUUID();
    void setCurrentUUID(String uuid);
    List<String> getContriesCode();
    List<String> getLanguage();
    List<String> getTags();
    List<String> getNames();
    List<String> getCodecs();
    void clearTable();
    boolean hasRecords();
    void addRadioStation(RadioStationDTO radioStationDTO);
    int getCountFilteredStations();
}
