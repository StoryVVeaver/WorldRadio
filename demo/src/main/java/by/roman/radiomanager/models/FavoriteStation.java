package by.roman.radiomanager.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_favorites_stations")
public class FavoriteStation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Column(columnDefinition = "TEXT")
    private String uuid;

    public FavoriteStation(){}

    public FavoriteStation(Long userId, String uuid) {
        this.userId = userId;
        this.uuid = uuid;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUuid() {
        return uuid;
    }
}
