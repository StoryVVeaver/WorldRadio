package by.roman.worldradio0.business_logic.view_models;

import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import by.roman.worldradio0.business_logic.data.repositories.interfaces.FavoriteRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.FilterRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.SettingsRepository;
import by.roman.worldradio0.business_logic.data.repositories.interfaces.UserRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AccountViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final SettingsRepository settingsRepository;
    private final FavoriteRepository favoriteRepository;
    private final FilterRepository filterRepository;

    @Inject
    public AccountViewModel(UserRepository userRepository,SettingsRepository settingsRepository,FavoriteRepository favoriteRepository,FilterRepository filterRepository){
        this.userRepository = userRepository;
        this.settingsRepository = settingsRepository;
        this.favoriteRepository = favoriteRepository;
        this.filterRepository = filterRepository;
    }

    public boolean reg(){
        return false;
    }
    public boolean enter(){
        userRepository.useradd();
    }
}
