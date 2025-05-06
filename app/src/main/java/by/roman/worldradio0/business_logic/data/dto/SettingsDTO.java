package by.roman.worldradio0.business_logic.data.dto;

import by.roman.worldradio0.business_logic.data.models.Settings;

public class SettingsDTO {
    private int userId;
    private int theme;
    private int mapEnabled;
    private int timerSeconds;
    private int timerDots;
    private int filterEnabled;

    public Settings toModel(){
        return  new Settings(userId,theme,mapEnabled,timerSeconds,timerDots,filterEnabled);
    }
    public SettingsDTO fromModel(Settings settings){
        SettingsDTO dto = new SettingsDTO();
        dto.userId = settings.getUserId();
        dto.theme = settings.getTheme();
        dto.mapEnabled = settings.getMapEnabled();
        dto.timerSeconds = settings.getTimerSeconds();
        dto.timerDots = settings.getTimerDots();
        dto.filterEnabled = settings.getFilterEnabled();
        return dto;
    }
    public int getId() {
        return userId;
    }
    public int getTheme() {
        return theme;
    }
    public int getMapEnabled() {
        return mapEnabled;
    }
    public int getTimerSeconds() {
        return timerSeconds;
    }
    public int getTimerDots() {
        return timerDots;
    }
    public int getFilterEnabled() {
        return filterEnabled;
    }
}
