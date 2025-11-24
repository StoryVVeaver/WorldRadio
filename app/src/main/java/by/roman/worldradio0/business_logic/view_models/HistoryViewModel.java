package by.roman.worldradio0.business_logic.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.models.History;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.HistoryRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HistoryViewModel extends ViewModel {
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final RadioRepository radioRepository;
    private final AtomicBoolean isActive = new AtomicBoolean(true);
    private int currentPage = 0;
    private boolean isLastPage = false;
    private final int pageSize = 20;
    private List<RadioStation> allStations = new ArrayList<>();
    private final MutableLiveData<UiState<List<RadioStation>>> historyList = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    @Inject
    public HistoryViewModel(HistoryRepository historyRepository, RadioRepository radioRepository, UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.radioRepository = radioRepository;
    }
    public LiveData<UiState<List<RadioStation>>> getHistoryList(){
        return historyList;
    }

    public void cancelPendingOperations() {
        isActive.set(false);
    }
    public boolean getIsLastPage() {
        return isLastPage;
    }
    public void resetState() {
        isActive.set(true);
        currentPage = 0;
        isLastPage = false;
        allStations.clear();
    }
    public void deleteAllHistory(){
        try {
            historyRepository.removeFromHistoryById(userRepository.getUserInSystem());
        } catch (Exception e){
            Log.e("SettingsViewModel", "Delete all history crashed");
        }
    }
    public void loadStart() {
        if (!isActive.get()) return;

        historyList.setValue(UiState.loading());
        executor.execute(() -> {
            if (!isActive.get()) return;

            try {
                List<RadioStation> list = new ArrayList<>();
                List <History> history_list = historyRepository.getHistoryList(0, pageSize);
                for(History i : history_list){
                    list.add(radioRepository.getStationById(i.getUuid()));
                }
                if (!isActive.get()) return;

                if (list.isEmpty()) {
                    historyList.postValue(UiState.error("Лист пуст"));
                } else {
                    allStations = new ArrayList<>(list);
                    historyList.postValue(UiState.success(allStations));
                    currentPage = 1;
                    isLastPage = list.size() < pageSize;
                }
            } catch (Exception e) {
                if (isActive.get()) {
                    historyList.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
                }
            }
        });
    }
    public int getPageSize() {
        return pageSize;
    }
    public void loadNextPage() {
        if (!isActive.get() || isLastPage) return;

        executor.execute(() -> {
            if (!isActive.get()) return;

            try {
                List<RadioStation> list = new ArrayList<>();
                List <History> history_list = historyRepository.getHistoryList(currentPage, pageSize);
                for(History i : history_list){
                    list.add(radioRepository.getStationById(i.getUuid()));
                }
                if (!isActive.get()) return;

                if (list.isEmpty()) {
                    isLastPage = true;
                    historyList.postValue(UiState.success(allStations));
                } else {
                    List<RadioStation> newList = new ArrayList<>(allStations);
                    newList.addAll(list);
                    allStations = newList;

                    historyList.postValue(UiState.success(allStations));
                    currentPage++;
                    isLastPage = list.size() < pageSize;
                }
            } catch (Exception e) {
                if (isActive.get()) {
                    historyList.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
                }
            }
        });
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
        isActive.set(false);
    }
    public History getLastHistory(){
        try {
            return historyRepository.getLastHistory();
        } catch (Exception e) {
            Log.e("HistoryViewModel", "Failed get last history");
            return null;
        }
    }
}
