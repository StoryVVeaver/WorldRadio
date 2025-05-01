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
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoriteViewModel extends ViewModel {
    private final RadioRepository radioRepository;
    private final FavoriteRepository favoriteRepository;
    private final MutableLiveData<UiState<List<RadioStation>>> favoriteStations = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private int currentPage = 0;
    private boolean isLastPage = false;
    private final int pageSize = 50;
    public boolean getIsLastPage() {
        return isLastPage;
    }
    @Inject
    public FavoriteViewModel(RadioRepository radioRepository, FavoriteRepository favoriteRepository) {
        this.radioRepository = radioRepository;
        this.favoriteRepository = favoriteRepository;
        loadStart();
    }
    public LiveData<UiState<List<RadioStation>>> getFavoriteStations(){
        return favoriteStations;
    }
    private void loadStart(){
        favoriteStations.setValue(UiState.loading());
        executor.execute(() -> {
            try {
                List<RadioStation> list = radioRepository.getFavoriteStations(currentPage,pageSize);
                list.isEmpty(); // вызов ошибки
                favoriteStations.postValue(UiState.success(list));
                currentPage++;
            } catch (Exception e) {
                favoriteStations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
    }
    public void loadNextPage() {
        if (isLastPage) return;
        executor.execute(() -> {
            try {
                List<RadioStation> list = radioRepository.getFavoriteStations(currentPage, pageSize);
                if (list.isEmpty()) {
                    isLastPage = true;
                } else {
                    List<RadioStation> currentList = favoriteStations.getValue() != null && favoriteStations.getValue().data != null
                            ? new ArrayList<>(favoriteStations.getValue().data)
                            : new ArrayList<>();
                    currentList.addAll(list);
                    favoriteStations.postValue(UiState.success(currentList));
                    currentPage++;
                }
            } catch (Exception e) {
                favoriteStations.postValue(UiState.error("Ошибка загрузки: " + e.getMessage()));
            }
        });
    }
    public void removeFromFavorite(String UUID){
        favoriteRepository.removeFromFavorite(UUID);
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
