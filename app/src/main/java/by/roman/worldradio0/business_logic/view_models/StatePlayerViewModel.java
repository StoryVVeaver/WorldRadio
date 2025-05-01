package by.roman.worldradio0.business_logic.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.data.repositories.interfaces.RadioRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StatePlayerViewModel extends ViewModel {
    private final RadioRepository repository;
    private boolean isExpanded;

    @Inject
    public StatePlayerViewModel(RadioRepository repository) {
        this.repository = repository;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    public boolean isExpanded() {
        return isExpanded;
    }
    public LiveData<Boolean> shouldShowPanel() {
        return repository.getShowPlayer();
    }
}
