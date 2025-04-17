package by.roman.worldradio0.business_logic.data.models;

public class Settings {
    private final int userId;
    private final int theme;
    private final int mapEnabled;
    private final int timerSeconds;
    private final int timerDots;
    private final int filterEnabled;

    public Settings(int userId, int theme, int mapEnabled, int timerSeconds, int timerDots, int filterEnabled) {
        this.userId = userId;
        this.theme = theme;
        this.mapEnabled = mapEnabled;
        this.timerSeconds = timerSeconds;
        this.timerDots = timerDots;
        this.filterEnabled = filterEnabled;
    }

    // Getters
    public int getUserId() { return userId; }
    public int getTheme() { return theme; }
    public int getMapEnabled() { return mapEnabled; }
    public int getTimerSeconds() { return timerSeconds; }
    public int getTimerDots() { return timerDots; }
    public int getFilterEnabled() { return filterEnabled; }
}
