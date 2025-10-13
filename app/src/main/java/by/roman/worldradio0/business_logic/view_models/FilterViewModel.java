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
import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FilterRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FilterViewModel extends ViewModel {
    private final RadioRepository radioRepository;
    private final FilterRepository filterRepository;
    private final MutableLiveData<UiState<List<RadioStation>>> stations = new MutableLiveData<>();
    private final MutableLiveData<List<String>> countriesLive = new MutableLiveData<>();
    private final MutableLiveData<List<String>> languagesLive = new MutableLiveData<>();
    private final MutableLiveData<List<String>> tagsLive = new MutableLiveData<>();
    private final MutableLiveData<List<String>> namesLive = new MutableLiveData<>();
    private final MutableLiveData<List<String>> codecsLive = new MutableLiveData<>();
    private final MutableLiveData<UiState<Integer>> count = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final AtomicBoolean isActive = new AtomicBoolean(true);
    private int currentPage = 0;
    private boolean isLastPage = false;
    private final int pageSize = 20;
    private List<RadioStation> allStations = new ArrayList<>();

    public boolean getIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public void setPage(int page) {
        this.currentPage = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    @Inject
    public FilterViewModel(RadioRepository radioRepository, FilterRepository filterRepository) {
        this.radioRepository = radioRepository;
        this.filterRepository = filterRepository;
    }

    public LiveData<UiState<List<RadioStation>>> getFilteredStations() {
        return stations;
    }

    public LiveData<UiState<Integer>> getCountFilteredStations() {
        return count;
    }

    public void loadCount() {
        if (!isActive.get()) return;

        count.setValue(UiState.loading());
        executor.execute(() -> {
            if (!isActive.get()) return;

            try {
                int cnt = radioRepository.getCountFilteredStations();
                if (isActive.get()) {
                    count.postValue(UiState.success(cnt));
                }
            } catch (Exception e) {
                if (isActive.get()) {
                    count.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
                }
            }
        });
    }
    public void loadStart() {
        if (!isActive.get()) return;

        stations.setValue(UiState.loading());
        executor.execute(() -> {
            if (!isActive.get()) return;

            try {
                List<RadioStation> list = radioRepository.getFilteredStations(0, pageSize);
                if (!isActive.get()) return;

                if (list.isEmpty()) {
                    stations.postValue(UiState.error("Лист пуст"));
                } else {
                    allStations = new ArrayList<>(list);
                    stations.postValue(UiState.success(allStations));
                    currentPage = 1;
                    isLastPage = list.size() < pageSize;
                }
            } catch (Exception e) {
                if (isActive.get()) {
                    stations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
                }
            }
        });
    }
    public void loadNextPage() {
        if (!isActive.get() || isLastPage) return;

        executor.execute(() -> {
            if (!isActive.get()) return;

            try {
                List<RadioStation> list = radioRepository.getFilteredStations(currentPage, pageSize);
                if (!isActive.get()) return;

                if (list.isEmpty()) {
                    isLastPage = true;
                    stations.postValue(UiState.success(allStations));
                } else {
                    List<RadioStation> newList = new ArrayList<>(allStations);
                    newList.addAll(list);
                    allStations = newList;

                    stations.postValue(UiState.success(allStations));
                    currentPage++;
                    isLastPage = list.size() < pageSize;
                }
            } catch (Exception e) {
                if (isActive.get()) {
                    stations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
                }
            }
        });
    }
    public void cancelPendingOperations() {
        isActive.set(false);
    }
    public void resetState() {
        isActive.set(true);
        currentPage = 0;
        isLastPage = false;
        allStations.clear();
    }
    public void loadAutocompleteData() {
        executor.execute(() -> {
            try {
                List<String> c = radioRepository.getContrives();
                List<String> l = radioRepository.getLanguage();
                List<String> t = radioRepository.getTags();
                List<String> n = radioRepository.getNames();
                List<String> co = radioRepository.getCodecs();

                if (!isActive.get()) return;

                countriesLive.postValue(c != null ? c : new ArrayList<>());
                languagesLive.postValue(l != null ? l : new ArrayList<>());
                tagsLive.postValue(t != null ? t : new ArrayList<>());
                namesLive.postValue(n != null ? n : new ArrayList<>());
                codecsLive.postValue(co != null ? co : new ArrayList<>());
            } catch (Exception e) {
            }
        });
    }
    public void setFilters(Filter filter) {
        Log.e("fdsgdmodel", "set");
        filterRepository.setFilters(new FilterDTO().fromModel(filter));
        Log.e("fdsgdmodel", "set");
    }
    public Filter getFilters() {
        return filterRepository.getFilters();
    }
    public LiveData<List<String>> getCountriesLive() {
        return countriesLive;
    }
    public LiveData<List<String>> getLanguagesLive() {
        return languagesLive;
    }
    public LiveData<List<String>> getTagsLive() {
        return tagsLive;
    }
    public LiveData<List<String>> getNamesLive() {
        return namesLive;
    }
    public LiveData<List<String>> getCodecsLive() {
        return codecsLive;
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        isActive.set(false);
        executor.shutdown();
    }
}