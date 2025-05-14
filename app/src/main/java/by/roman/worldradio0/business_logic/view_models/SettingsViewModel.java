package by.roman.worldradio0.business_logic.view_models;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.data.repositories.interfaces.SettingsRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {
    private SettingsRepository settingsRepository;

    @Inject
    public SettingsViewModel(SettingsRepository settingsRepository){
        this.settingsRepository = settingsRepository;
    }
}
