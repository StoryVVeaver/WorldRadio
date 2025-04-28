package by.roman.radiomanager.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_settings")
public class Settings {
    @Id
    private Long id;
    private int theme;
    private int mapEnable;
    private int timerSeconds;
    private int timerDots;

    public Settings(Long id, int theme, int mapEnable, int timerSeconds, int timerDots) {
        this.id = id;
        this.theme = theme;
        this.mapEnable = mapEnable;
        this.timerSeconds = timerSeconds;
        this.timerDots = timerDots;
    }

    public Long getId() {
        return id;
    }

    public int getTheme() {
        return theme;
    }

    public int getMapEnable() {
        return mapEnable;
    }

    public int getTimerSeconds() {
        return timerSeconds;
    }

    public int getTimerDots() {
        return timerDots;
    }
}
