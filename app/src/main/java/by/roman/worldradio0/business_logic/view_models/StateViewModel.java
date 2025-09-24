package by.roman.worldradio0.business_logic.view_models;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StateViewModel extends ViewModel {
    private final RadioRepository repository;
    private boolean isExpanded;
    private final MutableLiveData<Boolean> mapFragmentOpen = new MutableLiveData<>();

    @Inject
    public StateViewModel(RadioRepository repository) {
        this.repository = repository;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }
    public void setMapOpen(boolean isOpen){
        Log.v("state", isOpen + " ");
        mapFragmentOpen.postValue(isOpen);
    }
    public boolean isExpanded() {
        return isExpanded;
    }
    public LiveData<Boolean> shouldShowPanel() {
        return repository.getShowPlayer();
    }
    public LiveData<Boolean> isMapOpen() {
        return mapFragmentOpen;
    }
}
