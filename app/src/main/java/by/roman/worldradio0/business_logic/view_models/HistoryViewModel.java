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
import by.roman.worldradio0.business_logic.network.radio.DataFromRadio;
import by.roman.worldradio0.business_logic.network.radio.callbacks.RadioCallback;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HistoryViewModel extends ViewModel {
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final RadioRepository radioRepository;
    private final DataFromRadio dataFromRadio;

    private final AtomicBoolean isActive = new AtomicBoolean(true);
    private int currentPage = 0;
    private boolean isLastPage = false;
    private final int pageSize = 100;

    private List<RadioStation> allStations = new ArrayList<>();
    private final MutableLiveData<UiState<List<RadioStation>>> historyList = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Inject
    public HistoryViewModel(HistoryRepository historyRepository,
                            RadioRepository radioRepository,
                            UserRepository userRepository,
                            DataFromRadio dataFromRadio) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
        this.radioRepository = radioRepository;
        this.dataFromRadio = dataFromRadio;
    }

    public LiveData<UiState<List<RadioStation>>> getHistoryList() {
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

    public void deleteAllHistory() {
        executor.execute(() -> {
            try {
                historyRepository.removeFromHistoryById(userRepository.getUserInSystem());
            } catch (Exception e) {
                Log.e("HistoryViewModel", "Delete all history crashed", e);
            }
        });
    }

    public void deleteOneFromHistory(String uuid) {
        executor.execute(() -> {
            try {
                historyRepository.removeFromHistory(uuid);
            } catch (Exception e) {
                Log.e("HistoryViewModel", "Delete one history crashed", e);
            }
        });
    }

    public void loadStart() {
        if (!isActive.get()) return;
        resetState();
        historyList.setValue(UiState.loading());
        loadData(0);
    }

    public void loadNextPage() {
        if (!isActive.get() || isLastPage) return;
        loadData(currentPage);
    }

    private void loadData(int page) {
        executor.execute(() -> {
            if (!isActive.get()) return;

            try {
                List<History> historyEntries = historyRepository.getHistoryList(page * pageSize, pageSize);

                if (historyEntries.isEmpty()) {
                    if (page == 0) {
                        historyList.postValue(UiState.error("Лист пуст"));
                    } else {
                        isLastPage = true;
                        historyList.postValue(UiState.success(new ArrayList<>(allStations)));
                    }
                    return;
                }

                List<String> uuids = new ArrayList<>();
                for (History h : historyEntries) {
                    uuids.add(h.getUuid());
                }

                dataFromRadio.getStationsByUUID(new RadioCallback<>() {
                    @Override
                    public void onSuccess(List<RadioStation> fetchedStations) {
                        if (!isActive.get()) return;

                        List<RadioStation> sortedPage = sortStationsByOrder(fetchedStations, uuids);

                        if (page == 0) allStations.clear();
                        allStations.addAll(sortedPage);

                        currentPage++;
                        isLastPage = historyEntries.size() < pageSize;
                        historyList.postValue(UiState.success(new ArrayList<>(allStations)));
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        if (isActive.get()) {
                            historyList.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
                        }
                    }

                    @Override
                    public void onLoading() {

                    }
                }, uuids);

            } catch (Exception e) {
                if (isActive.get()) {
                    historyList.postValue(UiState.error("Ошибка БД: " + e.getMessage()));
                }
            }
        });
    }

    private List<RadioStation> sortStationsByOrder(List<RadioStation> stations, List<String> order) {
        List<RadioStation> sorted = new ArrayList<>();
        for (String uuid : order) {
            for (RadioStation station : stations) {
                if (station.getStationUuid().equals(uuid)) {
                    sorted.add(station);
                    break;
                }
            }
        }
        return sorted;
    }

    public int getPageSize() {
        return pageSize;
    }

    public History getLastHistory() {
        try {
            return historyRepository.getLastHistory();
        } catch (Exception e) {
            Log.e("HistoryViewModel", "Failed get last history", e);
            return null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        isActive.set(false);
        executor.shutdown();
    }
}