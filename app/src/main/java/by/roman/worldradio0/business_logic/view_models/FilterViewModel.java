package by.roman.worldradio0.business_logic.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.UiState;
import by.roman.worldradio0.business_logic.data.models.RadioStation;
import by.roman.worldradio0.business_logic.data.repositories.FilterRepository;
import by.roman.worldradio0.business_logic.data.repositories.RadioRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FilterViewModel extends ViewModel {
    private final RadioRepository radioRepository;
    private final FilterRepository filterRepository;
    private final MutableLiveData<UiState<List<RadioStation>>> stations = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private int currentPage = 0;
    private boolean isLastPage = false;
    private final int pageSize = 50;
    public boolean getIsLastPage() {
        return isLastPage;
    }
    @Inject
    public FilterViewModel(RadioRepository radioRepository, FilterRepository filterRepository){
        this.radioRepository = radioRepository;
        this.filterRepository = filterRepository;
        loadFiltered();
    }
    public LiveData<UiState<List<RadioStation>>> getFilteredStations() {
        return stations;
    }
    private void loadFiltered(){
        stations.setValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<RadioStation> list = radioRepository.getFilteredStations(currentPage,pageSize);
                list.isEmpty(); // вызов ошибки
                stations.postValue(UiState.success(list));
                currentPage++;
            } catch (Exception e) {
                stations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
    }
    public void loadNextPage() {
        if (isLastPage) return;
        executor.execute(() -> {
            try {
                List<RadioStation> list = radioRepository.getFilteredStations(currentPage, pageSize);
                if (list.isEmpty()) {
                    isLastPage = true;
                } else {
                    List<RadioStation> currentList = stations.getValue() != null && stations.getValue().data != null
                            ? new ArrayList<>(stations.getValue().data)
                            : new ArrayList<>();
                    currentList.addAll(list);
                    stations.postValue(UiState.success(currentList));
                    currentPage++;
                }
            } catch (Exception e) {
                stations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
    }
    public List<String> getCountries(){
        return radioRepository.getContrives();
    }
    public List<String> getLanguage(){
        return radioRepository.getLanguage();
    }
    public List<String> getTags(){
        return radioRepository.getTags();
    }
    public int getCountStations(){
        return radioRepository.getCountFilteredStations();
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
