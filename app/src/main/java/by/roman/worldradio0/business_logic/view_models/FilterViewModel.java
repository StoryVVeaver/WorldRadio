package by.roman.worldradio0.business_logic.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.LocationUtil;
import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.dto.FilterDTO;
import by.roman.worldradio0.business_logic.data.models.Filter;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FilterRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import by.roman.worldradio0.business_logic.network.radio.CountryModel;
import by.roman.worldradio0.business_logic.network.radio.DataFromRadio;
import by.roman.worldradio0.business_logic.network.radio.LangModel;
import by.roman.worldradio0.business_logic.network.radio.callbacks.RadioCallback;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FilterViewModel extends ViewModel {
    private final RadioRepository radioRepository;
    private final FilterRepository filterRepository;
    private final DataFromRadio dataFromRadio;
    private final MutableLiveData<UiState<List<RadioStation>>> stations = new MutableLiveData<>();
    private final MutableLiveData<List<String>> countriesLive = new MutableLiveData<>();
    private final MutableLiveData<List<String>> languagesLive = new MutableLiveData<>();
    private final MutableLiveData<List<String>> tagsLive = new MutableLiveData<>();
    private final MutableLiveData<List<String>> namesLive = new MutableLiveData<>();
    private final MutableLiveData<List<String>> codecsLive = new MutableLiveData<>();
    private final MutableLiveData<UiState<Integer>> count = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final AtomicBoolean isActive = new AtomicBoolean(true);
    private int currentOffset = 0;
    private boolean isLastPage = false;
    private final int pageSize = 100;
    private List<RadioStation> allStations = new ArrayList<>();

    public boolean getIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public void setOffset(int offset) {
        this.currentOffset = offset;
    }

    public int getPageSize() {
        return pageSize;
    }

    @Inject
    public FilterViewModel(RadioRepository radioRepository, FilterRepository filterRepository, DataFromRadio dataFromRadio) {
        this.radioRepository = radioRepository;
        this.filterRepository = filterRepository;
        this.dataFromRadio = dataFromRadio;
    }

    public LiveData<UiState<List<RadioStation>>> getFilteredStations() {
        return stations;
    }

    public LiveData<UiState<Integer>> getCountFilteredStations() {
        return count;
    }
    public void loadStart() {
        if (!isActive.get()) return;
        resetState();
        stations.setValue(UiState.loading());
        loadData(0);
    }

    public void loadNextPage() {
        if (!isActive.get() || isLastPage || stations.getValue() != null && stations.getValue().status == UiState.Status.LOADING) return;
        loadData(currentOffset);
    }

    private void loadData(int offset) {
        if (!isActive.get()) return;
        stations.postValue(UiState.loading());
        Filter filter = filterRepository.getFilters();

        dataFromRadio.getStations(new RadioCallback<>() {
            @Override
            public void onSuccess(List<RadioStation> list) {
                if (!isActive.get()) return;

                if (list.isEmpty()) {
                    if (offset == 0) {
                        stations.postValue(UiState.error("Лист пуст"));
                    } else {
                        isLastPage = true;
                        stations.postValue(UiState.success(allStations));
                    }
                } else {
                    Set<String> existingUuids = allStations.stream()
                            .map(RadioStation::getStationUuid)
                            .collect(Collectors.toSet());

                    List<RadioStation> uniqueNew = list.stream()
                            .filter(rs -> !existingUuids.contains(rs.getStationUuid()))
                            .collect(Collectors.toList());

                    if (offset == 0) allStations.clear();
                    allStations.addAll(uniqueNew);
                    stations.postValue(UiState.success(new ArrayList<>(allStations)));

                    currentOffset += pageSize;
                    isLastPage = list.size() < pageSize;
                }
                count.postValue(UiState.success(allStations.size()));
            }

            @Override
            public void onFailure(Throwable t) {
                if (isActive.get()) {
                    stations.postValue(UiState.error("Ошибка сети: " + t.getMessage()));
                }
            }

            @Override
            public void onLoading() {
            }
        }, filter, offset, pageSize);
    }

    public void resetState() {
        isActive.set(true);
        currentOffset = 0;
        isLastPage = false;
        allStations.clear();
    }
    public void loadAutocompleteData() {
        dataFromRadio.getCountries(new RadioCallback<>() {
            @Override
            public void onSuccess(List<CountryModel> c) {
                if (!isActive.get()) return;
                countriesLive.postValue(c != null ? LocationUtil.getCountryNamesFromIso(c) : new ArrayList<>());
            }

            @Override
            public void onFailure(Throwable t) {
            }

            @Override
            public void onLoading() {
            }
        });
        dataFromRadio.getLang(new RadioCallback<>() {
            @Override
            public void onSuccess(List<LangModel> list) {
                if (!isActive.get()) return;
                List<String> l = new ArrayList<>();
                for(LangModel i: list){
                    l.add(i.getName());
                }
                languagesLive.postValue(l);
            }

            @Override
            public void onFailure(Throwable t) {
            }

            @Override
            public void onLoading() {
            }
        });


//        executor.execute(() -> {
//            try {
//                List<String> t = radioRepository.getTags();
//                List<String> n = radioRepository.getNames();
//
//                if (!isActive.get()) return;
//
//                tagsLive.postValue(t != null ? t : new ArrayList<>());
//                namesLive.postValue(n != null ? n : new ArrayList<>());
//            } catch (Exception e) {
//                Log.e("FilterViewModel", e.getMessage() + " ");
//            }
//        });
    }
    public void setFilters(Filter filter) {
        filterRepository.setFilters(new FilterDTO().fromModel(filter));
        resetState();
        loadNextPage();
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
    @Override
    protected void onCleared() {
        super.onCleared();
        isActive.set(false);
        executor.shutdown();
    }
}