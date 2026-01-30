package by.roman.worldradio0.business_logic.data.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import by.roman.worldradio0.business_logic.data.database.FavoriteStationDao;
import by.roman.worldradio0.business_logic.data.database.FilterDao;
import by.roman.worldradio0.business_logic.data.database.RadioStationDao;
import by.roman.worldradio0.business_logic.data.database.UserDao;
import by.roman.worldradio0.business_logic.data.dto.RadioStationDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;

public class RadioRepositoryImpl implements RadioRepository {
    private final RadioStationDao radioStationDao;
    private final FavoriteStationDao favoriteStationDao;
    private final UserDao userDao;
    private final FilterDao filterDao;
    private final MutableLiveData<Boolean> showPlayer = new MutableLiveData<>();
    private String currentUUID = "";
    private final List<RadioRepositoryImpl.OnPlayingChangedListener> listeners = new ArrayList<>();
    public RadioRepositoryImpl(RadioStationDao radioStationDao, FavoriteStationDao favoriteStationDao, UserDao userDao, FilterDao filterDao) {
        this.radioStationDao = radioStationDao;
        this.favoriteStationDao = favoriteStationDao;
        this.userDao = userDao;
        this.filterDao = filterDao;
    }

    public interface OnPlayingChangedListener {
        void onPlayingChanged();
    }

    @Override
    public void addListener(RadioRepositoryImpl.OnPlayingChangedListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    private void notifyPlayerChanged() {
        for (RadioRepositoryImpl.OnPlayingChangedListener listener : new ArrayList<>(listeners)) {
            listener.onPlayingChanged();
        }
    }
    @Override
    public void removeListener(RadioRepositoryImpl.OnPlayingChangedListener listener) {
        listeners.remove(listener);
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
    public List<String> getFavoriteStations(){
        try {
            return favoriteStationDao.getFavoritesByUser(userDao.getIdUserInSystem());
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
    public RadioStation getStationByUrl(String url) {
        try {
            return radioStationDao.getStationByUrl(url);
        } catch (Exception e) {
            Log.e("RadioRepositoryImp","Failed get station by url");
            return null;
        }
    }

    @Override
    public String getCurrentUUID(){
        return currentUUID;
    }

    @Override
    public void setCurrentUUID(String currentUUID){
        if (currentUUID == null || currentUUID.isEmpty()) {
            this.currentUUID = "";
            return;
        }
        this.currentUUID = currentUUID;
        notifyPlayerChanged();
    }
    @Override
    public List<String> getContriesCode(){
        try {
            return radioStationDao.getCountryCodeList();
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
