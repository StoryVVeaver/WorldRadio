package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.business_logic.data.database.FavoriteDao;
import by.roman.worldradio0.business_logic.data.database.FilterDao;
import by.roman.worldradio0.business_logic.data.database.RadioStationDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;

public class RadioRepositoryImpl implements RadioRepository {
    private final RadioStationDao radioStationDao;
    private final FavoriteDao favoriteDao;
    private final UserDao userDao;
    private final FilterDao filterDao;
    private final MutableLiveData<Boolean> showPlayer = new MutableLiveData<>();
    public RadioRepositoryImpl(RadioStationDao radioStationDao, FavoriteDao favoriteDao, UserDao userDao, FilterDao filterDao) {
        this.radioStationDao = radioStationDao;
        this.favoriteDao = favoriteDao;
        this.userDao = userDao;
        this.filterDao = filterDao;
    }
    @Override
    public LiveData<Boolean> getShowPlayer() {
        return showPlayer;
    }
    @Override
    public void setStatePlayer(boolean state) {
        showPlayer.postValue(state);
    }
    @Override
    public List<RadioStation> getFavoriteStations(int currentPage, int pageSize){
        try {
            List<String> favoriteUUID = favoriteDao.getFavoritesByUser(userDao.getIdUserInSystem(), currentPage, pageSize);
            List<RadioStation> stations = new ArrayList<>();
            for(String i : favoriteUUID){
                stations.add(radioStationDao.getStationById(i));
            }
            return stations;
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed load favorite stations");
            return null;
        }
    }
    @Override
    public List<RadioStation> getFilteredStations(int currentPage, int pageSize){
        try {
            Filter filter = filterDao.getFilters(userDao.getIdUserInSystem());
            return radioStationDao.getFilteredStations(filter, currentPage, pageSize);
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed load filtered stations");
            return null;
        }
    }
    @Override
    public List<RadioStation> getAllStations(int currentPage, int pageSize){
        try {
            return radioStationDao.getAllStations(currentPage, pageSize);
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed load all stations");
            return null;
        }
    }
    @Override
    public void addRadioStation(RadioStationDTO radioStationDTO){
        try {
            radioStationDao.addRadioStation(radioStationDTO);
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed add " + radioStationDTO.getName());
        }
    }
    @Override
    public RadioStation getStationById(String uuid){
        try {
            return radioStationDao.getStationById(uuid);
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed load station by ID");
            return null;
        }
    }
    @Override
    public RadioStation getPlayingStation(){
        try {
            return radioStationDao.getStationById(userDao.getColumnPlayingUUID(userDao.getIdUserInSystem()));
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed load playing station");
            return null;
        }
    }
    @Override
    public List<String> getContrives(){
        try {
            return radioStationDao.getCountryList();
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed load country list");
            return null;
        }
    }
    @Override
    public List<String> getLanguage(){
        try {
            return radioStationDao.getLanguageList();
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed load language list");
            return null;
        }
    }
    @Override
    public List<String> getTags(){
        try {
            return radioStationDao.getTagsList();
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed load tags list");
            return null;
        }
    }
    @Override
    public List<String> getNames(){
        try {
            return radioStationDao.getNamesList();
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed load names list");
            return null;
        }
    }
    @Override
    public List<String> getCodecs(){
        try {
            return radioStationDao.getCodecsList();
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed load codecs list");
            return null;
        }
    }
    @Override
    public void clearTable(){
        try {
            radioStationDao.clearTable();
        } catch (Exception e) {
            Log.e("RadioRepositoryImpl","Failed clear table: " + e.getMessage());
        }
    }
    @Override
    public boolean hasRecords(){
        try {
            return radioStationDao.hasRecords();
        } catch (Exception e) {
            Log.e("RadioRepositoryImpl","Failed check is empty: " + e.getMessage());
            return false;
        }
    }
    @Override
    public int getCountFilteredStations(){
        try {
            Filter filter = filterDao.getFilters(userDao.getIdUserInSystem());
            return radioStationDao.getCountFilteredStations(filter);
        } catch (Exception e) {
            Log.e("RadioRepositoryImpl","Failed get count filtered stations: " + e.getMessage());
            return -1;
        }
    }
}
